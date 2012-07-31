/**
 * Copyright 2012 Santiago Hors Fraile,
 
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
package KinectPackage;

/**
 * Defines the Kinect event.
 * @author Santiago Hors Fraile
 */
public class KinectDataEvent {
	/**
	 * Represents all the Kinect state data for the current moment. 
	 */
	private KinectData kinectData;
	
	/**
	 * Represents the identification of the user whose Kinect data is being sent.
	 */
	private int userId;

	/**
	 * Creates a new KinectEvent and initializes with the given parameters.
	 * @param userId The user we refer to.
	 * @param kinectData The new data about the Kinect for the current moment.
	 */
	public KinectDataEvent (int userId, KinectData kinectData ){
		setUserId(userId);
		setKinectData(kinectData);
	}
	/**
	 * @return the kinectData
	 */
	public KinectData getKinectData() {
		return kinectData;
	}

	/**
	 * @param kinectData the kinectData to set
	 */
	public void setKinectData(KinectData kinectData) {
		this.kinectData = kinectData;
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



	

}
