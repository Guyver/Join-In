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


import org.OpenNI.StatusException;

import control.DeviceManager;
import control.SharedSocket;

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
	private  KinectSkeletonServiceEvent lastKinectSkeletonServiceEvent;
/**
 * A boolean variable which defines whether the specified user is valid (is within the Kinect scope and has been calibrated) or not.
 */
	private boolean validUser;
/**
 * This field represents the serialized socket output.
 */
	private SharedSocket sharedOutput;

	/**
	 * Default constructor
	 */
	public KinectEnhacedSkeletonLauncher(){
		sharedOutput= SharedSocket.getSharedSocket();
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
		sharedOutput= SharedSocket.getSharedSocket();
	
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
	public void setLastKinectSkeletonServiceEvent(KinectSkeletonServiceEvent lastKinectSkeletonServiceEvent) {
		this.lastKinectSkeletonServiceEvent = lastKinectSkeletonServiceEvent;
	}

	/**
	 * This function is executed continuously in the thread. It stablishes a connection to the socket, waits for a message of the server
	 * to send the skeleton message. 
	 */
	@Override
	public void run() {
		while (true) {
			if (validUser) {			
				sharedOutput.performTransference(lastKinectSkeletonServiceEvent);
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
	
}
