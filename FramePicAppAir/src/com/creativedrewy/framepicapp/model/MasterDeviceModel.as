package com.creativedrewy.framepicapp.model
{
	import com.creativedrewy.framepicapp.constants.GlobalVars;

	/**
	 * Model class for the device acting as the master of the system
	 */	
	public class MasterDeviceModel extends ModelBase
	{
		
		/**
		 * Constructor
		 */		
		public function MasterDeviceModel()
		{
			_roleString = "master";
			_registerMessage = "RegisterMaster";
		}
		
		public function sendInitOrder():void
		{
			sendMessage("InitPictureTakerOrder");
		}
		
		public function sendFreezeTime():void
		{
			sendMessage("StartFrameCapture");
		}
		
		public function sendResetSystem():void
		{
			sendMessage("ResetSystem");
		}
		
	}
}