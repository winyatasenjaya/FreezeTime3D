package com.creativedrewy.framepicapp.views
{
	import com.creativedrewy.framepicapp.components.SetupUIRegionBox;
	import com.creativedrewy.framepicapp.constants.GlobalVars;
	import com.creativedrewy.framepicapp.events.ServerEvent;
	import com.creativedrewy.framepicapp.model.PicTakerModel;
	
	import flash.display.BitmapData;
	import flash.events.Event;
	import flash.events.MouseEvent;
	import flash.events.TimerEvent;
	import flash.media.Camera;
	import flash.media.CameraRoll;
	import flash.media.Video;
	import flash.utils.Timer;
	
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
			//onStep3ButtonClick(null);
		}
		
		protected function onStep2ButtonClick(event:MouseEvent):void
		{
			_picTakerModel.submitOrder();
		}
		
		protected function onStep3ButtonClick(event:MouseEvent):void
		{
			//_picTakerModel.submitReady();.
			
			mainButtonsContainer.visible = false;
			
//			var camera:Camera = Camera.getCamera();
//			camera.setMode(2750, 1650, 15);
//			
//			vidStream = new Video(camera.width, camera.height);
//			vidStream.attachCamera(camera);
//			
//			cameraViewport.addChild(vidStream);
//			cameraViewport.visible = true;
//			
//			vidStream.rotation = 90;
//			vidStream.x = cameraViewport.width;
//			
//			var blah:Timer = new Timer(2000, 3);
//			blah.addEventListener(TimerEvent.TIMER, onCameraViewportBlah, false, 0, true);
//			blah.start();
		}
		
		private var vidStream:Video;
		
		protected function onCameraViewportBlah(event:Event):void
		{
//			var testRoll:CameraRoll = new CameraRoll();
//			
//			var bitmapData:BitmapData = new BitmapData(vidStream.height, vidStream.width);
//			bitmapData.draw(vidStream);
//			
//			testRoll.addBitmapData(bitmapData);
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
					
					step2OrderButton.label = "Frame Number: " + event.messagePayload;
					step2OrderButton.enabled = false;
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