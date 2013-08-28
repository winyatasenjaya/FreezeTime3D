package com.creativedrewy.framepicapp.model;

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

    /**
     * Constructor
     */
    public PicTakerModel(String ipAddress, IServerMessageHandler handler) {
        super(ipAddress, handler);

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

}
