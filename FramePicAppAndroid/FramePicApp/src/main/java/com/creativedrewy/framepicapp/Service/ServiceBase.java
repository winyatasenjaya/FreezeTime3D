package com.creativedrewy.framepicapp.service;

import android.app.Activity;
import android.widget.Toast;

import com.creativedrewy.framepicapp.R;
import com.koushikdutta.async.http.AsyncHttpClient;
import com.koushikdutta.async.http.socketio.Acknowledge;
import com.koushikdutta.async.http.socketio.ConnectCallback;
import com.koushikdutta.async.http.socketio.EventCallback;
import com.koushikdutta.async.http.socketio.SocketIOClient;
import com.koushikdutta.async.http.socketio.SocketIORequest;

import org.json.JSONArray;
import org.json.JSONObject;

import rx.Observable;
import rx.Subscriber;

/**
 * Functionality related to all models in the application; provides uniform server interaction
 */
public class ServiceBase {
    protected SocketIOClient _globalSocketIOClient;
    protected String _serverIP;
    protected String _roleString;   //Derived classes will need to specify a unique role string
    protected String _registerMessage;
    protected JSONObject _serverReturnJSON = null;

    public String getServerIP() { return _serverIP; }
    public void setServerIP(String _serverIP) { this._serverIP = _serverIP; }

    /**
     * Constructor
     * @param ipAddress IP address to FT3D socket server
     */
    public ServiceBase(String ipAddress){
        _serverIP = ipAddress;
    }

    /**
     * Startup the connection to the FT3D socket server
     */
    public Observable<String> initConnection() {
        SocketIORequest req = new SocketIORequest("http://" + _serverIP + ":7373");
        req.setTimeout(2500);

        return Observable.create((Subscriber<? super String> subscriber) -> {
            SocketIOClient.connect(AsyncHttpClient.getDefaultInstance(), req, (ex, socketIOClient) -> {
                if (ex != null) {
                    //_handlerActivity.runOnUiThread(() -> Toast.makeText(_handlerActivity, _handlerActivity.getString(R.string.server_connect_error_message), Toast.LENGTH_LONG).show());
                    subscriber.onError(new Throwable("Could not connect"));
                } else {
                    _globalSocketIOClient = socketIOClient;

                    socketIOClient.on("ServerDataEmitEvent", (jsonArray, acknowledge) -> {
                        _serverReturnJSON = jsonArray.optJSONObject(0);

                        if (_serverReturnJSON != null) {
                            //_handlerActivity.runOnUiThread(() -> ((IServerMessageHandler) _handlerActivity).handleServerMessage(_serverReturnJSON.optString("msg"), _serverReturnJSON.optString("payload")));
                            subscriber.onNext(_serverReturnJSON.optString("msg"));
                        }
                    });

                    sendAppDataEmit(_registerMessage);
                }
            });
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
        } catch (Exception e) { }

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
