
require("mootools");
var net = require("net");

var ControllerModule = require("./appsocketserver/MobileAppsSocketController");

var socketHost = process.env.IP;
var socketPort = 7474;
var socketController = new ControllerModule.MobileAppsSocketController(net, socketHost, socketPort);
socketController.startup();

//TODO: Need to create a package.json so that all requirements are noted
//var webserver = require("./webserver/webserver.js");