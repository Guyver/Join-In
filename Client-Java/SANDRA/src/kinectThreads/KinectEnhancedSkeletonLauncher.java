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

import control.SharedSocket;

import services.KinectSkeletonServiceEvent;
import launchers.KinectSkeletonLauncher;
/**
 * This class provides enhanced functionalities to the KinectSkeletonLauncher class like the socket-based data sending. 
 * @author Santiago Hors Fraile
 *
 */
public class KinectEnhancedSkeletonLauncher extends KinectSkeletonLauncher implements Runnable{
/**
 * The last state of the user's skeleton
 */
	private  KinectSkeletonServiceEvent lastKinectSkeletonServiceEvent;

/**
 * This field represents the serialized socket output.
 */
	private SharedSocket sharedOutput;

	/**
	 * Default constructor
	 */
	public KinectEnhancedSkeletonLauncher(){
		super();

	
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
	 * This function is executed continuously in the thread. It establishes a connection to the socket, waits for a message of the server
	 * to send the skeleton message. 
	 */
	@Override
	public void run() {
		while (true) {
			
			sharedOutput.performTransference(lastKinectSkeletonServiceEvent);
			
		}
	}

}
