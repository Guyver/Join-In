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
public class KinectUserHugLauncher extends LauncherWrapper {

	/**
	 * This field represents the the ID label of the users we want to get the poses of.
	 */
	private int userId;
	KinectPoseLauncher kplOpenedHug;
	KinectPoseLauncher kplClosedHug;
	
	
	public KinectPoseLauncher getKinectPoseLauncherOpenedHug(){
		return kplOpenedHug;
	}
	public KinectPoseLauncher getKinectPoseLauncherClosedHug(){
		return kplClosedHug;
	}
	
	public KinectUserHugLauncher(int userId){
		setUserId(userId);
		DeviceManager dm = DeviceManager.getDeviceManager();  
    	kplOpenedHug = dm.getKinectPoseLauncher(userId,  KinectPoseEnum.OPENED_HUG);
    	kplClosedHug = dm.getKinectPoseLauncher(userId, KinectPoseEnum.CLOSED_HUG);
    	
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
		
		kplOpenedHug.addListener(l);
    	kplClosedHug.addListener(l);
	}


}
