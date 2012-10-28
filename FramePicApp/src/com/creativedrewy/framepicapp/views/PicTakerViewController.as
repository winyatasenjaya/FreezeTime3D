package com.creativedrewy.framepicapp.views
{
	import flash.events.MouseEvent;
	
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
		[Bindable] public var step3ReadyButton:Button;
		[Bindable] public var cameraViewport:UIComponent;
		
		/**
		 * Constructor 
		 */		
		public function PicTakerViewController()
		{
			super();
		}
		
		protected function onStep3ButtonClick(event:MouseEvent):void
		{
			mainButtonsContainer.visible = false;
		}
		
	}
}