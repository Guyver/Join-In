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

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Semaphore;

import kinectThreads.KinectUserActionEnum;
import kinectThreads.KinectUserActionEnum;

import org.OpenNI.SkeletonJoint;

import services.KinectUserActionServiceEvent;

import services.KinectSkeletonServiceEvent;

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
	
	/**
	 * This field stores the last kind of the user's movement: walk, run, stand, go backwards, etc
	 */
	private KinectUserActionEnum lastMovement;
	/**
	 * This field stores the last kind of user's rotation: to the left, to the right or no rotation.
	 */
	private KinectUserActionEnum lastRotation; 
	
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
			Map<Object, Object> map = new HashMap<Object, Object>();
			map.put("device", "kinect");
				
			/*
			 * Map the joint positions
			 */
			if(se instanceof KinectSkeletonServiceEvent){
				validEvent=true;
				
				
				map.put("userID", ((KinectSkeletonServiceEvent) se).getUserId());
				map.put("type", "jointPosition");
				map.put(SkeletonJoint.HEAD, ((KinectSkeletonServiceEvent) se).getHead());
				map.put(SkeletonJoint.LEFT_ELBOW, ((KinectSkeletonServiceEvent) se).getLeftElbow());
				map.put(SkeletonJoint.LEFT_FOOT, ((KinectSkeletonServiceEvent) se).getLeftFoot());
				map.put(SkeletonJoint.LEFT_HAND, ((KinectSkeletonServiceEvent) se).getLeftHand());
				map.put(SkeletonJoint.LEFT_HIP, ((KinectSkeletonServiceEvent) se).getLeftHip());
				map.put(SkeletonJoint.LEFT_KNEE, ((KinectSkeletonServiceEvent) se).getLeftKnee());
				map.put(SkeletonJoint.LEFT_SHOULDER, ((KinectSkeletonServiceEvent) se).getLeftShoulder());
				map.put(SkeletonJoint.NECK, ((KinectSkeletonServiceEvent) se).getNeck());
				map.put(SkeletonJoint.RIGHT_ELBOW, ((KinectSkeletonServiceEvent) se).getRightElbow());
				map.put(SkeletonJoint.RIGHT_FOOT, ((KinectSkeletonServiceEvent) se).getRightFoot());
				map.put(SkeletonJoint.RIGHT_HAND, ((KinectSkeletonServiceEvent) se).getRightHand());
				map.put(SkeletonJoint.RIGHT_HIP, ((KinectSkeletonServiceEvent) se).getRightHip());
				map.put(SkeletonJoint.RIGHT_KNEE, ((KinectSkeletonServiceEvent) se).getRightKnee());
				map.put(SkeletonJoint.RIGHT_SHOULDER, ((KinectSkeletonServiceEvent) se).getRightShoulder());
				map.put(SkeletonJoint.TORSO, ((KinectSkeletonServiceEvent) se).getTorso());
				
			}
			/*
			 * Map the user's movements
			 */
			if(se instanceof KinectUserActionServiceEvent){
				validEvent=true;
				map.put("userID", ((KinectUserActionServiceEvent) se).getUserId());
				map.put("type", "action");
				
				String action= ((KinectUserActionServiceEvent)se).getUserAction();
				/*
				 * The KinectUserActionServiceEvent can store an action to walk, run, etc or to rotate the user. So,  we discriminate it here. 
				 */
				if(action.compareTo(KinectUserActionEnum.STAND.name())==0){
					lastMovement=KinectUserActionEnum.STAND;
				}else if(action.compareTo(KinectUserActionEnum.WALK.name())==0){
					lastMovement=KinectUserActionEnum.WALK;
				}else if(action.compareTo(KinectUserActionEnum.RUN.name())==0){
					lastMovement=KinectUserActionEnum.RUN;
				}else if(action.compareTo(KinectUserActionEnum.BACKWARDS.name())==0){
					lastMovement=KinectUserActionEnum.BACKWARDS;
				}else if(action.compareTo(KinectUserActionEnum.NO_ROTATION.name())==0){
					lastRotation=KinectUserActionEnum.NO_ROTATION;
				}else if(action.compareTo(KinectUserActionEnum.TURN_LEFT.name())==0){
					lastRotation=KinectUserActionEnum.TURN_LEFT;
				}else if(action.compareTo(KinectUserActionEnum.TURN_RIGHT.name())==0){
					lastRotation=KinectUserActionEnum.TURN_RIGHT;
				}
				
								
				if(lastRotation==KinectUserActionEnum.TURN_LEFT){
					map.put("rotateLeft","true");
					map.put("rotateRight","false");
				
				}else if(lastRotation==KinectUserActionEnum.TURN_RIGHT){	
					map.put("rotateLeft","false");
					map.put("rotateRight", "true");
				}else if(lastRotation== KinectUserActionEnum.NO_ROTATION){
					map.put("rotateLeft","false");
					map.put("rotateRight","false");
				}
				
				if(lastMovement == KinectUserActionEnum.STAND){
					map.put("standStill","true");
					map.put("walk", "false");
					map.put("run", "false");
					map.put("backwards", "false");
				}else if(lastMovement == KinectUserActionEnum.WALK){
					map.put("standStill","false");
					map.put("walk", "true");
					map.put("run", "false");
					map.put("backwards", "false");
				}else if(lastMovement == KinectUserActionEnum.RUN){
					map.put("standStill","false");
					map.put("walk", "false");
					map.put("run", "true");
					map.put("backwards", "false");
				}else if(lastMovement == KinectUserActionEnum.BACKWARDS){
					map.put("standStill","false");
					map.put("walk", "false");
					map.put("run", "false");
					map.put("backwards", "true");
				}
			}
		
			
			if(validEvent){	
				s+= gson.toJson(map);
				s+='\n';
			
				/*
				 *  ----Start of critical section-----
				 */
				try {
					sem.acquire();
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
				String dummyReceivedMessage= sm.readMessage();
				System.out.println("ENVIO: "+s.toString());
				sm.sendMessage(s);
				sem.release();
				/*
				 * ----End of critical section-----
				 */
				if(se instanceof KinectUserActionServiceEvent){
				System.out.println("Sent: "+s.toString());
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	

		
		
	}
	
	
	
	
}