package com.creativedrewy.framepicapp.activities;

import android.content.Context;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.creativedrewy.framepicapp.R;
import com.creativedrewy.framepicapp.model.IServerMessageHandler;
import com.creativedrewy.framepicapp.model.PicTakerModel;
import com.koushikdutta.async.http.AsyncHttpClient;

import java.io.IOException;

/**
 * Activity/view for apps that will operate as PicTakers
 */
public class PicTakerActivity extends Activity implements IServerMessageHandler {
    private Button _picRegisterButton;
    private Button _submitPicOrderButton;
    private Button _picReadyButton;
    private EditText _serverAddrEditText;
    private PicTakerModel _picTakerModel;
    private int _picFrameNumber = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pictaker_layout);

        _picRegisterButton = (Button) findViewById(R.id.picRegisterButton);
        _submitPicOrderButton = (Button) findViewById(R.id.submitPicOrderButton);
        _picReadyButton = (Button) findViewById(R.id.picReadyButton);
        _serverAddrEditText = (EditText) findViewById(R.id.serverAddrEditText);

        _picRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                _picTakerModel = new PicTakerModel("192.168.10.162", PicTakerActivity.this);
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
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        //TODO: Camera init actually happens after user clicks ready button
        //initializeCamera();
    }

    @Override
    protected void onStop() {
        super.onStop();
        //TODO: Release camera here
    }

    /**
     *
     */
    public void initializeCamera() {
        //TODO: Prolly wanna catch exceptions in case there is a camera init issue
        Camera systemCamera = Camera.open();

        Camera.Parameters params = systemCamera.getParameters();
        params.setPictureSize(1600, 1200);
        params.setPictureFormat(PixelFormat.JPEG);
        params.setJpegQuality(85);
        systemCamera.setParameters(params);

        CameraPreview cameraPreview = new CameraPreview(this, systemCamera);

        // Create our Preview view and set it as the content of our activity.
        //FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);

        // Calculating the width of the preview so it is proportional.
        //float widthFloat = (float) (deviceHeight) * 4 / 3;
        //int width = Math.round(widthFloat);

        // Resizing the LinearLayout so we can make a proportional preview. This
        // approach is not 100% perfect because on devices with a really small
        // screen the the image will still be distorted - there is place for
        // improvment.
        //LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(width, deviceHeight);
        //preview.setLayoutParams(layoutParams);

        // Adding the camera preview after the FrameLayout and before the button
        // as a separated element.
        //preview.addView(mPreview, 0);
    }

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
