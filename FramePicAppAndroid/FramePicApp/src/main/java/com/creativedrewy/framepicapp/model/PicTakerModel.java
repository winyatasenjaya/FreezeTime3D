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
 *
 */
public class PicTakerModel extends ModelBase {

    /**
     *
     * @param ipAddress
     * @param handler
     */
    public PicTakerModel(String ipAddress, IServerMessageHandler handler) {
        super(ipAddress, handler);

        _roleString = "picTaker";
        _registerMessage = "RegisterPicTaker";

        initConnection();
    }

    /**
     *
     */
    public void submitOrder() {
        sendAppDataEmit("RequestingFrameOrder");
    }

    /**
     *
     * @param frameNumber
     */
    public void submitReady(int frameNumber) {
        sendAppDataEmit("PicTakingReady", String.valueOf(frameNumber));
    }

}
