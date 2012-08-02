/**
 * Copyright 2012 Santiago Hors Fraile 

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

package run;

import handlers.KinectMotorHandler;
import handlers.KinectUserOutOfScopeHandler;
import handlers.NoninHandler;
import handlers.NunchukAccelerationHandler;
import handlers.NunchukAnalogStickHandler;
import handlers.NunchukButtonsHandler;
import handlers.KinectPoseHandler;
import handlers.KinectSkeletonJointsHandler;
import handlers.WiiBoardHandler;
import handlers.WiiMoteAccelerationHandler;
import handlers.WiiMoteButtonsHandler;
import handlers.WiiMoteIRGlanceHandler;
import handlers.WiiMoteIRHandler;
import handlers.WiiMoteRotationHandler;


import java.io.IOException;

import launchers.KinectMotorLauncher;
import launchers.KinectSkeletonLauncher;
import launchers.KinectUserGameControlLauncher;
import launchers.KinectUserHugLauncher;
import launchers.KinectUserMovementLauncher;
import launchers.KinectUserReachWithBothHandsLauncher;
import launchers.NoninLauncher;
import launchers.NunchukAccelerationLauncher;
import launchers.NunchukAnalogStickLauncher;
import launchers.NunchukButtonsLauncher;
import launchers.WiiBoardLauncher;
import launchers.WiiMoteAccelerationLauncher;
import launchers.WiiMoteButtonsLauncher;
import launchers.WiiMoteIRGlanceLauncher;
import launchers.WiiMoteIRLauncher;
import launchers.WiiMoteRotationLauncher;
import control.DeviceManager;

/**
 * This class starts the services of the devices requested as parameters.
 * @author Santiago Hors Fraile
 *
 */
public class StartDevices {
	
