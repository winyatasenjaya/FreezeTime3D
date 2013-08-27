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

    public PicTakerModel() {

        SocketIOClient.connect(AsyncHttpClient.getDefaultInstance(), "http://192.168.10.162:7474", new ConnectCallback() {
            @Override
            public void onConnectCompleted(Exception e, SocketIOClient socketIOClient) {

                socketIOClient.addListener("someevent", new EventCallback() {
                    @Override
                    public void onEvent(JSONArray jsonArray, Acknowledge acknowledge) {

                    }
                });

            }
        });
    }

}
