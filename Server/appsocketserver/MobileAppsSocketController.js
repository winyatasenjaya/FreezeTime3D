
var SocketEvents = SocketEvents || {};
SocketEvents.CONNECT = 'connect';
SocketEvents.DATA = 'data';
SocketEvents.END = 'end';
SocketEvents.TIMEOUT = 'timeout';

/**
 * Socket controlling class to interact with mobile devices; first step in brokering all socket communications
 */
module.exports.MobileAppsSocketController = new Class({
    host: "",
    port: -1,
    socketServer: null,
    policyRequestString: "<policy-file-request/>\0",
    policyFilePath: "./flashpolicy.xml",
    policyContents: "",
    connectedSockets: {},
    socketBroker: null,

    /**
     * @constructor
     * @param netLib Nodejs net library
     * @param hostName Socket server host name/IP
     * @param hostPort Socket server host port
     * @param websiteMessagingSocket A client websocket connection to the status website for update messaging
     */
    initialize: function(netLib, hostName, hostPort, websiteMessagingSocket) {
        this.host = hostName;
        this.port = hostPort;

        this.socketBroker = new (require("./SystemSocketBroker")).SystemSocketBroker(websiteMessagingSocket);
    },

    /**
     * Start the socket server
     */
    startup: function() {
        require("fs").readFile(this.policyFilePath, "utf8", function(error, fileData) {
            this.policyContents = fileData;

            var io = require('socket.io').listen(this.port);
            io.sockets.on('connection', function (socket) {

                socket.on('AppDataEmitEvent', function (data) {
                    this.onSocketData(socket, data);
                }.bind(this));

                console.log("Socket connection established with device at " + socket.handshake.address.address + "");
            }.bind(this));

            console.log("FreezeTime3D socket server has been started on port " + this.port + "");
        }.bind(this));
    },

    /**
     * Initial function to process socket data sent from the mobile apps
     */
    onSocketData: function(socket, data) {
        socket.remoteAddress = socket.handshake.address.address;
        this.socketBroker.processSystemMessages(data, socket);
    },

    onSocketEnd: function(socket) {
        //TODO: Not seeing much in the way of unique IDs for socket at this point...don't know how to
        //TODO: remove from global collection, update website, and so on
        console.log("Socket connection ended with a device");
    }

});