	/**
	 * This thread is in charge of reading the instructions that arrive through the socket
	 */
	static Thread readerFromSharedOutputThread;
	
	
	public static void main (String args []) throws IOException
	{		
		//Set the bluetooth for the Wii Controllers
		System.setProperty("bluecove.jsr82.psm_minimum_off", "true");
		try{
			//Check that the number of parameters contains at least the IP and Port to connect
			if(args.length<2){
				System.out.println("Please check the parameter list. It must have the IP address,and the port");	
			}else{
				new StartDevices(args);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}



	public StartDevices(String[] args) throws Exception
	{    
		//This variable may be modified in future version if we allow that two or more users use the same Kinect device at the same time
		int maximumNumberOfKinectUsers =1;
		//By default, I'm interested on the user whose label is '1'
		int theUserIWant=1;
		//Get the IP
		String serverIP= args[0];
		//Get the Port
		short serverPort= Short.parseShort(args[1]);
		//Get the device manager
		DeviceManager dm = DeviceManager.getDeviceManager(serverIP, serverPort, maximumNumberOfKinectUsers);
   	 	
		//Iterate over the rest of the parameters to run the requested services
		for(int i=2; i<args.length; i++){
		
			if(args[i].equalsIgnoreCase("kinectMovements")){
				KinectUserMovementLauncher kumLauncher= dm.getKinectUserMovementLauncher(theUserIWant); 	
		    	KinectPoseHandler poseHandler = new KinectPoseHandler();
		    	kumLauncher.addListener(poseHandler);    
		    
		    	
			}else if(args[i].equalsIgnoreCase("reachWithBothHands")){
				KinectUserReachWithBothHandsLauncher kurwbhLauncher= dm.getKinectUserReachWithBothHandsLauncher(theUserIWant);
				KinectPoseHandler poseHandler = new KinectPoseHandler();
				kurwbhLauncher.addListener(poseHandler);
	
			}
			else if(args[i].equalsIgnoreCase("kinectHug")){
				KinectUserHugLauncher kuhLauncher= dm.getKinectUserHugLauncher(theUserIWant);
				KinectPoseHandler poseHandler = new KinectPoseHandler();
				kuhLauncher.addListener(poseHandler);
	
			}else if(args[i].equalsIgnoreCase("gameControl")){
				KinectUserGameControlLauncher kgcLauncher= dm.getKinectUserGameControlLauncher(theUserIWant);
		    	KinectPoseHandler poseHandler = new KinectPoseHandler();
		    	kgcLauncher.addListener(poseHandler);
	
			}else if(args[i].equalsIgnoreCase("outOfScopeDetection")){		
			   	DeviceManager.getDeviceManager().getKinectUserOutOfScopeLauncher(1).addListener(new KinectUserOutOfScopeHandler());	
			
			}else if(args[i].equalsIgnoreCase("kinectJoints")){
				KinectSkeletonLauncher ksl= dm.getKinectSkeletonLauncher(theUserIWant);
				KinectSkeletonJointsHandler cqaks= new KinectSkeletonJointsHandler();
				ksl.addListener(cqaks);
				
			}else if(args[i].equalsIgnoreCase("kinectMotor")){	
				KinectMotorLauncher kml= dm.getKinectMotorLauncher();
				KinectMotorHandler kmh = new KinectMotorHandler();
				kml.addListener(kmh);
				
			}else if(args[i].equalsIgnoreCase("kinectAll")){
				
		    	KinectUserMovementLauncher kumLauncher= dm.getKinectUserMovementLauncher(theUserIWant);
		    	KinectUserHugLauncher kuhLauncher= dm.getKinectUserHugLauncher(theUserIWant);
		    	KinectUserGameControlLauncher kgcLauncher= dm.getKinectUserGameControlLauncher(theUserIWant);
		    	KinectUserReachWithBothHandsLauncher kurwbhLauncher= dm.getKinectUserReachWithBothHandsLauncher(theUserIWant);
		    	
		    	KinectPoseHandler poseHandler = new KinectPoseHandler();
		      	
		    	kumLauncher.addListener(poseHandler);
		    	kuhLauncher.addListener(poseHandler);
		    	kgcLauncher.addListener(poseHandler);
		    	kurwbhLauncher.addListener(poseHandler);
		    
		    	KinectSkeletonLauncher ksl= dm.getKinectSkeletonLauncher(theUserIWant);
				KinectSkeletonJointsHandler cqaks= new KinectSkeletonJointsHandler();
				ksl.addListener(cqaks);
				
				KinectMotorLauncher kml= dm.getKinectMotorLauncher();
				KinectMotorHandler kmh = new KinectMotorHandler();
				kml.addListener(kmh);
		    	
			} else if(args[i].substring(0, 19).equalsIgnoreCase("wiimoteAcceleration")) {
				
				int label= Integer.parseInt(args[i].substring(20));

				WiiMoteAccelerationLauncher wmacl;
				
				wmacl=dm.getWiiMoteAccelerationLauncher("", label);
				WiiMoteAccelerationHandler wmah = new WiiMoteAccelerationHandler();
		    	try {
		    		wmacl.addListener(wmah);
				} catch (Exception e) {
					e.printStackTrace();
				}    
				
			} else if(args[i].substring(0,15).equalsIgnoreCase("wiimoteRotation")) {
			
				int label= Integer.parseInt(args[i].substring(16));
				
				WiiMoteRotationLauncher wmrl;
			
				wmrl=dm.getWiiMoteRotationLauncher(label);
				WiiMoteRotationHandler wmrh = new WiiMoteRotationHandler();
		    	try {
		    		wmrl.addListener(wmrh);
				} catch (Exception e) {
					e.printStackTrace();
				}    
			} else if(args[i].substring(0,14).equalsIgnoreCase("wiimoteButtons")) {
				
				int label= Integer.parseInt(args[i].substring(15));
				
				WiiMoteButtonsLauncher wmbl;
				
				wmbl=dm.getWiiMoteButtonsLauncher("", label);
				WiiMoteButtonsHandler wmbh = new WiiMoteButtonsHandler();
		    	try {
		    		wmbl.addListener(wmbh);
				} catch (Exception e) {
					e.printStackTrace();
				}    
			} else if(args[i].substring(0,9).equalsIgnoreCase("wiimoteIR")) {
				int label= Integer.parseInt(args[i].substring(10));
				
				WiiMoteIRLauncher wmirl;
				
				wmirl=dm.getWiiMoteIRLauncher("", label);
				WiiMoteIRHandler wmirh = new WiiMoteIRHandler();
		    	try {
		    		wmirl.addListener(wmirh);
				} catch (Exception e) {
					e.printStackTrace();
				}    
			} else if(args[i].substring(0,15).equalsIgnoreCase("wiimoteIRGlance")) {
				int label= Integer.parseInt(args[i].substring(16));
				
				WiiMoteIRGlanceLauncher wmirgl;
				
				wmirgl=dm.getWiiMoteIRGlanceLauncher("", label);
				WiiMoteIRGlanceHandler wmirgh = new WiiMoteIRGlanceHandler();
		    	try {
		    		wmirgl.addListener(wmirgh);
				} catch (Exception e) {
					e.printStackTrace();
				}    
				
			} else if(args[i].substring(0,17).equalsIgnoreCase("wiimoteAllTypical")) {
				
				int label= Integer.parseInt(args[i].substring(18));
				WiiMoteAccelerationLauncher wmacl;
				
				wmacl=dm.getWiiMoteAccelerationLauncher("", label);
				WiiMoteAccelerationHandler wmah = new WiiMoteAccelerationHandler();
		    	try {
		    		wmacl.addListener(wmah);
				} catch (Exception e) {
					e.printStackTrace();
				}    
		    	WiiMoteRotationLauncher wmrl;
		    	WiiMoteButtonsLauncher wmbl;
		
				wmbl=dm.getWiiMoteButtonsLauncher("", label);
				WiiMoteButtonsHandler wmbh = new WiiMoteButtonsHandler();
		    	try {
		    		wmbl.addListener(wmbh);
				} catch (Exception e) {
					e.printStackTrace();
				}    
				wmrl=dm.getWiiMoteRotationLauncher(label);
				WiiMoteRotationHandler wmrh = new WiiMoteRotationHandler();
		    	try {
		    		wmrl.addListener(wmrh);
				} catch (Exception e) {
					e.printStackTrace();
				}    
		    	
			}else if(args[i].substring(0,19).equalsIgnoreCase("nunchukAcceleration")){
				int label= Integer.parseInt(args[i].substring(20));
				NunchukAccelerationLauncher nacl;
				
				nacl=dm.getNunchukAccelerationLauncher(label);
				NunchukAccelerationHandler nah = new NunchukAccelerationHandler();
		    	try {
		    		nacl.addListener(nah);
				} catch (Exception e) {
					e.printStackTrace();
				}    
			}else if(args[i].substring(0,18).equalsIgnoreCase("nunchukAnalogStick")){
				int label= Integer.parseInt(args[i].substring(19));
				NunchukAnalogStickLauncher nasl;
				
				nasl=dm.getNunchukAnalogStickLauncher(label);
				NunchukAnalogStickHandler nash = new NunchukAnalogStickHandler();
		    	try {
		    		nasl.addListener(nash);
				} catch (Exception e) {
					e.printStackTrace();
				}    
			}else if(args[i].substring(0,14).equalsIgnoreCase("nunchukButtons")){
				int label= Integer.parseInt(args[i].substring(15));
			
				NunchukButtonsLauncher nbl;
				
				nbl=dm.getNunchukButtonsLauncher(label);
				NunchukButtonsHandler nbh = new NunchukButtonsHandler();
		    	try {
		    		nbl.addListener(nbh);
				} catch (Exception e) {
					e.printStackTrace();
				}    
			}else if(args[i].substring(0,10).equalsIgnoreCase("nunchukAll")){
				int label= Integer.parseInt(args[i].substring(11));
				NunchukAccelerationLauncher nacl;
				
				nacl=dm.getNunchukAccelerationLauncher(label);
				NunchukAccelerationHandler nah = new NunchukAccelerationHandler();
		    	try {
		    		nacl.addListener(nah);
				} catch (Exception e) {
					e.printStackTrace();
				}    
		  
				NunchukAnalogStickLauncher nasl;
				
				nasl=dm.getNunchukAnalogStickLauncher(label);
				NunchukAnalogStickHandler nash = new NunchukAnalogStickHandler();
		    	try {
		    		nasl.addListener(nash);
				} catch (Exception e) {
					e.printStackTrace();
				}    
		    	
				NunchukButtonsLauncher nbl;
				
				nbl=dm.getNunchukButtonsLauncher(label);
				NunchukButtonsHandler nbh = new NunchukButtonsHandler();
		    	try {
		    		nbl.addListener(nbh);
				} catch (Exception e) {
					e.printStackTrace();
				}    
			}else if(args[i].equalsIgnoreCase("nonin")) {
				NoninLauncher nl;
				try {
					nl = dm.getNoninLauncher("");
				 	NoninHandler nh= new NoninHandler();
			    	nl.addListener(nh);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}else if(args[i].equalsIgnoreCase("wiiboard")){
				WiiBoardLauncher wbl;
				
				wbl= dm.getWiiBoardLauncher("");
				
				WiiBoardHandler wbh=new WiiBoardHandler();
				
				wbl.addListener(wbh);
			
			} else {
				System.out.println("Parameter \""+args[i]+"\" not understood");
			}
	
	     
		}
		
	}





}