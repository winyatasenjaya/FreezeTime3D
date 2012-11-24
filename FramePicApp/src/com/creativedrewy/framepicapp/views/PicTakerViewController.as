package com.creativedrewy.framepicapp.views
{
	import com.creativedrewy.framepicapp.components.SetupUIRegionBox;
	import com.creativedrewy.framepicapp.constants.GlobalVars;
	import com.creativedrewy.framepicapp.events.ServerEvent;
	import com.creativedrewy.framepicapp.model.PicTakerModel;
	
	import flash.events.MouseEvent;
	import flash.media.Camera;
	import flash.media.Video;
	
	import mx.core.UIComponent;
	
	import spark.components.Button;
	import spark.components.TextInput;
	import spark.components.VGroup;
	import spark.components.View;
	
	/**
	 * Controller functionality for the pic taker view
	 */
	public class PicTakerViewController extends View
	{
		[Bindable] public var mainButtonsContainer:VGroup;
		[Bindable] public var hostAddressTextInput:TextInput;
		[Bindable] public var step1RegisterButton:Button;
		[Bindable] public var step2Container:SetupUIRegionBox;
		[Bindable] public var step2OrderButton:Button;
		[Bindable] public var step3Container:SetupUIRegionBox;
		[Bindable] public var step3ReadyButton:Button;
		[Bindable] public var cameraViewport:UIComponent;
		
		private var _picTakerModel:PicTakerModel;
		
		/**
		 * Constructor 
		 */		
		public function PicTakerViewController()
		{
			super();
			
			_picTakerModel = new PicTakerModel();
			_picTakerModel.addEventListener(ServerEvent.MESSAGE_RECEIVED, onServerMessageReceived, false, 0, true);
		}
		
		protected function onStep1ButtonClick(event:MouseEvent):void
		{
			_picTakerModel.initConnection(hostAddressTextInput.text);
		}
		
		protected function onStep2ButtonClick(event:MouseEvent):void
		{
			_picTakerModel.submitOrder();
		}
		
		protected function onStep3ButtonClick(event:MouseEvent):void
		{
			_picTakerModel.submitReady();
			
			mainButtonsContainer.visible = false;
			
//			var vidStream:Video = new Video(cameraViewport.width, cameraViewport.height);
//			cameraViewport.addChild(vidStream);
//			
//			var camera:Camera = Camera.getCamera();
//			camera.setMode(cameraViewport.width, cameraViewport.height, 10);
//			vidStream.attachCamera(camera);
//			
//			cameraViewport.visible = true;
		}
		
		/**
		 * Handle the server messages here, since they dictate what happens in the UI
		 */		
		protected function onServerMessageReceived(event:ServerEvent):void
		{
			switch (event.serverMessage) {
				case "RegisterPicTakerResponse": {
					step1RegisterButton.label = "Registered!";
					step1RegisterButton.enabled = false;
					
					step2OrderButton.label = "Waiting for master...";
					break;
				}
				case "ServerReadyForOrder": {
					step2Container.enabled = true;
					step2OrderButton.label = "Submit Order";
					break;
				}
				case "FrameOrderResponse": {
					step3Container.enabled = true;
					//TODO: This is where we get our order; update UI but need it from the message
					break;
				}
				case "TakeFramePic": {
					//TODO: Snap picture, save, upload to web server
					break;
				}
				case "ResetPicTaking": {
					//TODO: Reset UI to redo the whole process
					break;
				}
			}
		}
		
	}
}