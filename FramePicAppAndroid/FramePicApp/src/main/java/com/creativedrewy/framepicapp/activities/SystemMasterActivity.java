package com.creativedrewy.framepicapp.activities;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.creativedrewy.framepicapp.R;

public class SystemMasterActivity extends Activity {
    private Button _masterRegisterButton;
    private Button _initOrderingButton;
    private Button _freezeTimeButton;
    private Button _resetSystemButton;
    private EditText _serverAddrEditText;
    private TextView _devicesReadyLabel;
    private TextView _devicesOrderedLabel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.system_master_layout);

        _masterRegisterButton = (Button) findViewById(R.id.masterRegisterButton);
        _initOrderingButton = (Button) findViewById(R.id.initOrderingButton);
        _freezeTimeButton = (Button) findViewById(R.id.freezeTimeButton);
        _resetSystemButton = (Button) findViewById(R.id.resetSystemButton);

        _serverAddrEditText = (EditText) findViewById(R.id.serverAddrEditText);
        _devicesReadyLabel = (TextView) findViewById(R.id.devicesReadyLabel);
        _devicesOrderedLabel = (TextView) findViewById(R.id.devicesOrderedLabel);

        _masterRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO: These only are enabled if connections are successfully made, of course
                _resetSystemButton.setEnabled(true);
                _initOrderingButton.setEnabled(true);
            }
        });

        _initOrderingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO: Only enable if ordering is successfully started
                _freezeTimeButton.setEnabled(true);
                _devicesOrderedLabel.setText("10 devices ordered");
            }
        });

        _freezeTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                _devicesReadyLabel.setText("20 devices ready");
            }
        });

        _resetSystemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

}
