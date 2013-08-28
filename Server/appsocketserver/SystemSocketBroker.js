
/**
 * Class that brokers all of the varied socket communications that come in from the mobile apps
 */
module.exports.SystemSocketBroker = new Class({
    masterSocket: null,
    picSockets: {},
    orderedSockets: {},
    currentFrameNumber: 0,
    messagesJSONPath: "./socketMessages.json",
    socketMessages: {},
    websiteMessagingSocket: null,

    /**
     * @constructor
     * @param websiteClientSocket A client websocket connection to the status website for update messaging
     */
    initialize: function(websiteClientSocket) {
        this.websiteMessagingSocket = websiteClientSocket;

        var msgContents = require("fs").readFileSync(this.messagesJSONPath, "utf8");
        this.socketMessages = JSON.decode(msgContents);
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
            console.log("--- GOT HERE ---");
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
                this.websiteMessagingSocket.emit('systemMsg', {msg: this.socketMessages.masterMessages.register});

                console.log("Master connection has been established");
                break;
            case this.socketMessages.masterMessages.initPicTakerOrder:
                this.currentFrameNumber = 0;
                this.orderedSockets = {};

                for (var addressKey in this.picSockets) {
                    var currentSocket = this.picSockets[addressKey];
                    this.sendAppSocketMessage(currentSocket, this.socketMessages.picTakerMessages.serverOrderingStart);
                }
                this.websiteMessagingSocket.emit('systemMsg', {msg: this.socketMessages.masterMessages.initPicTakerOrder});

                console.log("PicTaker ordering has been initiated");
                break;
            case this.socketMessages.masterMessages.startFrameCapture:
                console.log("Frame capturing beginning - get ready to freeze time!")
                for (var i = 0; i < this.currentFrameNumber; i++) {
                    var currentSocketInOrder = this.orderedSockets[i];
                    this.sendAppSocketMessage(currentSocketInOrder, this.socketMessages.picTakerMessages.takeFramePic);
                }

                break;
            case this.socketMessages.masterMessages.resetSystem:
                for (var addressKey in this.picSockets) {
                    var currentSocket = this.picSockets[addressKey];
                    this.sendAppSocketMessage(currentSocket, this.socketMessages.picTakerMessages.resetPicTaker);
                }

                console.log("System has been reset for next frame capture operation");
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
                //this.websiteMessagingSocket.emit('systemMsg', {msg: this.socketMessages.picTakerMessages.register});

                console.log("PicTaker has registered at address " + socket.remoteAddress);
                break;
            case this.socketMessages.picTakerMessages.requestFrameOrder:
                this.orderedSockets[this.currentFrameNumber] = socket;

                this.sendAppSocketMessage(socket, this.socketMessages.picTakerMessages.frameOrderResponse, this.currentFrameNumber);
                this.sendAppSocketMessage(this.masterSocket, this.socketMessages.masterMessages.picTakerOrderUpdate);
                this.websiteMessagingSocket.emit('systemMsg', {
                    msg: this.socketMessages.picTakerMessages.requestFrameOrder,
                    payload: this.currentFrameNumber
                });

                console.log("PicTaker at " + socket.remoteAddress + " is frame number " + this.currentFrameNumber);
                this.currentFrameNumber++;

                break;
            case this.socketMessages.picTakerMessages.picTakingReady:
                this.sendAppSocketMessage(this.masterSocket, this.socketMessages.masterMessages.picTakerFrameReadyUpdate);
                this.websiteMessagingSocket.emit('systemMsg', {
                    msg: this.socketMessages.picTakerMessages.picTakingReady,
                    payload: receivedPayload
                });

                console.log("PicTaker at " + socket.remoteAddress + " is ready for frame capture");
                break;
        }
    },

    /**
     * Send a message to a socket-connected app instance. Able to send message string and payload data.
     */
    sendAppSocketMessage: function(destSocket, messageString, payloadData) {
        destSocket.emit("ServerDataEmitEvent", {msg: messageString, payload: payloadData});
    }

});