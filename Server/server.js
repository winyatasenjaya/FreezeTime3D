
require("mootools");
var net = require("net");

var ControllerModule = require("./appsocketserver/MobileAppsSocketController");

var socketHost = process.env.IP;
var socketPort = 7474;
var socketController = new ControllerModule.MobileAppsSocketController(net, socketHost, socketPort);
socketController.startup();

var webserver = require("./webserver/webserver.js");