/**
 * Copyright 2010 Santiago Hors Fraile and Salvador Jesús Romero

  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
 */

package norut;


import java.io.IOException;

import launchers.*;



import control.*;

public class NorutTest 
{


	String eventstring="";
	static WiiMoteAccelerationLauncher wiimoteAccelerationLauncher_1;
	static WiiMoteAccelerationLauncher wiimoteAccelerationLauncher_2;
	static WiiMoteRotationLauncher wiimoteRotationLauncher_1;
	static WiiMoteRotationLauncher wiimoteRotationLauncher_2;
	static WiiMoteButtonsLauncher wiimoteButtonsLauncher_1;
	static WiiMoteButtonsLauncher wiimoteButtonsLauncher_2;
	
	public static void main (String args []) throws IOException
	{
		//System.setProperty("bluecove.stack.first", "widcomm");
		System.setProperty("bluecove.jsr82.psm_minimum_off", "true");
		try{
			new NorutTest();
		}catch(Exception e){
			//e.printStackTrace();
		}
	}
	

    public NorutTest() throws IOException
    {

    	DeviceManager dm = DeviceManager.getDeviceManager();
     	
    	/*----------------------------Set wiimote 1------------------------------------*/
    	
     	wiimoteAccelerationLauncher_1 = dm.getWiiMoteAccelerationLauncher("001B7AE9657D",1);
  
     	WiimoteAccelerationEventHandler wiimoteAccelerationHandler_1 = new WiimoteAccelerationEventHandler();
     	
    	try {
    		wiimoteAccelerationLauncher_1.addListener(wiimoteAccelerationHandler_1);
		} catch (Exception e4) {
			// TODO Auto-generated catch block
			e4.printStackTrace();
		}    	
		
		wiimoteButtonsLauncher_1 = dm.getWiiMoteButtonsLauncher("001B7AE9657D",1);
		
	 	WiimoteButtonsEventHandler wiimoteButtonsHandler_1 = new WiimoteButtonsEventHandler();
    	try {
    		wiimoteButtonsLauncher_1.addListener(wiimoteButtonsHandler_1);
		} catch (Exception e4) {
			// TODO Auto-generated catch block
			e4.printStackTrace();
		}    	
	
		
		wiimoteRotationLauncher_1 = dm.getWiiMoteRotationLauncher(1);
		
		WiimoteRotationEventHandler wiimoteRotationEventHandler_1 = new WiimoteRotationEventHandler();
		try {
			wiimoteRotationLauncher_1.addListener(wiimoteRotationEventHandler_1);
			
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
		/*-----------------------------------Set wiimote 2------------------------------------------------------*/
		
     
    	wiimoteAccelerationLauncher_2 = dm.getWiiMoteAccelerationLauncher("00191DD60E91",2);
     	WiimoteAccelerationEventHandler wiimoteAccelerationHandler_2 = new WiimoteAccelerationEventHandler();
     	
    	try {
    		wiimoteAccelerationLauncher_2.addListener(wiimoteAccelerationHandler_2);
		} catch (Exception e4) {
			// TODO Auto-generated catch block
			e4.printStackTrace();
		}    	
		
		wiimoteButtonsLauncher_2 = dm.getWiiMoteButtonsLauncher("00191DD60E91",2);
		
	 	WiimoteButtonsEventHandler wiimoteButtonsHandler_2 = new WiimoteButtonsEventHandler();
    	try {
    		wiimoteButtonsLauncher_2.addListener(wiimoteButtonsHandler_2);
		} catch (Exception e4) {
			// TODO Auto-generated catch block
			e4.printStackTrace();
		}    	
	
		
		wiimoteRotationLauncher_2 = dm.getWiiMoteRotationLauncher(2);
		
		WiimoteRotationEventHandler wiimoteRotationEventHandler_2 = new WiimoteRotationEventHandler();
		try {
			wiimoteRotationLauncher_2.addListener(wiimoteRotationEventHandler_2);
			
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
		
		/*-------------------------------------------------------------------------------------------------------*/
		
		//GAMES:
		hugGame();
		lifGame();

	}
    
    private void hugGame(){
    	

		RightArmHuggingGameEventHandler rightArmHuggingGameEventHandler = new RightArmHuggingGameEventHandler();
		LeftArmHuggingGameEventHandler leftArmHuggingGameEventHandler = new LeftArmHuggingGameEventHandler();
		
		try {
			wiimoteRotationLauncher_1.addListener(rightArmHuggingGameEventHandler);
			wiimoteRotationLauncher_2.addListener(leftArmHuggingGameEventHandler);
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
		
    	
    }
    
    private void lifGame(){
    	
    	
    }

    public static void endGame(){
    	
    	
    	
    	DeviceManager dm = DeviceManager.getDeviceManager();
    	
    	dm.dropWiiMote(1, wiimoteAccelerationLauncher_1);
    	dm.dropWiiMote(2, wiimoteAccelerationLauncher_2);
    	dm.dropWiiMote(1, wiimoteRotationLauncher_1);
    	dm.dropWiiMote(2, wiimoteRotationLauncher_2);
    	dm.dropWiiMote(1,wiimoteButtonsLauncher_1);
    	dm.dropWiiMote(2,wiimoteButtonsLauncher_2);
    	
    	System.exit(0);
    	
    }
   



	
}


