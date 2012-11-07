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
			_roleString = GlobalVars.serverMessages.masterId;
			_registerMessage = GlobalVars.serverMessages.masterMessages.register;
		}
		
	}
}