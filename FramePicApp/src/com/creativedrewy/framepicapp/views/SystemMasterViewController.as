package com.creativedrewy.framepicapp.views
{
	import com.creativedrewy.framepicapp.model.MasterDeviceModel;
	
	import spark.components.View;
	
	/**
	 * Controller functionality for the master view
	 */	
	public class SystemMasterViewController extends View
	{
		private var _masterModel:MasterDeviceModel;
		
		/**
		 * Constructor
		 */		
		public function SystemMasterViewController()
		{
			super();
			
			_masterModel = new MasterDeviceModel();
		}
		
	}
}