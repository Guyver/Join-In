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
package services;

import kinectThreads.KinectPoseEnum;
import control.IEventCommModule;
/**
 * Defines the KinectPose events
 * @author Santiago Hors Fraile
 *
 */
public class KinectPoseServiceEvent implements IEventCommModule  {
	
	private int userId;
	private KinectPoseEnum kinectPose;
	private boolean posing;
	
	public KinectPoseServiceEvent(int userId, KinectPoseEnum kinectPose, boolean posing){
		this.setKinectPose(kinectPose);
		this.setUserId(userId);
		this.setPosing(posing);
	}
	
	/**
	 * @return the userId
	 */
	public int getUserId() {
		return userId;
	}
	/**
	 * @param userId the userId to set
	 */
	public void setUserId(int userId) {
		this.userId = userId;
	}
	/**
	 * @return the kinectPose
	 */
	public KinectPoseEnum getKinectPose() {
		return kinectPose;
	}
	/**
	 * @param kinectPose the kinectPose to set
	 */
	public void setKinectPose(KinectPoseEnum kinectPose) {
		this.kinectPose = kinectPose;
	}

	/**
	 * @return the posing
	 */
	public boolean isPosing() {
		return posing;
	}

	/**
	 * @param posing the posing to set
	 */
	public void setPosing(boolean posing) {
		this.posing = posing;
	}
	
	

}
