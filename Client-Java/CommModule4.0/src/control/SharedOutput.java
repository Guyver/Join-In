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


import org.OpenNI.SkeletonJoint;

import services.KinectUserActionServiceEvent;
import services.KinectUserHugServiceEvent;

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

	
	static Map<Object, Object> actionMap; 
	static Map<Object, Object> lastMovementMap; 
	
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
	            actionMap= new HashMap<Object, Object>();
	            lastMovementMap= new HashMap<Object, Object>();
	            lastMovementMap.put("standStill","true");
	            lastMovementMap.put("walk", "false");
	            lastMovementMap.put("run", "false");
	            lastMovementMap.put("backwards", "false");
	            lastMovementMap.put("rotateLeft","false");
	            lastMovementMap.put("rotateRight","false");
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
			 * Map the joint positions
			 */
			if(se instanceof KinectSkeletonServiceEvent){
				validEvent=true;
				
				
				actionMap.put("userID",
						((KinectSkeletonServiceEvent) se).getUserId());
				actionMap.put("type", "jointPosition");
				if (((KinectSkeletonServiceEvent) se).getHead().getX() != 0.0
						|| ((KinectSkeletonServiceEvent) se).getHead().getY() != 0.0
						|| ((KinectSkeletonServiceEvent) se).getHead().getZ() != 0.0) {
					actionMap.put(SkeletonJoint.HEAD,
							((KinectSkeletonServiceEvent) se).getHead());
				}
				if (((KinectSkeletonServiceEvent) se).getLeftElbow().getX() != 0.0
						|| ((KinectSkeletonServiceEvent) se).getLeftElbow()
								.getY() != 0.0
						|| ((KinectSkeletonServiceEvent) se).getLeftElbow()
								.getZ() != 0.0) {

					actionMap.put(SkeletonJoint.LEFT_ELBOW,
							((KinectSkeletonServiceEvent) se).getLeftElbow());
				}
				if (((KinectSkeletonServiceEvent) se).getLeftFoot().getX() != 0.0
						|| ((KinectSkeletonServiceEvent) se).getLeftFoot()
								.getY() != 0.0
						|| ((KinectSkeletonServiceEvent) se).getLeftFoot()
								.getZ() != 0.0) {

					actionMap.put(SkeletonJoint.LEFT_FOOT,
							((KinectSkeletonServiceEvent) se).getLeftFoot());
				}
				if (((KinectSkeletonServiceEvent) se).getLeftHand().getX() != 0.0
						|| ((KinectSkeletonServiceEvent) se).getLeftHand()
								.getY() != 0.0
						|| ((KinectSkeletonServiceEvent) se).getLeftHand()
								.getZ() != 0.0) {

					actionMap.put(SkeletonJoint.LEFT_HAND,
							((KinectSkeletonServiceEvent) se).getLeftHand());
				}
				if (((KinectSkeletonServiceEvent) se).getLeftHip().getX() != 0.0
						|| ((KinectSkeletonServiceEvent) se).getLeftHip()
								.getY() != 0.0
						|| ((KinectSkeletonServiceEvent) se).getLeftHip()
								.getZ() != 0.0) {

					actionMap.put(SkeletonJoint.LEFT_HIP,
							((KinectSkeletonServiceEvent) se).getLeftHip());
				}
				if (((KinectSkeletonServiceEvent) se).getLeftKnee().getX() != 0.0
						|| ((KinectSkeletonServiceEvent) se).getLeftKnee()
								.getY() != 0.0
						|| ((KinectSkeletonServiceEvent) se).getLeftKnee()
								.getZ() != 0.0) {

					actionMap.put(SkeletonJoint.LEFT_KNEE,
							((KinectSkeletonServiceEvent) se).getLeftKnee());
				}
				if (((KinectSkeletonServiceEvent) se).getLeftShoulder().getX() != 0.0
						|| ((KinectSkeletonServiceEvent) se).getLeftShoulder()
								.getY() != 0.0
						|| ((KinectSkeletonServiceEvent) se).getLeftShoulder()
								.getZ() != 0.0) {

					actionMap
							.put(SkeletonJoint.LEFT_SHOULDER,
									((KinectSkeletonServiceEvent) se)
											.getLeftShoulder());
				}
				if (((KinectSkeletonServiceEvent) se).getNeck().getX() != 0.0
						|| ((KinectSkeletonServiceEvent) se).getNeck().getY() != 0.0
						|| ((KinectSkeletonServiceEvent) se).getNeck().getZ() != 0.0) {

					actionMap.put(SkeletonJoint.NECK,
							((KinectSkeletonServiceEvent) se).getNeck());
				}
				if (((KinectSkeletonServiceEvent) se).getRightElbow().getX() != 0.0
						|| ((KinectSkeletonServiceEvent) se).getRightElbow()
								.getY() != 0.0
						|| ((KinectSkeletonServiceEvent) se).getRightElbow()
								.getZ() != 0.0) {

					actionMap.put(SkeletonJoint.RIGHT_ELBOW,
							((KinectSkeletonServiceEvent) se).getRightElbow());
				}
				if (((KinectSkeletonServiceEvent) se).getRightFoot().getX() != 0.0
						|| ((KinectSkeletonServiceEvent) se).getRightFoot()
								.getY() != 0.0
						|| ((KinectSkeletonServiceEvent) se).getRightFoot()
								.getZ() != 0.0) {

					actionMap.put(SkeletonJoint.RIGHT_FOOT,
							((KinectSkeletonServiceEvent) se).getRightFoot());
				}
				if (((KinectSkeletonServiceEvent) se).getRightHand().getX() != 0.0
						|| ((KinectSkeletonServiceEvent) se).getRightHand()
								.getY() != 0.0
						|| ((KinectSkeletonServiceEvent) se).getRightHand()
								.getZ() != 0.0) {

					actionMap.put(SkeletonJoint.RIGHT_HAND,
							((KinectSkeletonServiceEvent) se).getRightHand());
				}
				if (((KinectSkeletonServiceEvent) se).getRightHip().getX() != 0.0
						|| ((KinectSkeletonServiceEvent) se).getRightHip()
								.getY() != 0.0
						|| ((KinectSkeletonServiceEvent) se).getRightHip()
								.getZ() != 0.0) {

					actionMap.put(SkeletonJoint.RIGHT_HIP,
							((KinectSkeletonServiceEvent) se).getRightHip());
				}
				if (((KinectSkeletonServiceEvent) se).getRightKnee().getX() != 0.0
						|| ((KinectSkeletonServiceEvent) se).getRightKnee()
								.getY() != 0.0
						|| ((KinectSkeletonServiceEvent) se).getRightKnee()
								.getZ() != 0.0) {

					actionMap.put(SkeletonJoint.RIGHT_KNEE,
							((KinectSkeletonServiceEvent) se).getRightKnee());
				}
				if (((KinectSkeletonServiceEvent) se).getRightShoulder().getX() != 0.0
						|| ((KinectSkeletonServiceEvent) se).getRightShoulder()
								.getY() != 0.0
						|| ((KinectSkeletonServiceEvent) se).getRightShoulder()
								.getZ() != 0.0) {

					actionMap.put(SkeletonJoint.RIGHT_SHOULDER,
							((KinectSkeletonServiceEvent) se)
									.getRightShoulder());
				}
				if (((KinectSkeletonServiceEvent) se).getTorso().getX() != 0.0
						|| ((KinectSkeletonServiceEvent) se).getTorso().getY() != 0.0
						|| ((KinectSkeletonServiceEvent) se).getTorso().getZ() != 0.0) {
					actionMap.put(SkeletonJoint.TORSO,
							((KinectSkeletonServiceEvent) se).getTorso());
				}
					
				actionMap.putAll(lastMovementMap);
				
			}
			/*
			 * Map the user's movements
			 */
			if(se instanceof KinectUserActionServiceEvent){
				validEvent=false;
				actionMap.put("userID", ((KinectUserActionServiceEvent) se).getUserId());
				actionMap.put("type", "action");
				
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
					lastMovementMap.put("rotateLeft","true");
					lastMovementMap.put("rotateRight","false");
				
				}else if(lastRotation==KinectUserActionEnum.TURN_RIGHT){	
					lastMovementMap.put("rotateLeft","false");
					lastMovementMap.put("rotateRight", "true");
				}else if(lastRotation== KinectUserActionEnum.NO_ROTATION){
					lastMovementMap.put("rotateLeft","false");
					lastMovementMap.put("rotateRight","false");
				}
				
				if(lastMovement == KinectUserActionEnum.STAND){
					lastMovementMap.put("standStill","true");
					lastMovementMap.put("walk", "false");
					lastMovementMap.put("run", "false");
					lastMovementMap.put("backwards", "false");
				}else if(lastMovement == KinectUserActionEnum.WALK){
					lastMovementMap.put("standStill","false");
					lastMovementMap.put("walk", "true");
					lastMovementMap.put("run", "false");
					lastMovementMap.put("backwards", "false");
				}else if(lastMovement == KinectUserActionEnum.RUN){
					lastMovementMap.put("standStill","false");
					lastMovementMap.put("walk", "false");
					lastMovementMap.put("run", "true");
					lastMovementMap.put("backwards", "false");
				}else if(lastMovement == KinectUserActionEnum.BACKWARDS){
					lastMovementMap.put("standStill","false");
					lastMovementMap.put("walk", "false");
					lastMovementMap.put("run", "false");
					lastMovementMap.put("backwards", "true");
				}
				lastMovementMap=actionMap;
		
			}
		
			if(se instanceof KinectUserHugServiceEvent){
				validEvent=true;
				actionMap.put("userID", ((KinectUserHugServiceEvent) se).getUserId());
				actionMap.put("type", "hug");

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
			//	System.out.println("Waiting message");
				String dummyReceivedMessage= sm.readMessage();
				
				sm.sendMessage(s);
				sem.release();
			//	if(! (se instanceof KinectUserActionServiceEvent) ){
				//	System.out.println("Sent: "+ s.toString());
			//	}
		
				/*
				 * ----End of critical section-----
				 */
				
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	

		
		
	}
	
	
	
	
}