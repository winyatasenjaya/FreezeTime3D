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
		
		/**
		 * When a pic taker is ready, it sends along its frame number so that the system can update accordingly
		 */		
		public function submitReady(frameNumber:int):void
		{
			sendMessage("PicTakingReady", frameNumber);
		}
		
	}
}