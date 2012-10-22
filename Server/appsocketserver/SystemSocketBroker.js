
/**
 * Class that brokers all of the varied socket communications that come in from the mobile apps
 */
module.exports.SystemSocketBroker = new Class({
    masterSocket: null,
    picSockets: {},

    /**
     * Process all system messages from all connected apps
     * @param data JSON data that has been sent
     * @param socket socket connection who is originating data
     */
    processSystemMessages: function(data, socket) {
        if (data.role == "master") {
            this.processMasterMessages(data.message, socket);
        } else if (data.role == "picTaker") {
            this.processPicTakerMessages(data.message, socket);
        }
    },

    /**
     * Process messages for the master connection
     */
    processMasterMessages: function(message, socket) {
        switch (message) {
            case "register master":
                break;
            case "initiate taker order":
                break;
            case "start pic taking":
                break;
        }
    },

    /**
     * Process messages for pic taker connections
     */
    processPicTakerMessages: function(message, socket) {
        switch (message) {
            case "register pic taker":
                break;
            case "submit place in order":
                break;
            case "ready for pic taking":
                break;
            case "image upload complete":
                break;
        }
    }

});