
require("mootools");
var net = require("net");

var ControllerModule = require("./appsocketserver/MobileAppsSocketController");

var socketHost = "localhost";
var socketPort = 7474;
var socketController = new ControllerModule.MobileAppsSocketController(net, socketHost, socketPort);
socketController.startup();