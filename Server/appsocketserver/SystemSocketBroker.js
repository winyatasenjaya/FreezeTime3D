
/**
 * Class that brokers all of the varied socket communications that come in from the mobile apps
 */
module.exports.SystemSocketBroker = new Class({
    masterSocket: null,
    picSockets: {},
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
     * Process messages for the master connection
     */
    processMasterMessages: function(message, socket) {
        switch (message) {
            case this.socketMessages.masterMessages.register:
                break;
            case this.socketMessages.masterMessages.initPicTakerOrder:
                break;
            case this.socketMessages.masterMessages.startFrameCapture:
                break;
        }
    },

    /**
     * Process messages for pic taker connections
     */
    processPicTakerMessages: function(message, socket) {
        switch (message) {
            case this.socketMessages.picTakerMessages.register:
                this.picSockets[socket.remoteAddress] = socket;
                socket.write(this.socketMessages.picTakerMessages.registerResponse);
                console.log("Pic Taker has registered at address " + socket.remoteAddress + "");
                break;
            case this.socketMessages.picTakerMessages.submitOrder:
                break;
            case this.socketMessages.picTakerMessages.picTakingReady:
                break;
            case this.socketMessages.picTakerMessages.imgUploadReady:
                break;
        }
    }

});