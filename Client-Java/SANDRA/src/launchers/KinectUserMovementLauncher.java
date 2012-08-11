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
public class KinectUserMovementLauncher extends LauncherWrapper {

	/**
	 * The kinect pose launcher for the left leg risen pose
	 */
	KinectPoseLauncher kplWalkLeftLegUp;
	/**
	 * The kinect pose launcher for the right leg risen pose
	 */
	KinectPoseLauncher kplWalkRightLegUp;
	/**
	 * The kinect pose launcher for the standing still pose
	 */
	KinectPoseLauncher kplStand;
	

	/**
	 * 
	 * @return the launcher of events of the pose of having your left leg risen
	 */
	public KinectPoseLauncher getKinectPoseLauncherWalkLeftLegUp(){
		return kplWalkLeftLegUp;
	}
	/**
	 * 
	 * @return the launcher of events of the pose of having your right leg risen
	 */
	public KinectPoseLauncher getKinectPoseLauncherWalkRightLegUp(){
		return kplWalkRightLegUp;
	}
	/**
	 * 
	 * @return the launcher of events of the pose 'standing still'
	 */
	public KinectPoseLauncher getKinectPoseLauncherStand(){
		return kplStand;
	}
	/**
	 * Default constructor.
	 */
	public KinectUserMovementLauncher(){
	
		DeviceManager dm = DeviceManager.getDeviceManager();
		kplWalkLeftLegUp= dm.getKinectPoseLauncher( KinectPoseEnum.WALK_LEFT_LEG_UP);
    	kplWalkRightLegUp= dm.getKinectPoseLauncher( KinectPoseEnum.WALK_RIGHT_LEG_UP);
    	kplStand= dm.getKinectPoseLauncher( KinectPoseEnum.STAND);
    

	}
	
	




	/**
	 * Adds a listener to the list of listeners of the superclass
	 * LauncherWrapper.
	 * 
	 * @param l
	 *            The listener that have to be added.
	 * @throws Exception
	 */
	public void addListener (IKinectPoseService l) throws Exception{	
		
		kplWalkLeftLegUp.addListener(l);
    	kplWalkRightLegUp.addListener(l);
    	kplStand.addListener(l);

    
	}


}
