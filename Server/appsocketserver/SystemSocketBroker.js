
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

    initialize: function() {
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
            this.processMasterMessages(data.message, socket);
        } else if (data.role == this.socketMessages.picTakerId) {
            this.processPicTakerMessages(data.message, socket);
        }
    },

    /**
     * Process messages for the Master connection
     */
    processMasterMessages: function(message, socket) {
        switch (message) {
            case this.socketMessages.masterMessages.register:
                this.masterSocket = socket;
                this.masterSocket.write(this.socketMessages.masterMessages.registerResponse);
                console.log("Master connection has been established");

                break;
            case this.socketMessages.masterMessages.initPicTakerOrder:
                this.currentFrameNumber = 0;
                this.orderedSockets = {};

                for (var addressKey in this.picSockets) {
                    var currentSocket = this.picSockets[addressKey];
                    currentSocket.write(this.socketMessages.picTakerMessages.serverOrderingStart);
                }
                console.log("PicTaker ordering has been initialized");

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
    processPicTakerMessages: function(message, socket) {
        switch (message) {
            case this.socketMessages.picTakerMessages.register:
                this.picSockets[socket.remoteAddress] = socket;
                socket.write(this.socketMessages.picTakerMessages.registerResponse);
                console.log("PicTaker has registered at address " + socket.remoteAddress);
                break;
            case this.socketMessages.picTakerMessages.requestFrameOrder:
                this.orderedSockets[this.currentFrameNumber] = socket;
                this.currentFrameNumber++;

                socket.write(this.socketMessages.picTakerMessages.frameOrderResponse);  //TODO: Payload send along too
                this.masterSocket.write(this.socketMessages.masterMessages.picTakerOrderUpdate);
                console.log("PicTaker at " + socket.remoteAddress + " is frame number " + this.currentFrameNumber - 1);
                break;
            case this.socketMessages.picTakerMessages.picTakingReady:
                this.masterSocket.write(this.socketMessages.masterMessages.picTakerFrameReadyUpdate);
                console.log("PicTaker at " + socket.remoteAddress + " is ready for frame capture");
                break;
        }
    }

});