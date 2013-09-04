package com.creativedrewy.framepicapp.model;

import android.app.Activity;
import android.util.Log;

import com.koushikdutta.async.http.AsyncHttpClient;
import com.koushikdutta.async.http.socketio.Acknowledge;
import com.koushikdutta.async.http.socketio.ConnectCallback;
import com.koushikdutta.async.http.socketio.EventCallback;
import com.koushikdutta.async.http.socketio.SocketIOClient;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Functionality related to all models in the application; provides uniform server interaction
 */
public class ModelBase {
    protected SocketIOClient _globalSocketIOClient;
    protected Activity _handlerActivity;
    protected String _serverIP;
    protected String _roleString;   //Derived classes will need to specify a unique role string
    protected String _registerMessage;
    protected JSONObject _serverReturnJSON = null;

    public String getServerIP() { return _serverIP; }
    public void setServerIP(String _serverIP) { this._serverIP = _serverIP; }

    /**
     * Constructor
     * @param ipAddress IP address to FT3D socket server
     * @param handlerActivity We have to pass in an activity so that we can broker thread stuff to UI
     */
    public ModelBase(String ipAddress, Activity handlerActivity){
        _serverIP = ipAddress;
        _handlerActivity = handlerActivity;
    }

    /**
     * Startup the connection to the FT3D socket server
     */
    public void initConnection() {
        SocketIOClient.connect(AsyncHttpClient.getDefaultInstance(), "http://" + _serverIP + ":7474", new ConnectCallback() {
            @Override
            public void onConnectCompleted(Exception e, SocketIOClient socketIOClient) {
                _globalSocketIOClient = socketIOClient;

                socketIOClient.on("ServerDataEmitEvent", new EventCallback() {
                    @Override
                    public void onEvent(JSONArray jsonArray, Acknowledge acknowledge) {
                        _serverReturnJSON = jsonArray.optJSONObject(0);

                        if (_serverReturnJSON != null) {
                            _handlerActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    //We also know that all passed in activities will implement the interface
                                    ((IServerMessageHandler) _handlerActivity).handleServerMessage(_serverReturnJSON.optString("msg"), _serverReturnJSON.optString("payload"));
                                }
                            });
                        }
                    }
                });

                //TODO: Need to handle the case when the app can't connect to the server
                //socketIOClient.setErrorCallback();

                sendAppDataEmit(_registerMessage);
            }
        });
    }

    /**
     * Send data to the socket server via a socket.io "emit" call
     * @param message Message data to send to the server
     * @param payload Any relevant payload data
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
     * Overloaded method to just specify message data
     * @param message
     */
    public void sendAppDataEmit(String message) {
        sendAppDataEmit(message, "");
    }
}
