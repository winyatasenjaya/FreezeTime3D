package com.creativedrewy.framepicapp.model;

import android.app.Activity;

import com.creativedrewy.framepicapp.BuildConfig;

/**
 * Model functionality for the app that will act as the system master
 */
public class SystemMasterModel extends ModelBase {
    public final static String SYSTEM_HOST_IP_PREF = "systemHostIPPref";

    /**
     * Constructor
     */
    public SystemMasterModel(String ipAddress, Activity handlerActivity) {
        super(ipAddress, handlerActivity);

        _roleString = "master";
        _registerMessage = BuildConfig.master_register;

        initConnection();
    }

    /**
     * Send the message to the server that all the PicTakers should start ordering
     */
    public void sendInitOrder() {
        sendAppDataEmit(BuildConfig.master_initPicTakerOrder);
    }

    /**
     * Send the message to the server to take all the frame pics
     */
    public void sendFreezeTime() {
        sendAppDataEmit(BuildConfig.master_startFrameCapture);
    }

    /**
     * Send the message to the server to reset the system for additional pic taking
     */
    public void sendResetSystem() {
        sendAppDataEmit(BuildConfig.master_resetSystem);
    }
}
