package com.creativedrewy.framepicapp.model;

import android.app.Activity;

import com.koushikdutta.async.http.AsyncHttpClient;
import com.koushikdutta.async.http.socketio.Acknowledge;
import com.koushikdutta.async.http.socketio.ConnectCallback;
import com.koushikdutta.async.http.socketio.EventCallback;
import com.koushikdutta.async.http.socketio.JSONCallback;
import com.koushikdutta.async.http.socketio.SocketIOClient;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Model class for the PicTaker functionality
 */
public class PicTakerModel extends ModelBase {
    public final static String PICTAKER_HOST_IP_PREF = "pictakerHostIPPref";

    /**
     * Constructor
     */
    public PicTakerModel(String ipAddress, Activity handlerActivity) {
        super(ipAddress, handlerActivity);

        _roleString = "picTaker";
        _registerMessage = "RegisterPicTaker";

        initConnection();
    }

    /**
     * Send the message to the server that this app instance should be put in frame order
     */
    public void submitOrder() {
        sendAppDataEmit("RequestingFrameOrder");
    }

    /**
     * Send the message to the server that this app instance is ready to take its framepic
     * @param frameNumber This app's frame number
     */
    public void submitReady(int frameNumber) {
        sendAppDataEmit("PicTakingReady", String.valueOf(frameNumber));
    }

    /**
     * Send the message to the server that this app instance is un-registering itself from the PicTakers set
     * @param frameNumber This app's frame number, if it has one
     */
    public void submitUnRegister(int frameNumber) {
        sendAppDataEmit("UnRegisterPicTaker", String.valueOf(frameNumber));
    }

}
