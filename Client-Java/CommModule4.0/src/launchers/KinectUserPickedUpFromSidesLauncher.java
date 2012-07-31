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
 * 
 * @author Santiago Hors Fraile
 */
public class KinectUserPickedUpFromSidesLauncher extends LauncherWrapper {
	/**
	 * This field represents the the ID label of the users we want to get the poses of.
	 */
	private int userId;
	KinectPoseLauncher kplLeftShoulderLowerAndCloser;
	KinectPoseLauncher kplRightShoulderLowerAndCloser;

	
	public KinectPoseLauncher getKinectPoseLauncherLeftShoulderLowerAndCloser(){
		return kplLeftShoulderLowerAndCloser;
	}
	public KinectPoseLauncher getKinectPoseLauncherRightShoulderLowerAndCloser(){
		return kplRightShoulderLowerAndCloser;
	}

	
	public KinectUserPickedUpFromSidesLauncher(int userId){
		setUserId(userId);
		DeviceManager dm = DeviceManager.getDeviceManager();
		kplLeftShoulderLowerAndCloser= dm.getKinectPoseLauncher(userId, KinectPoseEnum.LEFT_SHOULDER_LOWER_AND_CLOSER);
		kplRightShoulderLowerAndCloser= dm.getKinectPoseLauncher(userId, KinectPoseEnum.RIGHT_SHOULDER_LOWER_AND_CLOSER);
    
    

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




	public void addListener (IKinectPoseService l) throws Exception{	
		
		kplLeftShoulderLowerAndCloser.addListener(l);
		kplRightShoulderLowerAndCloser.addListener(l);
 

    
	}


}
