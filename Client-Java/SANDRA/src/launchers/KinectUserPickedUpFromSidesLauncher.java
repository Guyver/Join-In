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

package launchers;


import iservices.IKinectPoseService;
import kinectThreads.KinectPoseEnum;
import control.DeviceManager;
import control.LauncherWrapper;
/**
 * A Kinect event thrower facade. 
 * @author Santiago Hors Fraile
 */
public class KinectUserPickedUpFromSidesLauncher extends LauncherWrapper {

	/**
	 * This field represents the launcher of the events of having the left shoulder lower and closer to the camera than the right shoulder.
	 */
	KinectPoseLauncher kplLeftShoulderLowerAndCloser;
	/**
	 * This field represents the launcher of the events of having the right shoulder lower and closer to the camera than the left shoulder.
	 */
	KinectPoseLauncher kplRightShoulderLowerAndCloser;

	/**
	 * 
	 * @return the launcher of events of the pose of having your left shoulder lower and closer to the camera than the right shoulder.
	 */
	public KinectPoseLauncher getKinectPoseLauncherLeftShoulderLowerAndCloser(){
		return kplLeftShoulderLowerAndCloser;
	}	
	/**
	 * 
	 * @return the launcher of events of the pose of having your right shoulder lower and closer to the camera than the left shoulder.
	 */
	public KinectPoseLauncher getKinectPoseLauncherRightShoulderLowerAndCloser(){
		return kplRightShoulderLowerAndCloser;
	}

	/**
	 * Default constructor.
	 */
	public KinectUserPickedUpFromSidesLauncher(){

		DeviceManager dm = DeviceManager.getDeviceManager();
		kplLeftShoulderLowerAndCloser= dm.getKinectPoseLauncher( KinectPoseEnum.LEFT_SHOULDER_LOWER_AND_CLOSER);
		kplRightShoulderLowerAndCloser= dm.getKinectPoseLauncher( KinectPoseEnum.RIGHT_SHOULDER_LOWER_AND_CLOSER);
    
    

	}
	
	
	


	/**
	 * Adds a listener to the list of listeners of the superclass LauncherWrapper.
	 * @param l The listener that have to be added.
	 * @throws Exception 
	 */
	public void addListener (IKinectPoseService l) throws Exception{	
		
		kplLeftShoulderLowerAndCloser.addListener(l);
		kplRightShoulderLowerAndCloser.addListener(l);
 

    
	}


}
