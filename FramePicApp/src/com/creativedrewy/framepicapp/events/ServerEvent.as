package com.creativedrewy.framepicapp.events
{
	import flash.events.Event;
	
	/**
	 * Events that originate from the controlling server
	 */	
	public class ServerEvent extends Event
	{
		public static const MESSAGE_RECEIVED:String = "serverMessageReceived";
		
		private var _serverMessage:String;
		private var _messagePayload:String;

		public function get serverMessage():String { return _serverMessage; }
		public function set serverMessage(value:String):void { _serverMessage = value; }
		
		public function get messagePayload():String { return _messagePayload; }
		public function set messagePayload(value:String):void { _messagePayload = value; }
		
		/**
		 * Constructor
		 */		
		public function ServerEvent(type:String, message:String, payload:String)
		{
			super(type);
			_serverMessage = message;
			
			if (payload != null) {
				_messagePayload = payload;
			}
		}

	}
}