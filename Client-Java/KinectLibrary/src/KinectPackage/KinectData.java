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

package KinectPackage;

/**
 * Contains all the Kinect data.
 * 
 * @author Santiago Hors Fraile
 * 
 */
public class KinectData {

	private MotorCommunicator motorCommunicator;
	private SkeletonManager skeletonManager;

	/**
	 * Creates a new KinecData object and initializes it with the given
	 * parameters.
	 * 
	 * @param motorCommunicator
	 *            The motorCommunicator of the Kinect.
	 * @param skeletonManager
	 *            The skeletonManager of the Kinect.
	 */
	public KinectData(MotorCommunicator motorCommunicator,
			SkeletonManager skeletonManager) {

		this.setMotorCommunicator(motorCommunicator);
		this.setSkeletonManager(skeletonManager);

	}

	/**
	 * @return the motorCommunicator
	 */
	public MotorCommunicator getMotorCommunicator() {
		return motorCommunicator;
	}

	/**
	 * @param motorCommunicator
	 *            the motorCommunicator to set
	 */
	public void setMotorCommunicator(MotorCommunicator motorCommunicator) {
		this.motorCommunicator = motorCommunicator;
	}

	/**
	 * @return the skeletonManager
	 */
	public SkeletonManager getSkeletonManager() {
		return skeletonManager;
	}

	/**
	 * @param skeletonManager
	 *            the skeletonManager to set
	 */
	public void setSkeletonManager(SkeletonManager skeletonManager) {
		this.skeletonManager = skeletonManager;
	}

}
