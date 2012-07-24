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


import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;

import kinectThreads.KinectUserActionEnum;



import services.KinectUserActionServiceEvent;


import com.google.gson.Gson;



/**
 * This class maps the data of the device events and codifies it under the GSON/JSON protocol before sending it to the 
 * specified IP address (and port) through a socket.
 * @author Santiago Hors Fraile
 *
 */
public class SharedOutput {
	/**
	 * This object is the only object that can exist of this class. This object
	 * has to be the one used to send the events through the socket to the remote server.
	 */
	static private SharedOutput rc = null;
	/**
	 * This field represents the SocketManager of the socket we have stablished the communication through.
	 */
	private SocketManager sm = null;
	/**
	 * This field serializes the output
	 */
	private static Semaphore  sem=null;
	
	
	static ConcurrentHashMap<Object, Object> actionMap; 
	static ConcurrentHashMap<Object, Object> lastMovementMap; 
	
	/**
	 * 
	 * Returns a new instance of SharedOutupu if it did not exist or the
	 * instance that was created if there was a created instance already.
	 * 
	 * @return SharedOutput
	 */
	static public SharedOutput getSharedOutput() {
		
	        if (rc == null) {
	            rc = new SharedOutput();
	            sem= new Semaphore(1, true);
	            actionMap= new ConcurrentHashMap<Object, Object>();
	            lastMovementMap= new ConcurrentHashMap<Object, Object>();
	            lastMovementMap.put("standStill","true");
	            lastMovementMap.put("walk", "false");
	            lastMovementMap.put("run", "false");
	           
	      
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
						
				String action= ((KinectUserActionServiceEvent)se).getUserAction();
	
				if(action.compareTo(KinectUserActionEnum.STAND.name())==0){
					actionMap.put("type","standStill");
				}else if(action.compareTo(KinectUserActionEnum.WALK.name())==0){
					actionMap.put("type","walk");
				}else if(action.compareTo(KinectUserActionEnum.RUN.name())==0){
					actionMap.put("type","run");
				}else if(action.compareTo(KinectUserActionEnum.PAUSE.name())==0){
					actionMap.put("type", "pause");
				}else if(action.compareTo(KinectUserActionEnum.RESUME.name())==0){
					actionMap.put("type", "resume");
				}else if(action.compareTo(KinectUserActionEnum.HUG.name())==0){
					actionMap.put("type", "hug");
				}
			}

			
			
			if(validEvent){	
				s+= gson.toJson(actionMap);
			
				s+='\n';
				
				/*
				 *  ----Start of critical section-----
				 */
				try {
					sem.acquire();
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
				System.out.println("Wating for the message...");
				sm.readMessage();
				System.out.println("Message received, sending: "+s.toString());
				sm.sendMessage(s);
				sem.release();
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
	
	
	
	
}