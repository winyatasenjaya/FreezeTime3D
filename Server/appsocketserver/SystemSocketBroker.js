
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
            this.processPicTakerMessages(socket, data.message); //TODO: We can now facilitate a payload in the received JSON
        }
    },

    /**
     * Process messages for the Master connection
     */
    processMasterMessages: function(socket, message) {
        switch (message) {
            case this.socketMessages.masterMessages.register:
                this.masterSocket = socket;
                this.masterSocket.write(this.socketMessages.masterMessages.registerResponse);
                this.websiteMessagingSocket.emit('systemMsg', {msg: this.socketMessages.masterMessages.register})

                console.log("Master connection has been established");
                break;
            case this.socketMessages.masterMessages.initPicTakerOrder:
                this.currentFrameNumber = 0;
                this.orderedSockets = {};

                for (var addressKey in this.picSockets) {
                    var currentSocket = this.picSockets[addressKey];
                    currentSocket.write(this.socketMessages.picTakerMessages.serverOrderingStart);
                }
                console.log("PicTaker ordering has been initiated");

                break;
            case this.socketMessages.masterMessages.startFrameCapture:
                console.log("Frame capturing beginning - get ready to freeze time!")
                for (var i = 0; i < this.currentFrameNumber; i++) {
                    var currentSocketInOrder = this.orderedSockets[i];
                    currentSocketInOrder.write(this.socketMessages.picTakerMessages.takeFramePic);
                }

                break;
            case this.socketMessages.masterMessages.resetSystem:
                for (var addressKey in this.picSockets) {
                    var currentSocket = this.picSockets[addressKey];
                    currentSocket.write(this.socketMessages.picTakerMessages.resetPicTaker);
                }
                console.log("System has been reset for next frame capture operation");

                break;
        }
    },

    /**
     * Process messages for PicTaker connections
     */
    processPicTakerMessages: function(socket, message) {
        switch (message) {
            case this.socketMessages.picTakerMessages.register:
                this.picSockets[socket.remoteAddress] = socket;
                socket.write(this.socketMessages.picTakerMessages.registerResponse);
                this.websiteMessagingSocket.emit('systemMsg', {msg: this.socketMessages.picTakerMessages.register})

                console.log("PicTaker has registered at address " + socket.remoteAddress);
                break;
            case this.socketMessages.picTakerMessages.requestFrameOrder:
                this.orderedSockets[this.currentFrameNumber] = socket;

                socket.write(this.socketMessages.picTakerMessages.frameOrderResponse + "::payload::" + this.currentFrameNumber);
                this.masterSocket.write(this.socketMessages.masterMessages.picTakerOrderUpdate);

                console.log("PicTaker at " + socket.remoteAddress + " is frame number " + this.currentFrameNumber);
                this.currentFrameNumber++;

                break;
            case this.socketMessages.picTakerMessages.picTakingReady:
                this.masterSocket.write(this.socketMessages.masterMessages.picTakerFrameReadyUpdate);
                console.log("PicTaker at " + socket.remoteAddress + " is ready for frame capture");

                break;
        }
    }

});