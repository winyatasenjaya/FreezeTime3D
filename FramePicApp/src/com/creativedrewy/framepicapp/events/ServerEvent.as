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
		
		public function get serverMessage():String { return _serverMessage; }
		public function set serverMessage(value:String):void { _serverMessage = value; }
		
		/**
		 * Constructor
		 */		
		public function ServerEvent(type:String, message:String)
		{
			super(type);
			_serverMessage = message;
		}

	}
}