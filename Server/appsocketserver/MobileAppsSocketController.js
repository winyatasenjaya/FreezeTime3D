
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
    socketTimeout: 5000,
    socketServer: null,
    policyRequestString: "<policy-file-request/>\0",
    policyFilePath: "./flashpolicy.xml",
    policyContents: "",
    connectedSockets: {},
    socketBroker: null,

    /**
     * @constructor
     */
    initialize: function(netLib, hostName, hostPort) {
        this.host = hostName;
        this.port = hostPort;

        this.socketServer = netLib.createServer(function(socket) {
            socket.setEncoding('utf8');

            socket.on(SocketEvents.CONNECT, this.onSocketConnect.bind(this, socket));
            socket.on(SocketEvents.DATA, this.onSocketData.bind(this, socket));
            socket.on(SocketEvents.END, this.onSocketEnd.bind(this, socket));
        }.bind(this));

        this.socketBroker = new (require("./SystemSocketBroker")).SystemSocketBroker();
    },

    /**
     * Start the socket server
     */
    startup: function() {
        require("fs").readFile(this.policyFilePath, "utf8", function(error, fileData) {
            this.policyContents = fileData;
            this.socketServer.listen(this.port, this.host);

            console.log("::: Socket server has been started on port " + this.port + " :::");
        }.bind(this));
    },

    /**
     * Initial connection from a mobile device
     */
    onSocketConnect: function(socket) {
        console.log("::: Socket connection established with device at " + socket.remoteAddress + " :::");
    },

    /**
     * Initial function to process socket data sent from the mobile apps
     */
    onSocketData: function(socket, data) {
        if (data == this.policyRequestString) {
            socket.write(this.policyContents + "\0");
        } else {
            var sentJSON = JSON.decode(data);
            this.socketBroker.processSystemMessages(data, socket);
        }

        console.log("Socket data received: ", data);
    },

    onSocketEnd: function(socket) {
        console.log("::: Socket connection ended with a device :::");

        //TODO: Not seeing much in the way of unique IDs for socket at this point...don't know how to remove from global collection
    }

});