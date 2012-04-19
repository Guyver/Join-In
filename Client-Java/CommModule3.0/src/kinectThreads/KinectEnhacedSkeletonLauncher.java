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
package kinectThreads;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.vecmath.Vector3d;

import org.OpenNI.SkeletonJoint;
import org.OpenNI.StatusException;

import com.google.gson.Gson;

import control.DeviceManager;
import control.SocketUtils;
import services.KinectSkeletonServiceEvent;
import launchers.KinectSkeletonLauncher;
/**
 * This class provides enhaced functionalities to the KinectSkeletonLauncher class like the socket-based data sending. 
 * @author Santiago Hors Fraile
 *
 */
public class KinectEnhacedSkeletonLauncher extends KinectSkeletonLauncher implements Runnable{
/**
 * The last state of the user's skeleton
 */
	private KinectSkeletonServiceEvent lastKinectSkeletonServiceEvent;
/**
 * A boolean variable which defines whether the specified user is valid (is within the Kinect scope and has been calibrated) or not.
 */
	private boolean validUser;

	/**
	 * Default constructor
	 */
	public KinectEnhacedSkeletonLauncher(){
		
		validUser=false;
		lastKinectSkeletonServiceEvent=null;
	}
	/**
	 * Constructor with parameters
	 * @param userId The ID of the user we want to track.
	 */
	public KinectEnhacedSkeletonLauncher(int userId){
		super.setUserId(userId);
		validUser= isUserRegistered();
		
	
		lastKinectSkeletonServiceEvent=null;
		
		
	}
	
	/**
	 * @return the lastKinectSkeletonServiceEvent
	 */
	public KinectSkeletonServiceEvent getLastKinectSkeletonServiceEvent() {
		return lastKinectSkeletonServiceEvent;
	}

	/**
	 * @param lastKinectSkeletonServiceEvent the lastKinectSkeletonServiceEvent to set
	 */
	public void setLastKinectSkeletonServiceEvent(
			KinectSkeletonServiceEvent lastKinectSkeletonServiceEvent) {
		this.lastKinectSkeletonServiceEvent = lastKinectSkeletonServiceEvent;
	}

	/**
	 * This function is executed continuously in the thread. It stablishes a connection to the socket, waits for a message of the server
	 * to send the skeleton message. 
	 */
	public void run() {
		while (true) {
			
			SocketUtils su = SocketUtils.getSocket(DeviceManager.getDeviceManager().getIpAddress(), DeviceManager.getDeviceManager().getPort());
		
			if (validUser) {
				try {	
						
						su.readMessage();
						
						performTransference();
										
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	/**
	 * Returns a boolean which defines whether the specified user is valid (is within the Kinect scope and has been calibrated) or not.
	 * @return The boolean boolean which defines whether the specified user is valid (is within the Kinect scope and has been calibrated) or not.
	 */
	private boolean isUserRegistered(){
		
		boolean found= false;
		int numberOfUsers=DeviceManager.getDeviceManager().getKinectManager().getSkeletonManager().getUserGenerator().getNumberOfUsers();
		try {
			int[] users=DeviceManager.getDeviceManager().getKinectManager().getSkeletonManager().getUserGenerator().getUsers();
			int i=0; 
			
			while(i<numberOfUsers&&!found){
				if(users[i]==super.getUserId()){
					found=true;
					DeviceManager.getDeviceManager().waitForUserIsCalibrated(super.getUserId());
				}
				i++;
				
			}
		} catch (StatusException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return found;
	}
	
	private void performTransference(){
	
		SocketUtils su= SocketUtils.getSocket(DeviceManager.getDeviceManager().getIpAddress(), DeviceManager.getDeviceManager().getPort());
		
		while (su == null) {
		
			try {
				
				su = SocketUtils.getSocket(DeviceManager.getDeviceManager().getIpAddress(), DeviceManager.getDeviceManager().getPort());
			} catch (Exception e) {
				su = null;
			}
		}
		try {
	
			
			Gson gson = new Gson();
			String s ="" ;
			//System.out.println("Imprimiendo "+lastKinectSkeletonServiceEvent.getRightHand().toString());
			if(lastKinectSkeletonServiceEvent instanceof KinectSkeletonServiceEvent){
			
				
				Map<SkeletonJoint, Vector3d> map = new HashMap<SkeletonJoint, Vector3d>();
				map.put(SkeletonJoint.HEAD, ((KinectSkeletonServiceEvent) lastKinectSkeletonServiceEvent).getHead());
				map.put(SkeletonJoint.LEFT_ELBOW, ((KinectSkeletonServiceEvent) lastKinectSkeletonServiceEvent).getLeftElbow());
				map.put(SkeletonJoint.LEFT_FOOT, ((KinectSkeletonServiceEvent) lastKinectSkeletonServiceEvent).getLeftFoot());
				map.put(SkeletonJoint.LEFT_HAND, ((KinectSkeletonServiceEvent) lastKinectSkeletonServiceEvent).getLeftHand());
				map.put(SkeletonJoint.LEFT_HIP, ((KinectSkeletonServiceEvent) lastKinectSkeletonServiceEvent).getLeftHip());
				map.put(SkeletonJoint.LEFT_KNEE, ((KinectSkeletonServiceEvent) lastKinectSkeletonServiceEvent).getLeftKnee());
				map.put(SkeletonJoint.LEFT_SHOULDER, ((KinectSkeletonServiceEvent) lastKinectSkeletonServiceEvent).getLeftShoulder());
				map.put(SkeletonJoint.NECK, ((KinectSkeletonServiceEvent) lastKinectSkeletonServiceEvent).getNeck());
				map.put(SkeletonJoint.RIGHT_ELBOW, ((KinectSkeletonServiceEvent) lastKinectSkeletonServiceEvent).getRightElbow());
				map.put(SkeletonJoint.RIGHT_FOOT, ((KinectSkeletonServiceEvent) lastKinectSkeletonServiceEvent).getRightFoot());
				map.put(SkeletonJoint.RIGHT_HAND, ((KinectSkeletonServiceEvent) lastKinectSkeletonServiceEvent).getRightHand());
				map.put(SkeletonJoint.RIGHT_HIP, ((KinectSkeletonServiceEvent) lastKinectSkeletonServiceEvent).getRightHip());
				map.put(SkeletonJoint.RIGHT_KNEE, ((KinectSkeletonServiceEvent) lastKinectSkeletonServiceEvent).getRightKnee());
				map.put(SkeletonJoint.RIGHT_SHOULDER, ((KinectSkeletonServiceEvent) lastKinectSkeletonServiceEvent).getRightShoulder());
				map.put(SkeletonJoint.TORSO, ((KinectSkeletonServiceEvent) lastKinectSkeletonServiceEvent).getTorso());
				s= gson.toJson(map);
			}
	
			
			s+='\n';
			
			su.sendMessage(s);
			
			//System.out.println("Sending: "+s.toString());
		
		} catch (Exception e) {
	
			e.printStackTrace();
		}
	}
}
