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

package control;


import handlers.*;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;

import javax.vecmath.Vector3d;

import org.wiigee.device.Wiimote;

import kinectThreads.KinectUserActionEnum;


import services.KinectSkeletonServiceEvent;
import services.KinectUserActionServiceEvent;
import services.KinectUserOutOfScopeServiceEvent;
import services.NoninServiceEvent;
import services.NunchukAccelerationServiceEvent;
import services.NunchukAnalogStickServiceEvent;
import services.NunchukServiceButton;
import services.NunchukServiceButtonPressedEvent;
import services.NunchukServiceButtonReleasedEvent;
import services.WiiBoardServiceButtonEvent;
import services.WiiBoardServiceDisconnectionEvent;
import services.WiiBoardServiceMassEvent;
import services.WiiBoardServiceStatusEvent;
import services.WiiMoteServiceAccelerationEvent;
import services.WiiMoteServiceButton;
import services.WiiMoteServiceButtonPressReceivedEvent;
import services.WiiMoteServiceButtonReleaseReceivedEvent;
import services.WiiMoteServiceIREvent;
import services.WiiMoteServiceIRGlanceEvent;


import KinectPackage.LEDStatus;

import com.google.gson.Gson;



/**
 * This class maps the data of the device events and codifies it under the GSON/JSON protocol before sending it to the 
 * specified IP address (and port) through a socket.
 * @author Santiago Hors Fraile
 *
 */
public class SharedSocket implements Runnable{
	

	
	/**
	 * This object is the only object that can exist of this class. This object
	 * has to be the one used to send the events through the socket to the remote server.
	 */
	static private SharedSocket rc = null;
	/**
	 * This field represents the SocketManager of the socket we have stablished the communication through.
	 */
	private SocketManager sm = null;
	/**
	 * This field serializes the socket
	 */
	
	private static Semaphore sendingSem=null;
	
	/**
	 * This object stores what we are going to send
	 */
	static ConcurrentHashMap<Object, Object> actionMap; 
	
	/**
	 * This object stores the joints
	 */
	static ConcurrentHashMap<Object, Object> jointMap; 
	/**
	 * This object stores the last socket so that when we add a new field we can copy the last movement state to send it along the updated data.
	 */
	static ConcurrentHashMap<Object, Object> lastActionMap; 
	/**
	 * This object stores the jointMap and lastActionMap to be sent through the socket.
	 */
	static ConcurrentHashMap<Object, Object> mixedMap;
	/**
	 * This thread is run for detecting the inputs from the socket at all times.
	 */
	static Thread sharedsocketThread;
	
	/**
	 * 
	 * Returns a new instance of SharedOutupu if it did not exist or the
	 * instance that was created if there was a created instance already.
	 * It starts the thread to listen to the port inputs.
	 * 
	 * @return SharedSocket
	 */
	static public SharedSocket getSharedSocket() {
		
	        if (rc == null) {
	            rc = new SharedSocket();
	            
	            sendingSem = new Semaphore(0,true);
	            actionMap= new ConcurrentHashMap<Object, Object>();
	            jointMap=new ConcurrentHashMap<Object, Object>();
	            lastActionMap= new ConcurrentHashMap<Object, Object>();
	            lastActionMap.put("standStill","true");
	            lastActionMap.put("walk", "false");
	            mixedMap = new ConcurrentHashMap<Object,Object>();
				
				sharedsocketThread= new Thread(SharedSocket.getSharedSocket());
				sharedsocketThread.start();
	           
	      
	        }
	        return rc;
     }

