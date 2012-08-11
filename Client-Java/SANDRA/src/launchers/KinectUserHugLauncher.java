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
public class KinectUserHugLauncher extends LauncherWrapper {


	/**
	 * This field represents the launcher of events of the pose "opened hug".
	 */
	KinectPoseLauncher kplOpenedHug;
	/**
	 * This field represents the launcher of events of the pose "closed hug".
	 */
	KinectPoseLauncher kplClosedHug;
	
	/**
	 * 
	 * @return the launcher of events of the pose "opened hug"
	 */
	public KinectPoseLauncher getKinectPoseLauncherOpenedHug(){
		return kplOpenedHug;
	}
	/**
	 * 
	 * @return the launcher of events of the pose "closed hug"
	 */
	public KinectPoseLauncher getKinectPoseLauncherClosedHug(){
		return kplClosedHug;
	}
	
	/**
	 * Default constructor.
	 */
	public KinectUserHugLauncher(){

		DeviceManager dm = DeviceManager.getDeviceManager();  
    	kplOpenedHug = dm.getKinectPoseLauncher(  KinectPoseEnum.OPENED_HUG);
    	kplClosedHug = dm.getKinectPoseLauncher( KinectPoseEnum.CLOSED_HUG);
    	
	}
	
	/**
	 * Calls to the superclass LauncherWrapper function dropService and drops the Kinect from the DeviceMnager.
	 */
	@Override
	public void dropService(){
		if(!deviceNotNecessaryAnyLonger){
			super.dropService();
			DeviceManager.getDeviceManager().dropKinect(this);
		}
	}

	/**
	 * Adds a listener to the list of listeners of the superclass LauncherWrapper.
	 * @param l The listener that have to be added.
	 * @throws Exception 
	 */
	public void addListener (IKinectPoseService l) throws Exception{	
		
		kplOpenedHug.addListener(l);
    	kplClosedHug.addListener(l);
	}


}
