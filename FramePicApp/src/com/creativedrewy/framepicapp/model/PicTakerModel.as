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
			_roleString = GlobalVars.serverMessages.picTakerId;
			_registerMessage = GlobalVars.serverMessages.picTakerMessages.register;
		}
		
		public function submitOrder():void
		{
			//sendMessage(GlobalVars.serverMessages.picTakerMessages.submitOrder);
		}
		
	}
}