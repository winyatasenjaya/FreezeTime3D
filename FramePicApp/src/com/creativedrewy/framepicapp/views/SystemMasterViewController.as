package com.creativedrewy.framepicapp.views
{
	import com.creativedrewy.framepicapp.components.SetupUIRegionBox;
	import com.creativedrewy.framepicapp.events.ServerEvent;
	import com.creativedrewy.framepicapp.model.MasterDeviceModel;
	
	import flash.events.MouseEvent;
	
	import spark.components.Button;
	import spark.components.Label;
	import spark.components.TextInput;
	import spark.components.View;
	
	/**
	 * Controller functionality for the master view
	 */	
	public class SystemMasterViewController extends View
	{
		[Bindable] public var hostAddressTextInput:TextInput;
		[Bindable] public var step1RegisterButton:Button;
		[Bindable] public var initOrderContainer:SetupUIRegionBox;
		[Bindable] public var step2InitOrderingButton:Button;
		[Bindable] public var devicesOrderedLabel:Label;
		[Bindable] public var freezeTimeContainer:SetupUIRegionBox;
		[Bindable] public var step3FreezeTimeButton:Button;
		[Bindable] public var devicesReadyLabel:Label;
		[Bindable] public var resetButton:Button;
		
		private var _masterModel:MasterDeviceModel;
		private var _orderDevices:Number = 0;
		private var _readyDevices:Number = 0;
		
		/**
		 * Constructor
		 */		
		public function SystemMasterViewController()
		{
			super();
			
			_masterModel = new MasterDeviceModel();
			_masterModel.addEventListener(ServerEvent.MESSAGE_RECEIVED, onServerMessageReceived, false, 0, true);
		}
		
		protected function step1ButtonClick(event:MouseEvent):void
		{
			_masterModel.initConnection(hostAddressTextInput.text);
		}	
		
		protected function step2InitOrderClick(event:MouseEvent):void
		{
			_orderDevices = 0;
			_readyDevices = 0;
			
			step2InitOrderingButton.label = "Ordering Started";
			step2InitOrderingButton.enabled = false;
			devicesOrderedLabel.text = _orderDevices + " device(s) ordered";
			
			_masterModel.sendInitOrder();
		}
		
		protected function step3FreezeTimeClick(event:MouseEvent):void
		{
			_masterModel.sendFreezeTime();
			resetButton.label = "Start Over";
		}
		
		protected function resetButtonClick(event:MouseEvent):void
		{
			
		}
		
		/**
		 * Handle the server messages here, since they dictate what happens in the UI
		 */		
		protected function onServerMessageReceived(event:ServerEvent):void
		{
			switch (event.serverMessage) {
				case "RegisterMasterResponse": {
					step1RegisterButton.label = "Master Device";
					step1RegisterButton.enabled = false;
					
					initOrderContainer.enabled = true;
					break;
				}
				case "PicTakerOrderUpdate": {
					_orderDevices++;
					devicesOrderedLabel.text = _orderDevices + " device(s) ordered";
					break;
				}
				case "PicTakerFrameReadyUpdate": {
					_readyDevices++;
					devicesReadyLabel.text = _readyDevices + " device(s) ready";
					
					if (_readyDevices == _orderDevices) {
						freezeTimeContainer.enabled = true;
					} else {
						freezeTimeContainer.enabled = false;
					}
					break;
				}
			}
		}
		
	}
}