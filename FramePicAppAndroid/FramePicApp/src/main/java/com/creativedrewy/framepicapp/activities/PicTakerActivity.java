package com.creativedrewy.framepicapp.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.media.ExifInterface;
import android.os.Bundle;
import android.app.Activity;
import android.os.Environment;
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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.creativedrewy.framepicapp.R;
import com.creativedrewy.framepicapp.model.IServerMessageHandler;
import com.creativedrewy.framepicapp.model.PicTakerModel;
import com.creativedrewy.framepicapp.model.SystemMasterModel;
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
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
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

                //ProgressDialog dialog = ProgressDialog.show(PicTakerActivity.this, "Uploading Frame", "Uploading your frame to FT3D server.");

                //TODO: Add an error handler to interface so we can do something if user mis types IP
                _picTakerModel = new PicTakerModel(ipAddr, PicTakerActivity.this);

                //InputMethodManager inputMethodManager = (InputMethodManager)  PicTakerActivity.this.getSystemService(Activity.INPUT_METHOD_SERVICE);
                //inputMethodManager.hideSoftInputFromWindow(PicTakerActivity.this.getCurrentFocus().getWindowToken(), 0);
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

                //TODO: This is where we turn the camera viewport for pic taking
                _registerStepContainer.setVisibility(View.GONE);
                _submitOrderStepContainer.setVisibility(View.GONE);
                _readyStepContainer.setVisibility(View.GONE);

                //TODO: Camera init actually happens after user clicks ready button
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
        //TODO: Release camera here
        //TODO: Also, need to kill any open socket connections before leaving

        if (_systemCamera != null) {
            _systemCamera.release();
        }
    }

    /**
     *
     */
    public void initializeCamera() {
        //TODO: Prolly wanna catch exceptions in case there is a camera init issue
        _systemCamera = Camera.open();
        _systemCamera.setDisplayOrientation(90);

        Camera.Parameters params = _systemCamera.getParameters();
        params.setPictureSize(2560, 1920);
        params.setPictureFormat(PixelFormat.JPEG);
        params.setJpegQuality(85);
        _systemCamera.setParameters(params);

        CameraPreview cameraPreview = new CameraPreview(this, _systemCamera);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        cameraPreview.setLayoutParams(layoutParams);

        LinearLayout mainLayout = (LinearLayout) findViewById(R.id.picTakerMainLinearLayout);
        mainLayout.addView(cameraPreview);

        cameraPreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                _systemCamera.takePicture(null, null, _pictureCallback);
            }
        });
    }

    private Camera.PictureCallback _pictureCallback = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] bytes, Camera camera) {
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

            Toast.makeText(PicTakerActivity.this, "Picture successfully captured", Toast.LENGTH_LONG).show();

            AsyncHttpPost reqPost = new AsyncHttpPost("http://" + _picTakerModel.getServerIP() + ":7373/fileUpload");
            MultipartFormDataBody body = new MultipartFormDataBody();
            body.addFilePart("framePic", pictureFile);
            body.addStringPart("info", "{frameNumber: " + _picFrameNumber + "}");
            reqPost.setBody(body);

            Future<String> uploadReturn = AsyncHttpClient.getDefaultInstance().executeString(reqPost, new AsyncHttpClient.StringCallback() {
                @Override
                public void onCompleted(Exception e, AsyncHttpResponse asyncHttpResponse, String s) {
                    //TODO: This shows no matter what, want to actually catch errors
                    Toast.makeText(PicTakerActivity.this, "File Uploaded!", Toast.LENGTH_LONG).show();
                }
            });

            try {
                String serverMessage = uploadReturn.get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
//            catch (TimeoutException e) {
//                e.printStackTrace();
//            }

            // Adding Exif data for the orientation. For some strange reason the
            // ExifInterface class takes a string instead of a file.
//            try {
//                exif = new ExifInterface("/sdcard/" + dir + fileName);
//                exif.setAttribute(ExifInterface.TAG_ORIENTATION, "" + orientation);
//                exif.saveAttributes();
//            } catch (IOException e) {
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

//            var now:Date = new Date();
//            _imgFileName = "frame_" + _picFrameNumber + "_" + now.time + ".jpg";
        } else if (message.equals("TakeFramePic")) {
            //takeSaveCameraPic();
        } else if (message.equals("ResetPicTaking")) {
            //TODO: Reset UI to redo the whole process
        }
    }
}
