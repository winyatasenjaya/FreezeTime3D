package com.creativedrewy.framepicapp.model
{
	import com.creativedrewy.framepicapp.constants.GlobalVars;
	
	import flash.events.Event;
	import flash.events.ProgressEvent;
	import flash.net.Socket;

	/**
	 * Model class to handle communication with the socket server for apps acting as pic takers
	 */	
	public class PicTakerModel
	{
		private var _socketConnection:Socket;
		
		/**
		 * Constructor
		 */		
		public function PicTakerModel()
		{
			_socketConnection = new Socket(GlobalVars.SERVER_HOST, GlobalVars.SERVER_PORT);
			_socketConnection.addEventListener(Event.CONNECT, onSocketConnect, false, 0, true);
			_socketConnection.addEventListener(ProgressEvent.SOCKET_DATA, onSocketData, false, 0, true);
			//mySocket.addEventListener(Event.CLOSE, onSocketClose);
			//addEventListener(IOErrorEvent.IO_ERROR, ioErrorHandler);
			//addEventListener(SecurityErrorEvent.SECURITY_ERROR, securityErrorHandler);
		}
		
		protected function onSocketConnect(event:Event):void
		{
			//_socketConnection.writeUTFBytes(JSON.stringify({ role: "picTaker", message: "RegisterPicTaker" }));
		}
		
		protected function onSocketData(event:ProgressEvent):void
		{
			
		}
		
	}
}