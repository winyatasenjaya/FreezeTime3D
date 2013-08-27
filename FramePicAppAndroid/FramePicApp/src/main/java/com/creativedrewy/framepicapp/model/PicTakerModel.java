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
public class PicTakerModel {
    private SocketIOClient _globalSocketIOClient;

    public PicTakerModel() {

        SocketIOClient.connect(AsyncHttpClient.getDefaultInstance(), "http://192.168.10.162:7474", new ConnectCallback() {
            @Override
            public void onConnectCompleted(Exception e, SocketIOClient socketIOClient) {
                //_globalSocketIOClient = socketIOClient;

                socketIOClient.addListener("ServerDataEmitEvent", new EventCallback() {
                    @Override
                    public void onEvent(JSONArray jsonArray, Acknowledge acknowledge) {

                    }
                });
            }
        });
    }

    /**
     *
     */
    public void submitOrder() {
        //_globalSocketIOClient.emit("AppDataEmitEvent", {role: "picTakerRole", message: "RequestingFrameOrder", payload: ""});
    }

    /**
     *
     * @param frameNumber
     */
    public void submitReady(int frameNumber) {
        //_globalSocketIOClient.emit("AppDataEmitEvent", {role: "picTakerRole", message: "PicTakingReady", payload: frameNumber});
    }

}
