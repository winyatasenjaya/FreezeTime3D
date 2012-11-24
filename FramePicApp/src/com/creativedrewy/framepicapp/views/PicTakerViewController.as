package com.creativedrewy.framepicapp.views
{
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
		[Bindable] public var step2OrderButton:Button;
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
			
		}
		
		protected function onStep3ButtonClick(event:MouseEvent):void
		{
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
				case GlobalVars.serverMessages.picTakerMessages.registerResponse: {
					step1RegisterButton.label = "Registered!";
					step1RegisterButton.enabled = false;
					
					step2OrderButton.label = "Waiting for master...";
					break;
				}
			}
		}
		
	}
}