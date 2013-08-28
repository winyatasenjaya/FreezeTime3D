package com.creativedrewy.framepicapp.model;

import android.util.Log;

import com.koushikdutta.async.http.AsyncHttpClient;
import com.koushikdutta.async.http.socketio.Acknowledge;
import com.koushikdutta.async.http.socketio.ConnectCallback;
import com.koushikdutta.async.http.socketio.EventCallback;
import com.koushikdutta.async.http.socketio.SocketIOClient;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 */
public class ModelBase {
    protected SocketIOClient _globalSocketIOClient;
    protected IServerMessageHandler _messageHandler;
    protected String _roleString;
    protected String _registerMessage;

    /**
     *
     * @param ipAddress
     * @param handler
     */
    public ModelBase(String ipAddress, IServerMessageHandler handler){
        _messageHandler = handler;
    }

    /**
     *
     */
    public void initConnection() {
        SocketIOClient.connect(AsyncHttpClient.getDefaultInstance(), "http://192.168.10.162:7474", new ConnectCallback() {
            @Override
            public void onConnectCompleted(Exception e, SocketIOClient socketIOClient) {
                _globalSocketIOClient = socketIOClient;

                socketIOClient.on("ServerDataEmitEvent", new EventCallback() {
                    @Override
                    public void onEvent(JSONArray jsonArray, Acknowledge acknowledge) {
                        JSONObject outObj = jsonArray.optJSONObject(0);

                        if (outObj != null) {
                            //TODO: Make this actually deal with the data from the server
                            _messageHandler.handleServerMessage(outObj.optString("msg"), "");
                        }
                    }
                });

                sendAppDataEmit(_registerMessage);
            }
        });
    }

    /**
     *
     * @param message
     * @param payload
     */
    public void sendAppDataEmit(String message, String payload) {
        JSONObject messageData = new JSONObject();

        try {
            messageData.put("role", _roleString);
            messageData.put("message", message);
            messageData.put("payload", payload);
        } catch (Exception e) {

        }

        _globalSocketIOClient.emit("AppDataEmitEvent", new JSONArray().put(messageData));
    }

    /**
     *
     * @param message
     */
    public void sendAppDataEmit(String message) {
        sendAppDataEmit(message, "");
    }
}
