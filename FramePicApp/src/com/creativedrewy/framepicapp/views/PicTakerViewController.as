package com.creativedrewy.framepicapp.views
{
	import com.creativedrewy.framepicapp.components.SetupUIRegionBox;
	import com.creativedrewy.framepicapp.constants.GlobalVars;
	import com.creativedrewy.framepicapp.events.ServerEvent;
	import com.creativedrewy.framepicapp.model.PicTakerModel;
	
	import flash.display.Bitmap;
	import flash.display.BitmapData;
	import flash.events.Event;
	import flash.events.MouseEvent;
	import flash.events.TimerEvent;
	import flash.filesystem.File;
	import flash.filesystem.FileMode;
	import flash.filesystem.FileStream;
	import flash.geom.Rectangle;
	import flash.media.Camera;
	import flash.media.CameraRoll;
	import flash.media.Video;
	import flash.utils.ByteArray;
	import flash.utils.Timer;
	
	import mx.core.UIComponent;
	import mx.graphics.codec.JPEGEncoder;
	
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
		private var _picFrameNumber:int = -1;
		private var _camVideoStream:Video;
		private var _imageFile:File;
		private var _imageDirectory:String = "FreezeTime3D/";
		private var _imgFileName:String = "";
		
		/**
		 * Constructor 
		 */		
		public function PicTakerViewController()
		{
			super();
			
			_picTakerModel = new PicTakerModel();
			_picTakerModel.addEventListener(ServerEvent.MESSAGE_RECEIVED, onServerMessageReceived, false, 0, true);
		}
		
		/**
		 * Step 1 for PicTakers registers them as such with the server
		 */		
		protected function onStep1ButtonClick(event:MouseEvent):void
		{
			_picTakerModel.initConnection(hostAddressTextInput.text);
		}
		
		/**
		 * Step 2 for Pictakers is to submit a frame ordering request
		 */		
		protected function onStep2ButtonClick(event:MouseEvent):void
		{
			_picTakerModel.submitOrder();
		}
		
		/**
		 * Step 3 denotes that this PicTaker is ready for taking the pic; show the live feed from the camera
		 * TODO: Right now we are using AIR camera, which seems to only allow for really low res; investigate ANE camera
		 */		
		protected function onStep3ButtonClick(event:MouseEvent):void
		{
			_picTakerModel.submitReady(_picFrameNumber);
			
//			mainButtonsContainer.visible = false;
//			var camera:Camera = Camera.getCamera();
//			
//			_camVideoStream = new Video(800, 480);
//			_camVideoStream.attachCamera(camera);
//			
//			camera.setMode(800, 480, 20, false);
//			
//			cameraViewport.addChild(_camVideoStream);
//			cameraViewport.visible = true;
//			
//			_camVideoStream.rotation = 90;
//			_camVideoStream.x = cameraViewport.width;
		}
		
		/**
		 * Take the actual frame picture and save it to the user's device
		 */		
		protected function takeSaveCameraPic():void
		{
			var bitmapData:BitmapData = new BitmapData(_camVideoStream.height, _camVideoStream.width);
			bitmapData.draw(_camVideoStream);
			
			_imageFile = File.documentsDirectory.resolvePath(_imageDirectory + _imgFileName);
			
			var stream:FileStream = new FileStream();
			stream.open(_imageFile, FileMode.WRITE);
			
			var encoder:JPEGEncoder = new JPEGEncoder();
			var byteArray:ByteArray = encoder.encode(bitmapData);   
			stream.writeBytes(byteArray, 0, byteArray.bytesAvailable);
			stream.close();
			
			var framePicBitmap:Bitmap = new Bitmap(bitmapData);
			cameraViewport.removeChild(_camVideoStream);
			cameraViewport.addChild(framePicBitmap);
			
			framePicBitmap.rotation = 90;
			framePicBitmap.x = cameraViewport.width;
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
					_picFrameNumber = parseInt(event.messagePayload);
					step3Container.enabled = true;
					
					step2OrderButton.label = "Frame Number: " + _picFrameNumber;
					step2OrderButton.enabled = false;
					
					var now:Date = new Date();
					
					_imgFileName = "frame_" + _picFrameNumber + "_" + now.time + ".jpg";
					break;
				}
				case "TakeFramePic": {
					takeSaveCameraPic();
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