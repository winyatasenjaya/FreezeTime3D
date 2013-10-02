
/**
 * Class that brokers all of the varied socket communications that come in from the mobile apps.
 * -Did this need to be a mootools object? No. But I just can't resist the syntax. This class was
 * -originally part of a different implementation of the socket stuff, which you can see in the
 * -commit history.
 */
module.exports = new Class({
    masterSocket: null,
    picSockets: {},
    orderedSockets: {},
    currentFrameNumber: 0,
    messagesJSONPath: "./socketMessages.json",
    socketMessages: {},
    websiteMessagingSocket: null,
    messageEmitter: null,

    /**
     * @constructor
     * @param websiteClientSocket A client websocket connection to the status website for update messaging
     */
    initialize: function() {
        var msgContents = require("fs").readFileSync(this.messagesJSONPath, "utf8");
        this.socketMessages = JSON.decode(msgContents);

        var Emitter = require('events').EventEmitter;
        this.messageEmitter = new Emitter();
    },

    /**
     * Wrapper event handler function so this class can talk back to its consumer
     */
    on: function(event, func) {
        this.messageEmitter.on(event, func);
    },

    /**
     * Process all system messages from all connected apps
     * @param data JSON data that has been sent
     * @param socket socket connection who is originating data
     */
    processSystemMessages: function(data, socket) {
        if (data.role == this.socketMessages.masterId) {
            this.processMasterMessages(socket, data.message);
        } else if (data.role == this.socketMessages.picTakerId) {
            this.processPicTakerMessages(socket, data.message, data.payload);
        }
    },

    /**
     * Process messages for the Master connection
     */
    processMasterMessages: function(socket, message) {
        switch (message) {
            case this.socketMessages.masterMessages.register:
                this.masterSocket = socket;
                this.sendAppSocketMessage(this.masterSocket, this.socketMessages.masterMessages.registerResponse);
                this.sendWebsiteClientMessage("registerMasterFC");

                console.log(":::Master::: Connection has been established");
                break;
            case this.socketMessages.masterMessages.initPicTakerOrder:
                this.currentFrameNumber = 0;
                this.orderedSockets = {};

                for (var addressKey in this.picSockets) {
                    var currentSocket = this.picSockets[addressKey];
                    this.sendAppSocketMessage(currentSocket, this.socketMessages.picTakerMessages.serverOrderingStart);
                }

                this.sendWebsiteClientMessage("initPicTakerOrderFC");
                this.messageEmitter.emit("onSetupSessionFileSystem");   //Send the event to setup the server filesystem

                console.log(":::Master::: PicTaker ordering has been initiated");
                break;
            case this.socketMessages.masterMessages.startFrameCapture:
                console.log(":::Master::: Frame capturing beginning - get ready to freeze time!");
                for (var i = 0; i < this.currentFrameNumber; i++) {
                    var currentSocketInOrder = this.orderedSockets[i];
                    this.sendAppSocketMessage(currentSocketInOrder, this.socketMessages.picTakerMessages.takeFramePic);
                }
                this.sendWebsiteClientMessage("freezeTimeInitiatedFC")

                break;
            case this.socketMessages.masterMessages.resetSystem:
                orderedSockets = {};
                currentFrameNumber = 0;

                for (var addressKey in this.picSockets) {
                    var currentSocket = this.picSockets[addressKey];
                    this.sendAppSocketMessage(currentSocket, this.socketMessages.picTakerMessages.resetPicTaker);
                }

                this.sendWebsiteClientMessage("systemResetFC")
                console.log(":::Master::: System has been reset for next frame capture operation");
                break;
        }
    },

    /**
     * Process messages for PicTaker connections
     */
    processPicTakerMessages: function(socket, message, receivedPayload) {
        switch (message) {
            case this.socketMessages.picTakerMessages.register:
                this.picSockets[socket.remoteAddress] = socket;
                this.sendAppSocketMessage(socket, this.socketMessages.picTakerMessages.registerResponse);
                this.sendWebsiteClientMessage("picTakerHasRegisteredFC");

                console.log(":::PT::: Registered at address " + socket.remoteAddress);
                break;
            case this.socketMessages.picTakerMessages.requestFrameOrder:
                this.orderedSockets[this.currentFrameNumber] = socket;

                this.sendAppSocketMessage(socket, this.socketMessages.picTakerMessages.frameOrderResponse, this.currentFrameNumber);
                this.sendAppSocketMessage(this.masterSocket, this.socketMessages.masterMessages.picTakerOrderUpdate);
                this.sendWebsiteClientMessage("picTakerHasOrderedFC", this.currentFrameNumber);

                console.log(":::PT::: PicTaker at " + socket.remoteAddress + " is frame number " + this.currentFrameNumber);
                this.currentFrameNumber++;

                break;
            case this.socketMessages.picTakerMessages.picTakingReady:
                this.sendAppSocketMessage(this.masterSocket, this.socketMessages.masterMessages.picTakerFrameReadyUpdate);
                this.sendWebsiteClientMessage("picTakerIsReadyFC", receivedPayload);

                console.log(":::PT::: PicTaker at " + socket.remoteAddress + " is ready for frame capture");
                break;
        }
    },

    /**
     * Send a message to the status update website so it can reflect what is going on with the system
     */
    sendWebsiteClientMessage: function(msgString, payloadData) {
        if (this.websiteMessagingSocket) {
            this.websiteMessagingSocket.emit("update", {msg: msgString, payload: payloadData});
        }
    },

    /**
     * Send a message to a socket-connected app instance. Able to send message string and payload data.
     */
    sendAppSocketMessage: function(destSocket, messageString, payloadData) {
        destSocket.emit("ServerDataEmitEvent", {msg: messageString, payload: payloadData});
    }

});