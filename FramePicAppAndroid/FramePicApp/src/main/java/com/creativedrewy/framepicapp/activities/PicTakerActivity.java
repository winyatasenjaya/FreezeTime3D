package com.creativedrewy.framepicapp.activities;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.creativedrewy.framepicapp.R;

public class PicTakerActivity extends Activity {
    private Button _picRegisterButton;
    private Button _submitPicOrderButton;
    private Button _picReadyButton;
    private EditText _serverAddrEditText;

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
                //TODO: Only enable when appropriate
                _submitPicOrderButton.setEnabled(true);
            }
        });

        _submitPicOrderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO: Only enable when appropriate
                _picReadyButton.setEnabled(true);
            }
        });

        _picReadyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }
    
}
