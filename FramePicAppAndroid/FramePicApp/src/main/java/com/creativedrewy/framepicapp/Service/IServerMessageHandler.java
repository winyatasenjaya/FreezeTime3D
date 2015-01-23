package com.creativedrewy.framepicapp.Service;

/**
 * Interface for classes that want to work with message/payload data from the FT3D server
 */
public interface IServerMessageHandler {
    public void handleServerMessage(String message, String payload);
}
