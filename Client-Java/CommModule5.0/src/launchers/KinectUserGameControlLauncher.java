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
public class KinectUserGameControlLauncher extends LauncherWrapper {
	/**
	 * The launcher of the crossed hands above the user's shoulders pose
	 */
	KinectPoseLauncher kplCrossedHandsAboveShoulders;
	/**
	 * The launcher of the right hand beneath the right elbow and the right hand separated form the right hip 30cm  pose
	 */
	KinectPoseLauncher kplRightHandBeneathRightElbowSeparated30CmFromRightHip;
	/**
	 * The launcher of the left hand beneath the left elbow and the left hand separated form the left hip 30cm  pose
	 */
	KinectPoseLauncher kplLeftHandBeneathLeftElbowSeparated30CmFromLeftHip;
	/**
	 * The launcher of the right hand above the user's right shoulder pose
	 */
	KinectPoseLauncher kplRightHandAboveRightShoulder;
	/**
	 * The launcher of the left hand above the user's left shoulder pose
	 */
	KinectPoseLauncher kplLeftHandAboveLeftShoulder;
	/**
	 * The launcher of the psi-pose
	 */
	KinectPoseLauncher kplHandsAboveShouldersPsiPose;

	/**
	 * 
	 * @return the launcher of the crossed hands above the user's shoulders pose
	 */
	public KinectPoseLauncher getKinectPoseLauncherCrossedHands(){
		return kplCrossedHandsAboveShoulders;
	}
	/**
	 * 
	 * @return the launcher of the right hand beneath the right elbow and the right hand separated form the right hip 30cm  pose
	 */
	public KinectPoseLauncher getKinectPoseLauncherRightHandBeneathRightElbowSeparated30CmFromRightHip(){
		return kplRightHandBeneathRightElbowSeparated30CmFromRightHip;
	} 
	/**
	 * 
	 * @return the launcher of the left hand beneath the left elbow and the left hand separated form the left hip 30cm  pose
	 */
	public KinectPoseLauncher getKinectPoseLauncherLeftHandBeneathLeftElbowSeparated30CmFromLeftHip(){
		return kplLeftHandBeneathLeftElbowSeparated30CmFromLeftHip;
	} 
	/**
	 * 
	 * @return the launcher of the right hand above the user's right shoulder pose
	 */
	public KinectPoseLauncher getKinectPoseLauncherRightHandAboveRightShoulder(){
		return kplRightHandAboveRightShoulder;
	} 
	/**
	 * 
	 * @return the launcher of the left hand above the user's left shoulder pose
	 */
	public KinectPoseLauncher getKinectPoseLauncherLeftHandAboveLeftShoulder(){
		return kplLeftHandAboveLeftShoulder;
	} 
	/**
	 * 
	 * @return the launcher of the psi-pose
	 */
	public KinectPoseLauncher getKinectPoseLauncherHandsAboveShouldersPsiPose(){
		return kplHandsAboveShouldersPsiPose;
	} 
	
	/**
	 * Constructor with parameter.
	 * @param userId The user's label we want to track his/her skeleton to.
	 */
	public KinectUserGameControlLauncher(int userId){
		
		DeviceManager dm = DeviceManager.getDeviceManager();  

		kplCrossedHandsAboveShoulders = dm.getKinectPoseLauncher(userId, KinectPoseEnum.CROSSED_HANDS_ABOVE_SHOULDERS);
		kplRightHandBeneathRightElbowSeparated30CmFromRightHip = dm.getKinectPoseLauncher(userId, KinectPoseEnum.RIGHT_HAND_BENEATH_RIGHT_ELBOW_SEPARATED_FROM_RIGHT_HIP);
		kplLeftHandBeneathLeftElbowSeparated30CmFromLeftHip = dm.getKinectPoseLauncher(userId, KinectPoseEnum.LEFT_HAND_BENEATH_LEFT_ELBOW_SEPARATED_FROM_LEFT_HIP);
		kplRightHandAboveRightShoulder = dm.getKinectPoseLauncher(userId, KinectPoseEnum.RIGHT_HAND_ABOVE_RIGHT_SHOULDER);
		kplLeftHandAboveLeftShoulder = dm.getKinectPoseLauncher(userId, KinectPoseEnum.LEFT_HAND_ABOVE_LEFT_SHOULDER);
		kplHandsAboveShouldersPsiPose= dm.getKinectPoseLauncher(userId, KinectPoseEnum.HANDS_ABOVE_SHOULDERS_PSI_POSE);
		
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
	 * Adds a listener to all the listeners of the different pose launchers related to the control of game menus.
	 * @param l The listener that have to be added.
	 * @throws Exception 
	 */
	public void addListener (IKinectPoseService l) throws Exception{	

		
		kplCrossedHandsAboveShoulders.addListener(l);
		kplRightHandBeneathRightElbowSeparated30CmFromRightHip.addListener(l);
		kplLeftHandBeneathLeftElbowSeparated30CmFromLeftHip.addListener(l);
		kplRightHandAboveRightShoulder.addListener(l);
		kplLeftHandAboveLeftShoulder.addListener(l);
		kplHandsAboveShouldersPsiPose.addListener(l);
	}


}
