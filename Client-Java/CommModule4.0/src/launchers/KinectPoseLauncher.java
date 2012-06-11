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
	 * This is the skeleton manager which sends the SkeletonJoint data to this same class to evaluate it as a pose.
	 */
	private KinectSkeletonLauncher privateKinectSkeletonLauncher;

	

	
	private KinectSkeletonServiceEvent lastKsse;
	
	/**
	 * This constructor creates a new KinectPoseLauncher which will detect the pose kinectPose from the user whose ID label is userId.
	 * @param userId The ID label of the user that we want to get the pose from.
	 * @param kinectPose The pose we want to detect.
	 */
	public KinectPoseLauncher(int userId, KinectPoseEnum kinectPose){
		this.setKinectPose(kinectPose);
		this.setUserId(userId);
		lastKsse= null;
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
	 * Calls to the superclass LauncherWrapper function dropService and drops the Kinect from the DeviceManager.
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
	 * @param userId the userId to setKinectPoseServiceEvent
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
 * This function fires a new pose event whenever the user performs the pose assigned to this launcher. 
 */
	@Override
	public void kinectUpdate(KinectSkeletonServiceEvent ke) {
		
			boolean posing;
			posing= performPoseLogic(ke,this.getKinectPose());
			if(posing){
					KinectPoseServiceEvent se = new KinectPoseServiceEvent(ke.getUserId(),this.getKinectPose(),posing);		
					Iterator<IListenerCommModule> it = super.listenersList.iterator();
					while(it.hasNext()){
						IKinectPoseService l = (IKinectPoseService)it.next();
						l.kinectPoseUpdate(se);
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

		
		
		if(poseToMatch.name().compareTo(KinectPoseEnum.WALK_LEFT_LEG_UP.name())==0){
			return isPoseWalkingLeftLegUp(ksse);
		}else if(poseToMatch.name().compareTo(KinectPoseEnum.WALK_RIGHT_LEG_UP.name())==0){
			return isPoseWalkingRightLegUp(ksse);
		}else if(poseToMatch.name().compareTo(KinectPoseEnum.TOUCHING_OPPOSITE_SHOULDER.name())==0){
			return isTouchingOppositeShoulder(ksse);
		}else if(poseToMatch.name().compareTo(KinectPoseEnum.OPENED_HUG.name())==0){
			return isPoseOpenedHug(ksse);
		}else if(poseToMatch.name().compareTo(KinectPoseEnum.CLOSED_HUG.name())==0){
			return isPoseClosedHug(ksse);
		}else if(poseToMatch.name().compareTo(KinectPoseEnum.STAND.name())==0){
			return isPoseStand(ksse);
		}else if(poseToMatch.name().compareTo(KinectPoseEnum.LEFT_HAND_BACK.name())==0){
			return isLeftHandBack(ksse);
		}else if(poseToMatch.name().compareTo(KinectPoseEnum.RIGHT_HAND_BACK.name())==0){
			return isRightHandBack(ksse);
		}else if(poseToMatch.name().compareTo(KinectPoseEnum.CROSSED_HANDS_ABOVE_SHOULDERS.name())==0){
			return areCrossedHandsAboveShoulders(ksse);
		}else if(poseToMatch.name().compareTo(KinectPoseEnum.LEFT_HAND_ABOVE_LEFT_SHOULDER.name())==0){
			return isLeftHandAboveLeftShoulder(ksse);
		}else if(poseToMatch.name().compareTo(KinectPoseEnum.RIGHT_HAND_ABOVE_RIGHT_SHOULDER.name())==0){
			return isRightHandAboveRightShoulder(ksse);
		}else if(poseToMatch.name().compareTo(KinectPoseEnum.LEFT_HAND_BENEATH_LEFT_ELBOW_SEPARATED_50_CM_FROM_LEFT_HIP.name())==0){
			return isLeftHandBeneathLeftElbowSeparated30CmFromLeftHip(ksse);
		}else if(poseToMatch.name().compareTo(KinectPoseEnum.RIGHT_HAND_BENEATH_RIGHT_ELBOW_SEPATED_50_CM_FROM_RIGHT_HIP.name())==0){
			return isRightHandBeneathRightElbowSeparated30CmFromRightHip(ksse);
		}else{
			return false;
		}
		
	}
	
	private boolean isLeftHandAboveLeftShoulder(KinectSkeletonServiceEvent ksse){
		boolean isTheRequestedPose=false;
		
		if(ksse.getLeftHand().getY()>ksse.getLeftShoulder().getY() && ksse.getLeftHand().getX()<ksse.getLeftShoulder().getX()){
			isTheRequestedPose=true;
		}
				
		return isTheRequestedPose;
	}
	
	private boolean isRightHandAboveRightShoulder(KinectSkeletonServiceEvent ksse){
		boolean isTheRequestedPose=false;
		
		if(ksse.getRightHand().getY()>ksse.getRightShoulder().getY()&& ksse.getRightHand().getX()>ksse.getRightShoulder().getX()){
			isTheRequestedPose=true;
		}
		
		return isTheRequestedPose;
	}
	
	private boolean isLeftHandBeneathLeftElbowSeparated30CmFromLeftHip(KinectSkeletonServiceEvent ksse){
		boolean isTheRequestedPose=false;
		
		if(ksse.getLeftHand().getY()<ksse.getLeftElbow().getY() && ksse.getLeftHand().getX()<ksse.getLeftHip().getX()-500){
			isTheRequestedPose=true;
		}
		return isTheRequestedPose;
	}
	
	private boolean isRightHandBeneathRightElbowSeparated30CmFromRightHip(KinectSkeletonServiceEvent ksse){
		boolean isTheRequestedPose=false;
		if(ksse.getRightHand().getY()<ksse.getRightElbow().getY() && ksse.getRightHand().getX()>ksse.getRightHip().getX()+500){
			isTheRequestedPose=true;
		}
		return isTheRequestedPose;
	}
	
	private boolean isTouchingOppositeShoulder(KinectSkeletonServiceEvent ksse) {
		boolean isTheRequestedPose = false;
	
		
		if(ksse.getLeftHand().getX()>ksse.getHead().getX() && ksse.getRightShoulder().getZ()-100<ksse.getLeftHand().getZ() && ksse.getRightShoulder().getY()<ksse.getLeftHand().getY()||
				ksse.getRightHand().getX()<ksse.getHead().getX() && ksse.getLeftShoulder().getZ()-100<ksse.getRightHand().getZ() && ksse.getLeftShoulder().getY()<ksse.getRightHand().getY()	){
			isTheRequestedPose=true;
		}

		return isTheRequestedPose;
	}
	private boolean areCrossedHandsAboveShoulders(KinectSkeletonServiceEvent ksse){
		boolean isTheRequestedPose=false;
		
		//if(ksse.getRightHand().getX()<ksse.getHead().getX() && ksse.getLeftHand().getX()>ksse.getHead().getX() && ksse.getLeftHand().getY()>ksse.getNeck().getY()&&ksse.getRightHand().getY()>ksse.getNeck().getY()){
		if(ksse.getRightHand().getX()+100<ksse.getLeftHand().getX() && ksse.getRightHand().getY()>ksse.getRightShoulder().getY()&&ksse.getLeftHand().getY()>ksse.getLeftShoulder().getY()){
				isTheRequestedPose=true;
		}
			
			
		return isTheRequestedPose;
	}
	
	
	private boolean isPoseStand(KinectSkeletonServiceEvent ksse){
		
		boolean isTheRequestedPose= false;
		
		if(lastKsse!=null){

			
			//If the difference in depth between the two knees is less than 20 cm...
			if(Math.abs(ksse.getLeftKnee().getZ()-ksse.getRightKnee().getZ())<20 ){
				//acceleration condition
				double leftKneeAcceleration= lastKsse.getLeftKnee().getZ()-ksse.getLeftKnee().getZ();
				double rightKneeAcceleration= lastKsse.getRightKnee().getZ()-ksse.getRightKnee().getZ();
				if(rightKneeAcceleration!=0&&leftKneeAcceleration!=0){
					if(Math.abs(leftKneeAcceleration)<2 && Math.abs(rightKneeAcceleration)<2){
						isTheRequestedPose=true;
					}
				}
			}
		}
		lastKsse=ksse;
		return isTheRequestedPose;
	}
	

	private boolean isLeftHandBack(KinectSkeletonServiceEvent ksse){
		boolean isTheRequestedPose= false;
		
		if(ksse.getLeftHand().getZ()>ksse.getTorso().getZ()+150){
			
			isTheRequestedPose=true;
		}
		return isTheRequestedPose;
		
	}
	
	private boolean isRightHandBack(KinectSkeletonServiceEvent ksse){
		boolean isTheRequestedPose= false;
	
		if(ksse.getRightHand().getZ()>ksse.getTorso().getZ()+150){
			
			isTheRequestedPose=true;
		}
		return isTheRequestedPose;
		
	}
	
		
	
	/**
	 * Specific algorithm to detect whether the user has his or her left leg up
	 * (similar to a walk step).
	 * 
	 * @param ksse
	 *            The KinectSkeletonServiceEvent containing the Skeleton data of
	 *            the user.
	 * @return True if the user is doing the pose. False otherwise.
	 */
	private boolean isPoseWalkingLeftLegUp(KinectSkeletonServiceEvent ksse) {
		boolean isTheRequestedPose = false;
		double length = 75;

		if ( (ksse.getRightKnee().getZ() - length) >  ksse.getLeftKnee().getZ() ) {
			isTheRequestedPose = true;
		
		}
	
		return isTheRequestedPose;
	}

	/**
	 * Specific algorithm to detect whether the user has his or her right leg up
	 * (similar to a walk step).
	 * 
	 * @param ksse
	 *            The KinectSkeletonServiceEvent containing the Skeleton data of
	 *            the user.
	 * @return True if the user is doing the pose. False otherwise.
	 */
	private boolean isPoseWalkingRightLegUp(KinectSkeletonServiceEvent ksse) {

		boolean isTheRequestedPose = false;
		double length = 75;

		if ( (ksse.getLeftKnee().getZ() - length) >  ksse.getRightKnee().getZ() ) {
			isTheRequestedPose = true;
		}
		
		return isTheRequestedPose;
	}
	
	
	/**
	 * Specific algorithm to detect whether the user has his or her arms opened
	 * (like the first thing you do before hugging).
	 * 
	 * @param ksse
	 *            The KinectSkeletonServiceEvent containing the Skeleton data of
	 *            the user.
	 * @return True if the user is doing the pose. False otherwise.
	 */
	private boolean isPoseOpenedHug(KinectSkeletonServiceEvent ksse){
		boolean isTheRequestedPose = false;
		
		
		if(ksse.getLeftHand().getX()<ksse.getLeftElbow().getX()&&ksse.getRightHand().getX()>ksse.getRightElbow().getX()){
			isTheRequestedPose=true;
		}
	
		return isTheRequestedPose;
	}
	
	/**
	 * Specific algorithm to detect whether the user has his or her arms closed
	 * (like when you are hugging).
	 * 
	 * @param ksse
	 *            The KinectSkeletonServiceEvent containing the Skeleton data of
	 *            the user.
	 * @return True if the user is doing the pose. False otherwise.
	 */
	private boolean isPoseClosedHug(KinectSkeletonServiceEvent ksse){
		boolean isTheRequestedPose = false;
		
		if(ksse.getLeftHand().getX()>ksse.getRightHip().getX()&&ksse.getRightHand().getX()<ksse.getLeftHip().getX()&&ksse.getRightHand().getY()<ksse.getLeftShoulder().getY()&&ksse.getLeftHand().getY()<ksse.getRightShoulder().getY()){
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
	 * @param kinectSkeletonLauncher
	 *            the kinectSkeletonLauncher to set
	 */
	public void setPrivateKinectSkeletonLauncher(
			KinectSkeletonLauncher privateKinectSkeletonLauncher) {
		this.privateKinectSkeletonLauncher = privateKinectSkeletonLauncher;
	}

	


}
