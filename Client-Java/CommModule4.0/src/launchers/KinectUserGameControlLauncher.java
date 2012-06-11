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
public class KinectUserGameControlLauncher extends LauncherWrapper {
	



	KinectPoseLauncher kplTouchingOppositeShoulder;
	KinectPoseLauncher kplCrossedHandsAboveShoulders;
	KinectPoseLauncher kplRightHandBeneathRightElbowSeparated30CmFromRightHip;
	KinectPoseLauncher kplLeftHandBeneathLeftElbowSeparated30CmFromLeftHip;
	KinectPoseLauncher kplRightHandAboveRightShoulder;
	KinectPoseLauncher kplLeftHandAboveLeftShoulder;
	

	public KinectPoseLauncher getKinectPoseLauncherTouchingOppositeShoulder(){
		return kplTouchingOppositeShoulder;
	}
	public KinectPoseLauncher getKinectPoseLauncherCrossedHands(){
		return kplCrossedHandsAboveShoulders;
	}
	public KinectPoseLauncher getKinectPoseLauncherRightHandBeneathRightElbowSeparated30CmFromRightHip(){
		return kplRightHandBeneathRightElbowSeparated30CmFromRightHip;
	} 
	public KinectPoseLauncher getKinectPoseLauncherLeftHandBeneathLeftElbowSeparated30CmFromLeftHip(){
		return kplLeftHandBeneathLeftElbowSeparated30CmFromLeftHip;
	} 
	public KinectPoseLauncher getKinectPoseLauncherRightHandAboveRightShoulder(){
		return kplRightHandAboveRightShoulder;
	} 
	public KinectPoseLauncher getKinectPoseLauncherLeftHandAboveLeftShoulder(){
		return kplLeftHandAboveLeftShoulder;
	} 
	
	public KinectUserGameControlLauncher(int userId){
		
		DeviceManager dm = DeviceManager.getDeviceManager();  
		kplTouchingOppositeShoulder = dm.getKinectPoseLauncher(userId, KinectPoseEnum.TOUCHING_OPPOSITE_SHOULDER);
		kplCrossedHandsAboveShoulders = dm.getKinectPoseLauncher(userId, KinectPoseEnum.CROSSED_HANDS_ABOVE_SHOULDERS);
		kplRightHandBeneathRightElbowSeparated30CmFromRightHip = dm.getKinectPoseLauncher(userId, KinectPoseEnum.RIGHT_HAND_BENEATH_RIGHT_ELBOW_SEPATED_50_CM_FROM_RIGHT_HIP);
		kplLeftHandBeneathLeftElbowSeparated30CmFromLeftHip = dm.getKinectPoseLauncher(userId, KinectPoseEnum.LEFT_HAND_BENEATH_LEFT_ELBOW_SEPARATED_50_CM_FROM_LEFT_HIP);
		kplRightHandAboveRightShoulder = dm.getKinectPoseLauncher(userId, KinectPoseEnum.RIGHT_HAND_ABOVE_RIGHT_SHOULDER);
		kplLeftHandAboveLeftShoulder = dm.getKinectPoseLauncher(userId, KinectPoseEnum.LEFT_HAND_ABOVE_LEFT_SHOULDER);
	}
	
	
	



	public void addListener (IKinectPoseService l) throws Exception{	

		kplTouchingOppositeShoulder.addListener(l);
		kplCrossedHandsAboveShoulders.addListener(l);
		kplRightHandBeneathRightElbowSeparated30CmFromRightHip.addListener(l);
		kplLeftHandBeneathLeftElbowSeparated30CmFromLeftHip.addListener(l);
		kplRightHandAboveRightShoulder.addListener(l);
		kplLeftHandAboveLeftShoulder.addListener(l);
	}


}