	/**
	 * This function sends any type of data device data event through the socket. It is mapped and codified under the GSON/JSON protocol before it is sent.
	 * It is mandatory to get any message from the other side of the socket each time that we are going to send data.
	 * @param se The device data event (of any type) that we want to send information about.
	 */
	public void performTransference(IEventCommModule se){
	
		
		while (sm == null) {
			try {
				
				sm = SocketManager.getSocket(DeviceManager.getDeviceManager().getIpAddress(), DeviceManager.getDeviceManager().getPort());
			} catch (Exception e) {
				sm = null;
			}
		}
		try {
	
			boolean validEvent=false;
			
			Gson gson = new Gson();
			String s ="";

			actionMap.put("device", "kinect");
			
			
			/*
			 * Map the user's movements
			 */
			if(se instanceof KinectUserActionServiceEvent){
				validEvent=true;	
				
				actionMap.put("device", "kinect");
				if(lastActionMap.containsKey("walk")){
					actionMap.put("walk", lastActionMap.get("walk"));
				}else{
					actionMap.put("walk", "false");
				}
				actionMap.put("hug", "false");
				if(lastActionMap.containsKey("pause")){
					actionMap.put("pause", lastActionMap.get("pause"));
				}else{
					actionMap.put("pause", "false");
				}
				actionMap.put("accept","false");
				actionMap.put("cancel","false");
				if(actionMap.containsKey("reached")){
					actionMap.put("reached", lastActionMap.get("reached"));
				}else{
					actionMap.put("reached", 0);
				}
				actionMap.put("pickedUpFromLeft","false");
				actionMap.put("pickedUpFromRight", "false");
	
				
				String action= ((KinectUserActionServiceEvent)se).getUserAction();
				
				if(action.compareTo(KinectUserActionEnum.STAND.name())==0){
					actionMap.put("walk","false");
				}else if(action.compareTo(KinectUserActionEnum.WALK.name())==0){
					actionMap.put("walk","true");
				}else if(action.compareTo(KinectUserActionEnum.PAUSE.name())==0){
					actionMap.put("pause", "true");
				}else if(action.compareTo(KinectUserActionEnum.RESUME.name())==0){
					actionMap.put("pause", "false");
				}else if(action.compareTo(KinectUserActionEnum.HUG.name())==0){
					actionMap.put("hug", "true");
				}else if(action.compareTo(KinectUserActionEnum.ACCEPT.name())==0){
					actionMap.put("accept", "true");
				}else if(action.compareTo(KinectUserActionEnum.CANCEL.name())==0){
					actionMap.put("cancel", "true");
				}else if(action.compareTo(KinectUserActionEnum.REACHED.name())==0){
					actionMap.put("reached", ((KinectUserActionServiceEvent) se).getValue());
				}else if(action.compareTo(KinectUserActionEnum.PICKED_UP_FROM_LEFT.name())==0){
					actionMap.put("pickedUpFromLeft", "true");
				}else if(action.compareTo(KinectUserActionEnum.PICKED_UP_FROM_RIGHT.name())==0){
					actionMap.put("pickedUpFromRight", "true");
				}
			}else 
			if(se instanceof KinectUserOutOfScopeServiceEvent){
				actionMap.put("pause", "true");
				System.out.println("+++++++++++++++++++");
				System.out.println("GAME PAUSE (user out of scope)");
				System.out.println("+++++++++++++++++++");
			}
			if (se instanceof KinectSkeletonServiceEvent){
				if(((KinectSkeletonServiceEvent) se).getHead().getX()!=0.0){
					validEvent=true;
					
					jointMap.put("device", "kinect");
					jointMap.put("head", ((KinectSkeletonServiceEvent) se).getHead());
					jointMap.put("neck", ((KinectSkeletonServiceEvent) se).getNeck());
					jointMap.put("leftShoulder", ((KinectSkeletonServiceEvent) se).getLeftShoulder());
					jointMap.put("rightShoulder", ((KinectSkeletonServiceEvent) se).getRightShoulder());
					jointMap.put("leftElbow", ((KinectSkeletonServiceEvent) se).getLeftElbow());
					jointMap.put("rightElbow", ((KinectSkeletonServiceEvent) se).getRightElbow());
					jointMap.put("leftHand", ((KinectSkeletonServiceEvent) se).getLeftHand());
					jointMap.put("rightHand", ((KinectSkeletonServiceEvent) se).getRightHand());
					jointMap.put("torso", ((KinectSkeletonServiceEvent) se).getTorso());
					jointMap.put("leftHip", ((KinectSkeletonServiceEvent) se).getLeftHip());
					jointMap.put("rightHip", ((KinectSkeletonServiceEvent) se).getRightHip());
					jointMap.put("leftKnee", ((KinectSkeletonServiceEvent) se).getLeftKnee());
					jointMap.put("rightKnee", ((KinectSkeletonServiceEvent) se).getRightKnee());
					jointMap.put("leftFoot", ((KinectSkeletonServiceEvent) se).getLeftFoot());
					jointMap.put("rightFoot", ((KinectSkeletonServiceEvent) se).getRightFoot());
				}
			}else
			if(se instanceof WiiMoteServiceAccelerationEvent){
				validEvent=true; 
				
				actionMap.put("device", "wiimote");
				actionMap.put("type", "acceleration");
				actionMap.put("value",new Vector3d(((WiiMoteServiceAccelerationEvent) se).X,((WiiMoteServiceAccelerationEvent) se).Y,((WiiMoteServiceAccelerationEvent) se).Z));
			}else
			if(se instanceof WiiMoteServiceButtonPressReceivedEvent){
				validEvent=true;
				
				actionMap.put("device", "wiimote");
				actionMap.put("type","button");
				actionMap.put("value",WiiMoteServiceButton.returnButtonName(((WiiMoteServiceButtonPressReceivedEvent) se).button));
				actionMap.put("action", "pressed");
			}else
			if(se instanceof WiiMoteServiceButtonReleaseReceivedEvent){
				validEvent=true;
				 
				actionMap.put("device", "wiimote");
				actionMap.put("type","button");
				actionMap.put("value",WiiMoteServiceButton.returnButtonName(((WiiMoteServiceButtonReleaseReceivedEvent) se).button));
				actionMap.put("action", "released");
				
			}else
			if(se instanceof WiiMoteServiceIREvent){
				validEvent=true;
				 
				actionMap.put("device","wiimote");actionMap= new ConcurrentHashMap<Object, Object>();
				actionMap.put("type", "ir");
				actionMap.put("coordinates", ((WiiMoteServiceIREvent) se).getCoordinates());
				actionMap.put("valids",((WiiMoteServiceIREvent) se).getValids());
				actionMap.put("size", ((WiiMoteServiceIREvent) se).getSize());
			}else
			if(se instanceof WiiMoteServiceIRGlanceEvent){
				validEvent=true;
				
				actionMap.put("device","wiimote");
				actionMap.put("type", "irGlance");
				actionMap.put("period", ((WiiMoteServiceIRGlanceEvent) se).period);
				actionMap.put("source",((WiiMoteServiceIRGlanceEvent) se).source);
				actionMap.put("speed",((WiiMoteServiceIRGlanceEvent) se).speed);
			}else
			if(se instanceof NoninServiceEvent){
				validEvent=true;
				 
				actionMap.put("device","nonin");
				actionMap.put("o2", ((NoninServiceEvent) se).oxy);
				actionMap.put("pulse",((NoninServiceEvent) se).pulse);
				actionMap.put("lowBattery",((NoninServiceEvent) se).data.isLowBattery());
			}else
			if(se instanceof WiiBoardServiceMassEvent){
				validEvent=true;
				 
				actionMap.put("device","wiiboard");
				actionMap.put("type", "mass");
				actionMap.put("bottomLeft", ((WiiBoardServiceMassEvent) se).bottomLeft);
				actionMap.put("bottomRight", ((WiiBoardServiceMassEvent) se).bottomRight);
				actionMap.put("topLeft", ((WiiBoardServiceMassEvent) se).topLeft);
				actionMap.put("topRight", ((WiiBoardServiceMassEvent) se).topRight);
			}else
			if(se instanceof WiiBoardServiceButtonEvent){
				validEvent=true;
				 
				actionMap.put("device","wiiboard");
				actionMap.put("type", "button");
				actionMap.put("isPressed", ((WiiBoardServiceButtonEvent) se).isPressed);
				actionMap.put("isReleased", ((WiiBoardServiceButtonEvent) se).isReleased);
			}else
			if(se instanceof WiiBoardServiceDisconnectionEvent){
				validEvent=true;
				 
				actionMap.put("device","wiiboard");
				actionMap.put("type", "disconnection");
			}else
			if(se instanceof WiiBoardServiceStatusEvent){
				validEvent=true;
				 
				actionMap.put("device","wiiboard");
				actionMap.put("type", "status");
				actionMap.put("battery", ((WiiBoardServiceStatusEvent) se).battery);
				actionMap.put("light", ((WiiBoardServiceStatusEvent) se).lightState);
			}else
			if(se instanceof NunchukAccelerationServiceEvent){
				validEvent=true;
				 
				actionMap.put("device","nunchuk");
				actionMap.put("type", "acceleration");
				actionMap.put("battery", new Vector3d(((NunchukAccelerationServiceEvent) se).getX(),((NunchukAccelerationServiceEvent) se).getY(),((NunchukAccelerationServiceEvent) se).getZ()));
				actionMap.put("light", ((WiiBoardServiceStatusEvent) se).lightState);
			}else
			if(se instanceof NunchukServiceButtonPressedEvent){
				validEvent=true;
				 
				actionMap.put("device","nunchuk");
				actionMap.put("type", "buttons");
				actionMap.put("buttonPressed", NunchukServiceButton.getValue(((NunchukServiceButtonPressedEvent) se).button));
				
			}else
			if(se instanceof NunchukServiceButtonReleasedEvent){
				validEvent=true;
				 
				actionMap.put("device","nunchuk");
				actionMap.put("type", "buttons");
				actionMap.put("buttonReleased", NunchukServiceButton.getValue(((NunchukServiceButtonPressedEvent) se).button));
			}
			else if(se instanceof NunchukAnalogStickServiceEvent){
				actionMap.put("device","nunchuk");
				actionMap.put("type", "analogStick");
				actionMap.put("angle", ((NunchukAnalogStickServiceEvent) se).getAngle());
				actionMap.put("point", ((NunchukAnalogStickServiceEvent) se).getPoint());
				actionMap.put("tilt", ((NunchukAnalogStickServiceEvent) se).tilt());
			}
				
			
			if(validEvent){	
				
				
			/**
			 * This map splitting should be done for each service (or group of services) of the devices. 
			 * But since for the July 2012 demo we only require the Kinect, this task remains pending.
			 */
				mixedMap.putAll(jointMap);
				mixedMap.putAll(actionMap);
				
				s+= gson.toJson(mixedMap);
				mixedMap = new ConcurrentHashMap<Object,Object>();
				
				if (se instanceof KinectSkeletonServiceEvent){
					jointMap.clear();
				}else if(se instanceof KinectUserActionServiceEvent){
					actionMap.clear();
				}
				
				s+='\n';

		
				
				/*
				 *  ----Start of critical section-----
				 */
			
		
				
					//System.out.println("Wating for the message... ");
				
					//sm.readMessage();
					sendingSem.acquire();
					
				
					System.out.println("Sending: "+s.toString());
						
					
					sm.sendMessage(s);
				
				//sem.release();
			}
		//	if(! (se instanceof KinectUserActionServiceEvent) ){
			//	System.out.println("Sent: "+ s.toString());
		//	}
	
			/*
			 * ----End of critical section-----
			 */
			
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	

		
		
	}
	
	/**
	 * This function catches the information received from the socket and creates/removes launchers depending on the request.
	 * This function also listens to the 'continue' message that the node sends indicating that is ready to be sent new data.
	 */
	public void receiveInstructions(){
	
		String receivedMessage=null;
		while (sm == null) {
			try {
				sm = SocketManager.getSocket(DeviceManager.getDeviceManager().getIpAddress(), DeviceManager.getDeviceManager().getPort());
				
			} catch (Exception e) {
				sm = null;
			}
		}
		
		 try {
			
			receivedMessage= sm.readMessage();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		

		Gson gson = new Gson();
		
		ConcurrentHashMap<Object,Object> receivedMessageConcurrentHashMap = gson.fromJson(receivedMessage, ConcurrentHashMap.class);
	
		if(receivedMessageConcurrentHashMap.containsKey("continue")){
			sendingSem.release();
		}//else 
		if(receivedMessageConcurrentHashMap.containsKey("device")){
			if(((String)(receivedMessageConcurrentHashMap.get("device"))).compareToIgnoreCase("wiiboard")==0){
				if(((String)(receivedMessageConcurrentHashMap.get("action"))).compareToIgnoreCase("start")==0){
					try {
						DeviceManager.getDeviceManager().getWiiBoardLauncher("").addListener(new WiiBoardHandler());
					} catch (Exception e) {
						e.printStackTrace();
					}
					
				}
				if(((String)(receivedMessageConcurrentHashMap.get("action"))).compareToIgnoreCase("stop")==0){
					DeviceManager.getDeviceManager().dropWiiBoard(DeviceManager.getDeviceManager().getCreatedWiiBoardLauncher());
				}else if(((String)(receivedMessageConcurrentHashMap.get("action"))).compareToIgnoreCase("ledOn")==0){
					DeviceManager.getDeviceManager().wiiBoard.setLight(true);
				}else if(((String)(receivedMessageConcurrentHashMap.get("action"))).compareToIgnoreCase("ledOff")==0){
					DeviceManager.getDeviceManager().wiiBoard.setLight(false);
				}
			}else if(((String)(receivedMessageConcurrentHashMap.get("device"))).compareToIgnoreCase("wiimote")==0){
				
				int label = Integer.parseInt((String)receivedMessageConcurrentHashMap.get("label"));
				if(((String)(receivedMessageConcurrentHashMap.get("action"))).compareToIgnoreCase("startTypical")==0){
					try {
						DeviceManager.getDeviceManager().getWiiMoteAccelerationLauncher("", label).addListener(new WiiMoteAccelerationHandler());
						DeviceManager.getDeviceManager().getWiiMoteButtonsLauncher("", label).addListener(new WiiMoteButtonsHandler());		
						DeviceManager.getDeviceManager().getWiiMoteRotationLauncher(label).addListener(new WiiMoteRotationHandler());
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				if(((String)(receivedMessageConcurrentHashMap.get("action"))).compareToIgnoreCase("startAcceleration")==0){
					try {
						DeviceManager.getDeviceManager().getWiiMoteAccelerationLauncher("", label).addListener(new WiiMoteAccelerationHandler());
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				if(((String)(receivedMessageConcurrentHashMap.get("action"))).compareToIgnoreCase("startButtons")==0){
					try {
						DeviceManager.getDeviceManager().getWiiMoteButtonsLauncher("", label).addListener(new WiiMoteButtonsHandler());		
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				if(((String)(receivedMessageConcurrentHashMap.get("action"))).compareToIgnoreCase("startRotation")==0){
					try {
						DeviceManager.getDeviceManager().getWiiMoteRotationLauncher(label).addListener(new WiiMoteRotationHandler());
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				if(((String)(receivedMessageConcurrentHashMap.get("action"))).compareToIgnoreCase("startIR")==0){
					try {
						DeviceManager.getDeviceManager().getWiiMoteIRLauncher("", label).addListener(new WiiMoteIRHandler());
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				if(((String)(receivedMessageConcurrentHashMap.get("action"))).compareToIgnoreCase("stopAcceleration")==0){
					try {
						DeviceManager.getDeviceManager().dropWiiMote(label, DeviceManager.getDeviceManager().getCreatedWiiMoteAccelerationLauncher().get(label));
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				if(((String)(receivedMessageConcurrentHashMap.get("action"))).compareToIgnoreCase("stopButtons")==0){
					try {
						DeviceManager.getDeviceManager().dropWiiMote(label, DeviceManager.getDeviceManager().getCreatedWiiMoteButtonsLauncher().get(label));
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				if(((String)(receivedMessageConcurrentHashMap.get("action"))).compareToIgnoreCase("stopRotation")==0){
					try {
						DeviceManager.getDeviceManager().dropWiiMote(label, DeviceManager.getDeviceManager().getCreatedWiiMoteRotationLauncher().get(label));
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				if(((String)(receivedMessageConcurrentHashMap.get("action"))).compareToIgnoreCase("stopIR")==0){
					try {
						DeviceManager.getDeviceManager().dropWiiMote(label, DeviceManager.getDeviceManager().getCreatedWiiMoteIRLauncher().get(label));
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				if(((String)(receivedMessageConcurrentHashMap.get("action"))).compareToIgnoreCase("stopTypical")==0){
					try {
						DeviceManager.getDeviceManager().dropWiiMote(label, DeviceManager.getDeviceManager().getCreatedWiiMoteAccelerationLauncher().get(label));
						DeviceManager.getDeviceManager().dropWiiMote(label, DeviceManager.getDeviceManager().getCreatedWiiMoteButtonsLauncher().get(label));
						DeviceManager.getDeviceManager().dropWiiMote(label, DeviceManager.getDeviceManager().getCreatedWiiMoteRotationLauncher().get(label));
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				if(((String)(receivedMessageConcurrentHashMap.get("action"))).compareToIgnoreCase("vibrateForTime")==0){
					int vibrationTime = Integer.parseInt((String)receivedMessageConcurrentHashMap.get("vibrationTime"));
					try {
						DeviceManager.getDeviceManager().wiiMoteCreated.get(label).vibrateForTime(vibrationTime);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}	
				if(((String)(receivedMessageConcurrentHashMap.get("action"))).compareToIgnoreCase("changeLeds")==0){
					int ledValue = Integer.parseInt((String)receivedMessageConcurrentHashMap.get("ledValue"));
					try {
						DeviceManager.getDeviceManager().wiiMoteCreated.get(label).setLED(ledValue);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			
			}else if(((String)(receivedMessageConcurrentHashMap.get("device"))).compareToIgnoreCase("nunchuk")==0){
				
				int label = Integer.parseInt((String)receivedMessageConcurrentHashMap.get("label"));
				try {
					if(((String)(receivedMessageConcurrentHashMap.get("action"))).compareToIgnoreCase("startAll")==0){
							DeviceManager.getDeviceManager().getNunchukAccelerationLauncher(label).addListener(new NunchukAccelerationHandler());
							DeviceManager.getDeviceManager().getNunchukAnalogStickLauncher(label).addListener(new NunchukAnalogStickHandler());
							DeviceManager.getDeviceManager().getNunchukButtonsLauncher(label).addListener(new NunchukButtonsHandler());
					}
					if(((String)(receivedMessageConcurrentHashMap.get("action"))).compareToIgnoreCase("startAcceleration")==0){
						
							DeviceManager.getDeviceManager().getNunchukAccelerationLauncher(label).addListener(new NunchukAccelerationHandler());
					}
					if(((String)(receivedMessageConcurrentHashMap.get("action"))).compareToIgnoreCase("startButtons")==0){
						
							DeviceManager.getDeviceManager().getNunchukButtonsLauncher(label).addListener(new NunchukButtonsHandler());
						
					}
					if(((String)(receivedMessageConcurrentHashMap.get("action"))).compareToIgnoreCase("startAnalogStick")==0){
					
							DeviceManager.getDeviceManager().getNunchukAnalogStickLauncher(label).addListener(new NunchukAnalogStickHandler());
						
					}
					if(((String)(receivedMessageConcurrentHashMap.get("action"))).compareToIgnoreCase("stopAcceleration")==0){
						
							DeviceManager.getDeviceManager().dropNunchuk(label, DeviceManager.getDeviceManager().getCreatedNunchukAccelerationLauncher().get(label));
						
					}
					if(((String)(receivedMessageConcurrentHashMap.get("action"))).compareToIgnoreCase("stopButtons")==0){
						
							DeviceManager.getDeviceManager().dropNunchuk(label, DeviceManager.getDeviceManager().getCreatedNunchukButtonsLauncher().get(label));
					
					}
					if(((String)(receivedMessageConcurrentHashMap.get("action"))).compareToIgnoreCase("stopAnalogStick")==0){
						
							DeviceManager.getDeviceManager().dropNunchuk(label, DeviceManager.getDeviceManager().getCreatedNunchukAnalogStickLauncher().get(label));
						
					}
					if(((String)(receivedMessageConcurrentHashMap.get("action"))).compareToIgnoreCase("stop")==0){
						DeviceManager.getDeviceManager().setNunchukDisabled(label);
					}			
				}catch(Exception e){
					e.printStackTrace();
				}
			}else if(((String)(receivedMessageConcurrentHashMap.get("device"))).compareToIgnoreCase("kinect")==0){
				
				if(((String)(receivedMessageConcurrentHashMap.get("action"))).compareToIgnoreCase("start")==0){
					try{
						if(((String)(receivedMessageConcurrentHashMap.get("type"))).compareToIgnoreCase("skeleton")==0){
							DeviceManager.getDeviceManager().getKinectSkeletonLauncher(1).addListener(new KinectSkeletonJointsHandler());						
						}else if(((String)(receivedMessageConcurrentHashMap.get("type"))).compareToIgnoreCase("motor")==0){
							DeviceManager.getDeviceManager().getKinectMotorLauncher().addListener(new KinectMotorHandler());
						}else if(((String)(receivedMessageConcurrentHashMap.get("type"))).compareToIgnoreCase("gameControl")==0){
							DeviceManager.getDeviceManager().getKinectUserGameControlLauncher(1).addListener(new KinectPoseHandler());	
						}else if(((String)(receivedMessageConcurrentHashMap.get("type"))).compareToIgnoreCase("hug")==0){
							DeviceManager.getDeviceManager().getKinectUserHugLauncher(1).addListener(new KinectPoseHandler());	
						}else if(((String)(receivedMessageConcurrentHashMap.get("type"))).compareToIgnoreCase("movement")==0){
							DeviceManager.getDeviceManager().getKinectUserMovementLauncher(1).addListener(new KinectPoseHandler());	
						}else if(((String)(receivedMessageConcurrentHashMap.get("type"))).compareToIgnoreCase("pickedUpFromSides")==0){
							DeviceManager.getDeviceManager().getKinectUserPickedUpFromSidesLauncher(1).addListener(new KinectPoseHandler());
						}else if(((String)(receivedMessageConcurrentHashMap.get("type"))).compareToIgnoreCase("all")==0){
							DeviceManager.getDeviceManager().getKinectSkeletonLauncher(1).addListener(new KinectSkeletonJointsHandler());
							DeviceManager.getDeviceManager().getKinectMotorLauncher().addListener(new KinectMotorHandler());
							DeviceManager.getDeviceManager().getKinectMotorLauncher().addListener(new KinectPoseHandler());
							DeviceManager.getDeviceManager().getKinectUserGameControlLauncher(1).addListener(new KinectPoseHandler());	
							DeviceManager.getDeviceManager().getKinectUserHugLauncher(1).addListener(new KinectPoseHandler());	
							DeviceManager.getDeviceManager().getKinectUserMovementLauncher(1).addListener(new KinectPoseHandler());	
							DeviceManager.getDeviceManager().getKinectUserPickedUpFromSidesLauncher(1).addListener(new KinectPoseHandler());	
						
						}
					}catch(Exception e){
						e.printStackTrace();
					}
					
				}
				if(((String)(receivedMessageConcurrentHashMap.get("action"))).compareToIgnoreCase("stop")==0){
					
					if(((String)(receivedMessageConcurrentHashMap.get("type"))).compareToIgnoreCase("skeleton")==0){
						DeviceManager.getDeviceManager().dropKinect(DeviceManager.getDeviceManager().getCreatedKinectSkeletonLauncher());
					}else if(((String)(receivedMessageConcurrentHashMap.get("type"))).compareToIgnoreCase("motor")==0){
						DeviceManager.getDeviceManager().dropKinect(DeviceManager.getDeviceManager().getCreatedKinectMotorLauncher());
					}else if(((String)(receivedMessageConcurrentHashMap.get("type"))).compareToIgnoreCase("pose")==0){
						DeviceManager.getDeviceManager().dropKinect(DeviceManager.getDeviceManager().getCreatedKinectPoseLauncher());
					}else if(((String)(receivedMessageConcurrentHashMap.get("type"))).compareToIgnoreCase("movement")==0){
						DeviceManager.getDeviceManager().dropKinect(DeviceManager.getDeviceManager().getCreatedKinectUserMovementLauncher());
					}else if(((String)(receivedMessageConcurrentHashMap.get("type"))).compareToIgnoreCase("hug")==0){
						DeviceManager.getDeviceManager().dropKinect(DeviceManager.getDeviceManager().getCreatedKinectUserHugLauncher());
					}else if(((String)(receivedMessageConcurrentHashMap.get("type"))).compareToIgnoreCase("gameControl")==0){
						DeviceManager.getDeviceManager().dropKinect(DeviceManager.getDeviceManager().getCreatedKinectUserGameControlLauncher());
					}else if(((String)(receivedMessageConcurrentHashMap.get("type"))).compareToIgnoreCase("pickUpFromSides")==0){
						DeviceManager.getDeviceManager().dropKinect(DeviceManager.getDeviceManager().getCreatedKinectUserPickedUpFromSidesLauncher());
					}else if(((String)(receivedMessageConcurrentHashMap.get("type"))).compareToIgnoreCase("all")==0){
						DeviceManager.getDeviceManager().dropKinect(DeviceManager.getDeviceManager().getCreatedKinectUserPickedUpFromSidesLauncher());
					}
				}
				if(((String)(receivedMessageConcurrentHashMap.get("action"))).compareToIgnoreCase("autoAdjust")==0){
					try {
						DeviceManager.getDeviceManager().adjustKinectForTheBestTilt(1);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				if(((String)(receivedMessageConcurrentHashMap.get("action"))).compareToIgnoreCase("changeLed")==0){
					String status = (String) receivedMessageConcurrentHashMap.get("status");
					try {
						DeviceManager.getDeviceManager().getKinectManager().getMotorCommunicator().setLED(LEDStatus.valueOf(status));
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				if(((String)(receivedMessageConcurrentHashMap.get("action"))).compareToIgnoreCase("changeTilt")==0){
					int angle = Integer.parseInt((String)receivedMessageConcurrentHashMap.get("angle"));
					try {
						DeviceManager.getDeviceManager().getKinectManager().getMotorCommunicator().setAngle(angle);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			
				//other functions
			} else if (((String)(receivedMessageConcurrentHashMap.get("device"))).compareToIgnoreCase("nonin")==0){
				
				if(((String)(receivedMessageConcurrentHashMap.get("action"))).compareToIgnoreCase("start")==0){
					
					try {
						DeviceManager.getDeviceManager().getNoninLauncher("").addListener(new NoninHandler());
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
	
				if(((String)(receivedMessageConcurrentHashMap.get("action"))).compareToIgnoreCase("stop")==0){
					
					try {
						DeviceManager.getDeviceManager().dropNonin(DeviceManager.getDeviceManager().getCreatedNoninLauncher());
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		
		}
		
	}


	@Override
	public void run() {
		while(true){
			receiveInstructions();
		}
		
	}
	
}