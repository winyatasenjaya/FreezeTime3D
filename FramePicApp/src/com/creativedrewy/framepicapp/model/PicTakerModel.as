package com.creativedrewy.framepicapp.model
{
	import com.creativedrewy.framepicapp.constants.GlobalVars;
	import com.creativedrewy.framepicapp.events.ServerEvent;

	/**
	 * Model class to handle communication with the socket server for apps acting as pic takers
	 */	
	public class PicTakerModel extends ModelBase
	{
		/**
		 * Constructor
		 */		
		public function PicTakerModel()
		{
			_roleString = "picTaker";
			_registerMessage = "RegisterPicTaker";
		}
		
		public function submitOrder():void
		{
			sendMessage("RequestingFrameOrder");
		}
		
		public function submitReady():void
		{
			sendMessage("PicTakingReady");
		}
		
	}
}