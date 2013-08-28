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
 * Functionality related to all models in the application; provides uniform server interaction
 */
public class ModelBase {
    protected SocketIOClient _globalSocketIOClient;
    protected IServerMessageHandler _messageHandler;
    protected String _roleString;   //Derived classes will need to specify a unique role string
    protected String _registerMessage;

    /**
     * Constructor
     * @param ipAddress IP address to FT3D socket server
     * @param handler Class that will work with data coming back from the server
     */
    public ModelBase(String ipAddress, IServerMessageHandler handler){
        _messageHandler = handler;
    }

    /**
     * Startup the connection to the FT3D socket server
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
                            _messageHandler.handleServerMessage(outObj.optString("msg"), "");
                        }
                    }
                });

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
