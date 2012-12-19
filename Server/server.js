
require("mootools");
var net = require("net");

var webserver = require("./webserver/webserver.js");
var websocketClient = require('socket.io-client');
var websiteMessagingSocket = websocketClient.connect(process.env.IP, { port: 7373 });


var ControllerModule = require("./appsocketserver/MobileAppsSocketController");

var socketHost = process.env.IP;
var socketPort = 7474;
var socketController = new ControllerModule.MobileAppsSocketController(net, socketHost, socketPort, websiteMessagingSocket);
socketController.startup();