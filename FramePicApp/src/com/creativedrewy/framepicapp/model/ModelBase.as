package com.creativedrewy.framepicapp.model
{
	import com.creativedrewy.framepicapp.constants.GlobalVars;
	import com.creativedrewy.framepicapp.events.ServerEvent;
	
	import flash.events.Event;
	import flash.events.EventDispatcher;
	import flash.events.IEventDispatcher;
	import flash.events.ProgressEvent;
	import flash.net.Socket;
	
	/**
	 * Base class for all models that FreeTime3D implements
	 */	
	public class ModelBase extends EventDispatcher
	{
		protected var _socketConnection:Socket;
		protected var _roleString:String;
		protected var _registerMessage:String;
		
		/**
		 * Constructor
		 */		
		public function ModelBase() { }
		
		/**
		 * Initialize the connection with the socekt server
		 */
		public function initConnection(serverHost:String):void
		{
			_socketConnection = new Socket(serverHost, GlobalVars.SERVER_PORT);
			_socketConnection.addEventListener(Event.CONNECT, onSocketConnect, false, 0, true);
			_socketConnection.addEventListener(ProgressEvent.SOCKET_DATA, onSocketData, false, 0, true);
			//mySocket.addEventListener(Event.CLOSE, onSocketClose);
			//addEventListener(IOErrorEvent.IO_ERROR, ioErrorHandler);
			//addEventListener(SecurityErrorEvent.SECURITY_ERROR, securityErrorHandler);
		}
		
		/**
		 * Once the socket connection has been made, send the "register" message
		 */		
		protected function onSocketConnect(event:Event):void
		{
			sendMessage(_registerMessage);
		}
		
		/**
		 * Send a message to the socket server; it is converted to a small JSON obj for identification
		 */		
		public function sendMessage(messageString:String):void
		{
			var msgObj:Object = {
				role: _roleString,
				message: messageString
			};
			
			_socketConnection.writeUTFBytes(JSON.stringify(msgObj));
		}
		
		/**
		 * The server has sent data back to us; pass to the consuming class
		 */		
		protected function onSocketData(event:ProgressEvent):void
		{
			var responseMessage:String = _socketConnection.readUTFBytes(_socketConnection.bytesAvailable);
			dispatchEvent(new ServerEvent(ServerEvent.MESSAGE_RECEIVED, responseMessage));
		}
		
	}
}