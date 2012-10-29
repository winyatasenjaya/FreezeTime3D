package com.creativedrewy.framepicapp.views
{
	import flash.events.MouseEvent;
	import flash.media.Camera;
	import flash.media.Video;
	
	import mx.core.UIComponent;
	
	import spark.components.Button;
	import spark.components.VGroup;
	import spark.components.View;
	
	/**
	 * Controller functionality for the pic taker view
	 */
	public class PicTakerViewController extends View
	{
		[Bindable] public var mainButtonsContainer:VGroup;
		[Bindable] public var step1RegisterButton:Button;
		[Bindable] public var step3ReadyButton:Button;
		[Bindable] public var cameraViewport:UIComponent;
		
		/**
		 * Constructor 
		 */		
		public function PicTakerViewController()
		{
			super();
		}
		
		protected function onStep1ButtonClick(event:MouseEvent):void
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
		
	}
}