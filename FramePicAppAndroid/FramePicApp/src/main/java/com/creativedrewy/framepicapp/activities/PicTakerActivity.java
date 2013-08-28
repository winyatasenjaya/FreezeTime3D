package com.creativedrewy.framepicapp.activities;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.creativedrewy.framepicapp.R;
import com.creativedrewy.framepicapp.model.IServerMessageHandler;
import com.creativedrewy.framepicapp.model.PicTakerModel;
import com.koushikdutta.async.http.AsyncHttpClient;

/**
 *
 */
public class PicTakerActivity extends Activity implements IServerMessageHandler {
    private Button _picRegisterButton;
    private Button _submitPicOrderButton;
    private Button _picReadyButton;
    private EditText _serverAddrEditText;
    private PicTakerModel _picTakerModel;

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
                //_picTakerModel.submitReady(0);  //TODO: Submit actual order number here
            }
        });
    }

    /**
     *
     * @param message
     * @param payload
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
//            _picFrameNumber = parseInt(event.messagePayload);
//            step3Container.enabled = true;
//
//            step2OrderButton.label = "Frame Number: " + _picFrameNumber;
//            step2OrderButton.enabled = false;
//
//            var now:Date = new Date();
//
//            _imgFileName = "frame_" + _picFrameNumber + "_" + now.time + ".jpg";
        } else if (message.equals("TakeFramePic")) {
            //takeSaveCameraPic();
        } else if (message.equals("ResetPicTaking")) {
            //TODO: Reset UI to redo the whole process
        }
    }
}
