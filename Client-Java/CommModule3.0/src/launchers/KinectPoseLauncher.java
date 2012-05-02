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


import java.util.Iterator;

import kinectThreads.IKinectPoseListener;
import kinectThreads.KinectPoseEnum;

import iservices.IKinectPoseService;

import services.KinectPoseServiceEvent;
import services.KinectSkeletonServiceEvent;

import control.DeviceManager;
import control.IListenerCommModule;



import control.LauncherWrapper;
/**
 * A Kinect event thrower facade. 
 * @author Santiago Hors Fraile
 */
public class KinectPoseLauncher extends LauncherWrapper implements IKinectPoseListener{
	
	/**
	 * This field represents the the ID label of the users we want to get the poses of.
	 */
	private int userId;
	/**
	 * The pose that this launcher is going to be taking care of to report to its listeners
	 */
	private KinectPoseEnum kinectPose;
	/**
	 * This boolean variable represents whether the user was posing or not before the new update.
	 */
	private boolean lastTimePosingState; 
	/**
	 * This is the skeleton manager which sends the SkeletonJoint data to this same class to evaluate it as a pose.
	 */
	private KinectSkeletonLauncher privateKinectSkeletonLauncher;
	
	/**
	 * This constructor creates a new KinectPoseLauncher which will detect the pose kinectPose from the user whose ID label is userId.
	 * @param userId The ID label of the user that we want to get the pose from.
	 * @param kinectPose The pose we want to detect.
	 */
	public KinectPoseLauncher(int userId, KinectPoseEnum kinectPose){
		this.setKinectPose(kinectPose);
		this.setUserId(userId);
		lastTimePosingState=false;
	
	}

	/**
	 * 
	 * @param l Adds a listener to the list of listeners of the superclass LauncherWrapper.
	 * @throws Exception
	 */
	public void addListener (IKinectPoseService l) throws Exception{	
	
			super.addListener(l);
			
	}
	/**
	 * Calls to the superclass LauncherWrapper function dropService and drops the Kinect from the DeviceMnager.
	 */
	public void dropService(){
		if(!deviceNotNecessaryAnyLonger){
			super.dropService();
			DeviceManager.getDeviceManager().dropKinect(this);	
		}
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
 * This function fires a new pose event whenever the user performs the pose assigned to this launcher. It only fires one event per pose change.
 */
	@Override
	public void kinectUpdate(KinectSkeletonServiceEvent ke) {
		
			boolean posing;
			
			posing= performPoseLogic(ke,this.getKinectPose());
			if(posing){
				if(lastTimePosingState==false){ 
					lastTimePosingState=true;
					KinectPoseServiceEvent se = new KinectPoseServiceEvent(ke.getUserId(),this.getKinectPose(),posing);		
					Iterator<IListenerCommModule> it = super.listenersList.iterator();
					while(it.hasNext()){
						IKinectPoseService l = (IKinectPoseService)it.next();
						l.kinectPoseUpdate(se);
					}		
				}else{
					//System.out.println("Y sigo posando...");
				}
			}else{
				if(lastTimePosingState==true){
					lastTimePosingState=false;
					//System.out.println("Has dejado de posar");
				}else{
					//No decir nada porque no estoy posando
				}
			}
	}
	/**
	 * This function selects the algorithm to apply to the KinectSkeletonServiceEvent depending on the chosen pose.
	 * @param ksse The KinectSkeletonServiceEvent containing the Skeleton data of the user.
	 * @param poseToMatch The pose we want to know if the user is doing.
	 * @return True if the user is doing the pose. False otherwise.
	 */
	private boolean performPoseLogic(KinectSkeletonServiceEvent ksse, KinectPoseEnum poseToMatch){

		
		if(poseToMatch.name()=="WALK_LEFT_LEG_UP"){
			return isPoseWalkingLeftLegUp(ksse);
		}else if(poseToMatch.name()=="WALK_RIGHT_LEG_UP"){
			return isPoseWalkingRightLegUp(ksse);
		}else{
			return false;
		}
		
		
	}
	
	/**
	 * Specific algorithm to detect whether the user has the right leg up (similar to a walk step).
	 * @param ksse The KinectSkeletonServiceEvent containing the Skeleton data of the user.
	 * @return True if the user is doing the pose. False otherwise.
	 */
private boolean isPoseWalkingRightLegUp(KinectSkeletonServiceEvent ksse) {
		
		boolean isTheRequestedPose=false;
		double length= 40;
		
		if(ksse.getRightKnee().getY()>(ksse.getLeftKnee().getY()+length)){
			isTheRequestedPose=true;
		}

		return isTheRequestedPose;
	}

/**
 * Specific algorithm to detect whether the user has the left leg up (similar to a walk step).
 * @param ksse The KinectSkeletonServiceEvent containing the Skeleton data of the user.
 * @return True if the user is doing the pose. False otherwise.
 */
private boolean isPoseWalkingLeftLegUp(KinectSkeletonServiceEvent ksse) {
		boolean isTheRequestedPose=false;
		double length= 40;
		
		if(ksse.getLeftKnee().getY()>(ksse.getRightKnee().getY()+length)){
			isTheRequestedPose=true;
		}
		return isTheRequestedPose;
	}

/**
 * @return the kinectSkeletonLauncher
 */
public KinectSkeletonLauncher getPrivateKinectSkeletonLauncher() {
	return privateKinectSkeletonLauncher;
}

/**
 * @param kinectSkeletonLauncher the kinectSkeletonLauncher to set
 */
public void setPrivateKinectSkeletonLauncher(KinectSkeletonLauncher privateKinectSkeletonLauncher) {
	this.privateKinectSkeletonLauncher = privateKinectSkeletonLauncher;
}


}
