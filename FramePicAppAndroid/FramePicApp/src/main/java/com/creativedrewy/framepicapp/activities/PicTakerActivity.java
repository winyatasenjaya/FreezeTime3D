package com.creativedrewy.framepicapp.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.media.ExifInterface;
import android.os.Bundle;
import android.app.Activity;
import android.os.Environment;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.creativedrewy.framepicapp.R;
import com.creativedrewy.framepicapp.model.IServerMessageHandler;
import com.creativedrewy.framepicapp.model.PicTakerModel;
import com.creativedrewy.framepicapp.model.SystemMasterModel;
import com.koushikdutta.async.future.Future;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.async.http.AsyncHttpClient;
import com.koushikdutta.async.http.AsyncHttpPost;
import com.koushikdutta.async.http.AsyncHttpRequest;
import com.koushikdutta.async.http.AsyncHttpResponse;
import com.koushikdutta.async.http.MultipartFormDataBody;
import com.koushikdutta.async.http.Part;
import com.koushikdutta.async.http.callback.HttpConnectCallback;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Activity/view for apps that will operate as PicTakers
 */
public class PicTakerActivity extends Activity implements IServerMessageHandler {
    private Button _picRegisterButton;
    private Button _submitPicOrderButton;
    private Button _picReadyButton;
    private EditText _serverAddrEditText;
    private RelativeLayout _registerStepContainer;
    private RelativeLayout _submitOrderStepContainer;
    private RelativeLayout _readyStepContainer;
    private PicTakerModel _picTakerModel;
    private int _picFrameNumber = -1;
    private SharedPreferences _appPrefs;
    private Camera _systemCamera = null;
    private CameraPreview _cameraPreviewWindow;
    private ProgressDialog _uploadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pictaker_layout);

        _picRegisterButton = (Button) findViewById(R.id.picRegisterButton);
        _submitPicOrderButton = (Button) findViewById(R.id.submitPicOrderButton);
        _picReadyButton = (Button) findViewById(R.id.picReadyButton);
        _serverAddrEditText = (EditText) findViewById(R.id.serverAddrEditText);

        _registerStepContainer = (RelativeLayout) findViewById(R.id.registerStepContainer);
        _submitOrderStepContainer = (RelativeLayout) findViewById(R.id.submitOrderStepContainer);
        _readyStepContainer = (RelativeLayout) findViewById(R.id.readyStepContainer);

        _picRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String ipAddr = _serverAddrEditText.getText().toString();

                SharedPreferences.Editor editor = _appPrefs.edit();
                editor.putString(PicTakerModel.PICTAKER_HOST_IP_PREF, ipAddr);
                editor.commit();

                _picTakerModel = new PicTakerModel(ipAddr, PicTakerActivity.this);

                InputMethodManager inputMethodManager = (InputMethodManager)  PicTakerActivity.this.getSystemService(Activity.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(PicTakerActivity.this.getCurrentFocus().getWindowToken(), 0);
            }
        });

        _submitPicOrderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                _picTakerModel.submitOrder();
            }
        });

        _picReadyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                _picTakerModel.submitReady(_picFrameNumber);

                _registerStepContainer.setVisibility(View.GONE);
                _submitOrderStepContainer.setVisibility(View.GONE);
                _readyStepContainer.setVisibility(View.GONE);

                initializeCamera();
            }
        });

        _appPrefs = getPreferences(MODE_PRIVATE);
    }

    @Override
    protected void onStart() {
        super.onStart();

        String ipString = _appPrefs.getString(PicTakerModel.PICTAKER_HOST_IP_PREF, "");
        if (!ipString.equals("")) {
            _serverAddrEditText.setText(ipString);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
        //TODO: Also, need to kill any open socket connections before leaving?

        if (_systemCamera != null) {
            _systemCamera.release();
        }
    }

    /**
     *
     */
    public void initializeCamera() {
        try {
            _systemCamera = Camera.open();
            _systemCamera.setDisplayOrientation(90);

            Camera.Parameters params = _systemCamera.getParameters();
            params.setPictureSize(2560, 1920);
            params.setPictureFormat(PixelFormat.JPEG);
            params.setJpegQuality(85);
            _systemCamera.setParameters(params);

            _cameraPreviewWindow = new CameraPreview(this, _systemCamera);

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            _cameraPreviewWindow.setLayoutParams(layoutParams);

            LinearLayout mainLayout = (LinearLayout) findViewById(R.id.picTakerMainLinearLayout);
            mainLayout.addView(_cameraPreviewWindow);
        } catch (Exception ex) {
            Toast.makeText(this, "Could not init camera. Will not capture frame.", Toast.LENGTH_LONG).show();
        }
    }

    private byte[] _imageBytes;

    private Camera.PictureCallback _pictureCallback = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] bytes, Camera camera) {
            _imageBytes = bytes;
            String fileName = "FT3D_" + _picFrameNumber + "_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()).toString() + ".jpg";

            File sdRoot = Environment.getExternalStorageDirectory();
            String dir = "/FT3D/";

            // Creating the directory where to save the image. Sadly in older
            // version of Android we can not get the Media catalog name
            File mkDir = new File(sdRoot, dir);
            mkDir.mkdirs();

            // Main file where to save the data that we recive from the camera
            File pictureFile = new File(sdRoot, dir + fileName);

            try {
                FileOutputStream purge = new FileOutputStream(pictureFile);
                purge.write(bytes);
                purge.close();
            } catch (FileNotFoundException e) {
                Log.d("DG_DEBUG", "File not found: " + e.getMessage());
            } catch (IOException e) {
                Log.d("DG_DEBUG", "Error accessing file: " + e.getMessage());
            }

            AsyncHttpPost reqPost = new AsyncHttpPost("http://" + _picTakerModel.getServerIP() + ":7373/fileUpload");
            MultipartFormDataBody body = new MultipartFormDataBody();
            body.addFilePart("framePic", pictureFile);
            body.addStringPart("frameNumber", String.valueOf(_picFrameNumber));
            reqPost.setBody(body);

            Future<String> uploadReturn = AsyncHttpClient.getDefaultInstance().executeString(reqPost);
            uploadReturn.setCallback(new FutureCallback<String>() {
                @Override
                public void onCompleted(Exception e, String s) {
                    if (_uploadingDialog != null) {
                        _uploadingDialog.cancel();
                    }

                    if (_systemCamera != null) {
                        _systemCamera.release();
                    }

                    LinearLayout mainLayout = (LinearLayout) findViewById(R.id.picTakerMainLinearLayout);
                    mainLayout.removeView(_cameraPreviewWindow);

                    ImageView newImage = new ImageView(PicTakerActivity.this);
                    Bitmap bmp = BitmapFactory.decodeByteArray(_imageBytes, 0, _imageBytes.length);

                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                    newImage.setLayoutParams(layoutParams);
                    newImage.setImageBitmap(bmp);

                    _picReadyButton.setVisibility(View.GONE);
                    _readyStepContainer.addView(newImage);

                    _registerStepContainer.setVisibility(View.VISIBLE);
                    _submitOrderStepContainer.setVisibility(View.VISIBLE);
                    _readyStepContainer.setVisibility(View.VISIBLE);
                }
            });

            try {
                _uploadingDialog = ProgressDialog.show(PicTakerActivity.this, "Uploading Frame", "Uploading your frame to FT3D server.");
                uploadReturn.get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
//            catch (TimeoutException e) {
//                e.printStackTrace();
//            }
        }
    };

    /**
     *
     */
    public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
        private SurfaceHolder _surfaceHolder;
        private Camera _camera;

        public CameraPreview(Context context, Camera camera) {
            super(context);
            _camera = camera;

            _surfaceHolder = getHolder();
            _surfaceHolder.addCallback(this);
            _surfaceHolder.setFixedSize(100, 100);
        }

        @Override
        public void surfaceCreated(SurfaceHolder surfaceHolder) {
            try {
                _camera.setPreviewDisplay(_surfaceHolder);
                _camera.startPreview();
            } catch (IOException e) {
                Log.d("DG_DEBUG", "Error setting camera preview: " + e.getMessage());
            }
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            //TODO: Possibly handle device rotation here, but will have to investigate
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder surfaceHolder) { }
    }

    /**
     * Handle message/payload data from the FT3D server; implemented from the interface
     */
    @Override
    public void handleServerMessage(String message, String payload) {
        if (message.equals("RegisterPicTakerResponse")) {
            _picRegisterButton.setText("Registered!");
            _picRegisterButton.setEnabled(false);

            _submitPicOrderButton.setText("Waiting for master...");
        } else if (message.equals("ServerReadyForOrder")) {
            _submitPicOrderButton.setEnabled(true);
            _submitPicOrderButton.setText("Submit Order");
        } else if (message.equals("FrameOrderResponse")) {
            _picFrameNumber = Integer.valueOf(payload);
            _picReadyButton.setEnabled(true);

            _submitPicOrderButton.setText("Frame Number: " + _picFrameNumber);
            _submitPicOrderButton.setEnabled(false);
        } else if (message.equals("TakeFramePic")) {
            if (_systemCamera != null) {
                _systemCamera.takePicture(null, null, _pictureCallback);
            } else {
                //TODO: Give a message to the user how they aren't taking a pic?
            }
        } else if (message.equals("ResetPicTaking")) {
            //TODO: Reset UI to redo the whole process
        }
    }
}
