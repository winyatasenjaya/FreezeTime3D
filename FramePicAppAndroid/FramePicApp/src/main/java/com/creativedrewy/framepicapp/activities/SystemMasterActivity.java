package com.creativedrewy.framepicapp.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.creativedrewy.framepicapp.BuildConfig;
import com.creativedrewy.framepicapp.R;
import com.creativedrewy.framepicapp.Service.IServerMessageHandler;
import com.creativedrewy.framepicapp.Service.SystemMasterService;

/**
 * Activity/view for the app that will act as the FT3D master
 */
public class SystemMasterActivity extends Activity implements IServerMessageHandler {
    private Button _masterRegisterButton;
    private Button _initOrderingButton;
    private Button _freezeTimeButton;
    private Button _resetSystemButton;
    private EditText _serverAddrEditText;
    private TextView _devicesReadyLabel;
    private TextView _devicesOrderedLabel;
    private SystemMasterService _masterModel;
    private int _orderedDevices = 0;
    private int _readyDevices = 0;
    private SharedPreferences _appPrefs;

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
                String ipAddr = _serverAddrEditText.getText().toString();

                SharedPreferences.Editor editor = _appPrefs.edit();
                editor.putString(SystemMasterService.SYSTEM_HOST_IP_PREF, ipAddr);
                editor.commit();

                _masterModel = new SystemMasterService(ipAddr, SystemMasterActivity.this);

                InputMethodManager inputMethodManager = (InputMethodManager)  SystemMasterActivity.this.getSystemService(Activity.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(SystemMasterActivity.this.getCurrentFocus().getWindowToken(), 0);
            }
        });

        _initOrderingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                _masterModel.sendInitOrder();

                _initOrderingButton.setText("Ordering...");
                _initOrderingButton.setEnabled(false);
            }
        });

        _freezeTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                _masterModel.sendFreezeTime();

                _freezeTimeButton.setText("Boom!");
                _freezeTimeButton.setEnabled(false);
            }
        });

        _resetSystemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetFreezeOperation();
            }
        });

        _appPrefs = getPreferences(MODE_PRIVATE);
    }

    @Override
    protected void onStart() {
        super.onStart();

        String ipString = _appPrefs.getString(SystemMasterService.SYSTEM_HOST_IP_PREF, "");
        if (!ipString.equals("")) {
            _serverAddrEditText.setText(ipString);
        }
    }

    /**
     * Reset the system for the next round
     */
    public void resetFreezeOperation() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to reset?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                _masterModel.sendResetSystem();

                _initOrderingButton.setText(getString(R.string.init_ordering_label));
                _initOrderingButton.setEnabled(true);

                _freezeTimeButton.setText(getString(R.string.freeze_time_button_text));
                _freezeTimeButton.setEnabled(false);

                _devicesOrderedLabel.setText("");
                _devicesReadyLabel.setText("");

                _orderedDevices = 0;
                _readyDevices = 0;
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) { }
        });

        builder.show();
    }

    /**
     * Handle message/payload data from the FT3D server; implemented from the interface
     */
    @Override
    public void handleServerMessage(String message, String payload) {
        if (message.equals(BuildConfig.master_registerResponse)) {
            _masterRegisterButton.setEnabled(false);
            _masterRegisterButton.setText("Is master");

            _resetSystemButton.setEnabled(true);
            _initOrderingButton.setEnabled(true);
        } else if (message.equals(BuildConfig.master_picTakerOrderUpdate)) {
            _orderedDevices++;
            _devicesOrderedLabel.setText(_orderedDevices + " device(s) ordered");
        } else if (message.equals(BuildConfig.master_picTakerFrameReadyUpdate)) {
            _readyDevices++;
            _devicesReadyLabel.setText(_readyDevices + " device(s) ready");

            if (_readyDevices == _orderedDevices) {
                _freezeTimeButton.setEnabled(true);
            } else {
                _freezeTimeButton.setEnabled(false);
            }
        }
        //else if (message.equals("PicTakerHasUnRegistered")) {
        //    //TODO: Check the payload to see if the PicTaker was ordered and/or ready
        //    //TODO: But note, right now, nothing is getting sent to the master app when a PicTaker un-registers
        //}
    }
}
