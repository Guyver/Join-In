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
	 * The pose that this launcher is going to be taking care of to report to its listeners
	 */
	private KinectPoseEnum kinectPose;

	/**
	 * This is the skeleton manager which sends the SkeletonJoint data to this same class to evaluate it as a pose.
	 */
	private KinectSkeletonLauncher privateKinectSkeletonLauncher;
	/**
	 * This is variable is used to "remember" the last state of the skeleton so we can compare the current state to the previous one 
	 * and check if the knees have suffered any change. Then we can know if the user is standing or not.
	 */
	private KinectSkeletonServiceEvent lastKsse;
	
	/**
	 * This constructor creates a new KinectPoseLauncher which will detect the pose kinectPose from the user whose ID label is userId.
	 * @param kinectPose The pose we want to detect.
	 */
	public KinectPoseLauncher(KinectPoseEnum kinectPose){
		this.setKinectPose(kinectPose);
		
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
	@Override
	public void dropService(){
		if(!deviceNotNecessaryAnyLonger){
			super.dropService();
			DeviceManager.getDeviceManager().dropKinect(this);	
		}
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
					//KinectPoseServiceEvent se = new KinectPoseServiceEvent(ke.getUserId(),this.getKinectPose(),posing);	
					KinectPoseServiceEvent se = new KinectPoseServiceEvent(this.getKinectPose(),posing);		
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
		}else if(poseToMatch.name().compareTo(KinectPoseEnum.OPENED_HUG.name())==0){
			return isPoseOpenedHug(ksse);
		}else if(poseToMatch.name().compareTo(KinectPoseEnum.CLOSED_HUG.name())==0){
			return isPoseClosedHug(ksse);
		}else if(poseToMatch.name().compareTo(KinectPoseEnum.STAND.name())==0){
			return isPoseStand(ksse);
		}else if(poseToMatch.name().compareTo(KinectPoseEnum.LEFT_SHOULDER_LOWER_AND_CLOSER.name())==0){
			return isLeftShoulderLowerAndCloser(ksse);
		}else if(poseToMatch.name().compareTo(KinectPoseEnum.RIGHT_SHOULDER_LOWER_AND_CLOSER.name())==0){
			return isRightShoulderLowerAndCloser(ksse);
		}else if(poseToMatch.name().compareTo(KinectPoseEnum.CROSSED_HANDS_ABOVE_SHOULDERS.name())==0){
			return areCrossedHandsAboveShoulders(ksse);
		}else if(poseToMatch.name().compareTo(KinectPoseEnum.LEFT_HAND_ABOVE_LEFT_SHOULDER.name())==0){
			return isLeftHandAboveLeftShoulder(ksse);
		}else if(poseToMatch.name().compareTo(KinectPoseEnum.RIGHT_HAND_ABOVE_RIGHT_SHOULDER.name())==0){
			return isRightHandAboveRightShoulder(ksse);
		}else if(poseToMatch.name().compareTo(KinectPoseEnum.LEFT_HAND_BENEATH_LEFT_ELBOW_SEPARATED_FROM_LEFT_HIP.name())==0){
			return isLeftHandBeneathLeftElbowSeparatedFromLeftHip(ksse);
		}else if(poseToMatch.name().compareTo(KinectPoseEnum.RIGHT_HAND_BENEATH_RIGHT_ELBOW_SEPARATED_FROM_RIGHT_HIP.name())==0){
			return isRightHandBeneathRightElbowSeparatedFromRightHip(ksse);
		}else if(poseToMatch.name().compareTo(KinectPoseEnum.HANDS_ABOVE_SHOULDERS_PSI_POSE.name())==0){
				return isHandsAboveShouldersPsiPose(ksse);
		}else if(poseToMatch.name().compareTo(KinectPoseEnum.RIGHT_HAND_IN_FRONT_OF_THE_USER.name())==0){
			return isRightHandInFrontOfTheUser50cm(ksse);
		}else if(poseToMatch.name().compareTo(KinectPoseEnum.LEFT_HAND_IN_FRONT_OF_THE_USER.name())==0){
			return isLeftHandInFrontOfTheUser50cm(ksse);
		}else{
			return false;
		}
		
	}
	/**
	 * Specific algorithm to detect whether the user is doing the psi-pose.
	 * 
	 * 
	 * @param ksse
	 *            The KinectSkeletonServiceEvent containing the Skeleton data of
	 *            the user.
	 * @return True if the user is doing the pose. False otherwise.
	 */
	private boolean isHandsAboveShouldersPsiPose(KinectSkeletonServiceEvent ksse) {
		boolean isTheRequestedPose=false;
		
		if(isLeftHandAboveLeftShoulder(ksse)&&isRightHandAboveRightShoulder(ksse)){
			isTheRequestedPose=true;
		}
		return isTheRequestedPose;
	}
	/**
	 * Specific algorithm to detect whether the user has their left hand higher than their left shoulder.
	 * 
	 * 
	 * @param ksse
	 *            The KinectSkeletonServiceEvent containing the Skeleton data of
	 *            the user.
	 * @return True if the user is doing the pose. False otherwise.
	 */
	private boolean isLeftHandAboveLeftShoulder(KinectSkeletonServiceEvent ksse){
		boolean isTheRequestedPose=false;
		
		if(ksse.getLeftHand().getY()>ksse.getLeftShoulder().getY() && ksse.getLeftHand().getX()<ksse.getLeftShoulder().getX()){
			isTheRequestedPose=true;
		}
				
		return isTheRequestedPose;
	}
	/**
	 * Specific algorithm to detect whether the user has their right hand higher than their right shoulder.
	 * 
	 * 
	 * @param ksse
	 *            The KinectSkeletonServiceEvent containing the Skeleton data of
	 *            the user.
	 * @return True if the user is doing the pose. False otherwise.
	 */
	private boolean isRightHandAboveRightShoulder(KinectSkeletonServiceEvent ksse){
		boolean isTheRequestedPose=false;
		
		if(ksse.getRightHand().getY()>ksse.getRightShoulder().getY()&& ksse.getRightHand().getX()>ksse.getRightShoulder().getX()){
			isTheRequestedPose=true;
		}
		
		return isTheRequestedPose;
	}
	/**
	 * Specific algorithm to detect whether the user has their left hand streched in front of them.
	 * 
	 * 
	 * @param ksse
	 *            The KinectSkeletonServiceEvent containing the Skeleton data of
	 *            the user.
	 * @return True if the user is doing the pose. False otherwise.
	 */
	private boolean isLeftHandInFrontOfTheUser50cm(KinectSkeletonServiceEvent ksse){
		
		boolean isTheRequestedPose=false;
		
		double correctedTorso= ksse.getTorso().getZ()-500.0;
		if(ksse.getLeftHand().getZ()<correctedTorso && ksse.getLeftHand().getZ()!=0 && ksse.getTorso().getZ()!=0){
			isTheRequestedPose=true;
		}
		
		return isTheRequestedPose;
	}
	/**
	 * Specific algorithm to detect whether the user has their right hand streched in front of them.
	 * 
	 * 
	 * @param ksse
	 *            The KinectSkeletonServiceEvent containing the Skeleton data of
	 *            the user.
	 * @return True if the user is doing the pose. False otherwise.
	 */
	private boolean isRightHandInFrontOfTheUser50cm(KinectSkeletonServiceEvent ksse){
		boolean isTheRequestedPose=false;
		
		double correctedTorso= ksse.getTorso().getZ()-500.0;
		if(ksse.getRightHand().getZ()<correctedTorso && ksse.getRightHand().getZ()!=0 && ksse.getTorso().getZ()!=0){
			isTheRequestedPose=true;
		}
		
		return isTheRequestedPose;
	}
	/**
	 * Specific algorithm to detect whether the user has their left hand separated 50cm or more from their left hip. 
	 * Their left hand must be lower than their left elbow.
	 *
	 * 
	 * 
	 * @param ksse
	 *            The KinectSkeletonServiceEvent containing the Skeleton data of
	 *            the user.
	 * @return True if the user is doing the pose. False otherwise.
	 */
	private boolean isLeftHandBeneathLeftElbowSeparatedFromLeftHip(KinectSkeletonServiceEvent ksse){
		boolean isTheRequestedPose=false;
		
		if(ksse.getLeftHand().getY()<ksse.getLeftElbow().getY() && ksse.getLeftHand().getX()<ksse.getLeftHip().getX()-500){
			isTheRequestedPose=true;
		}
		return isTheRequestedPose;
	}
	/**
	 * Specific algorithm to detect whether the user has their right hand separated 50cm or more from their right hip. 
	 * Their right hand must be lower than their right elbow.
	 *
	 * 
	 * 
	 * @param ksse
	 *            The KinectSkeletonServiceEvent containing the Skeleton data of
	 *            the user.
	 * @return True if the user is doing the pose. False otherwise.
	 */
	private boolean isRightHandBeneathRightElbowSeparatedFromRightHip(KinectSkeletonServiceEvent ksse){
		boolean isTheRequestedPose=false;
		if(ksse.getRightHand().getY()<ksse.getRightElbow().getY() && ksse.getRightHand().getX()>ksse.getRightHip().getX()+500){
			isTheRequestedPose=true;
		}
		return isTheRequestedPose;
	}
	
	
	/**
	 * Specific algorithm to detect whether the user is doing a X with their arms in front of their face.
	 * 
	 * 
	 * @param ksse
	 *            The KinectSkeletonServiceEvent containing the Skeleton data of
	 *            the user.
	 * @return True if the user is doing the pose. False otherwise.
	 */
	private boolean areCrossedHandsAboveShoulders(KinectSkeletonServiceEvent ksse){
		boolean isTheRequestedPose=false;
		if(ksse.getRightHand().getX()+100<ksse.getLeftHand().getX() && ksse.getRightHand().getY()>ksse.getRightShoulder().getY()&&ksse.getLeftHand().getY()>ksse.getLeftShoulder().getY()){
				isTheRequestedPose=true;
		}
			
			
		return isTheRequestedPose;
	}
	
	/**
	 * Specific algorithm to detect whether the user is standing.
	 * 
	 * 
	 * @param ksse
	 *            The KinectSkeletonServiceEvent containing the Skeleton data of
	 *            the user.
	 * @return True if the user is doing the pose. False otherwise.
	 */
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
	
	/**
	 * Specific algorithm to detect whether the user has their left shoulder lower and closer to the camera (at least 10cm) than their right shoulder .
	 * 
	 * 
	 * @param ksse
	 *            The KinectSkeletonServiceEvent containing the Skeleton data of
	 *            the user.
	 * @return True if the user is doing the pose. False otherwise.
	 */
	private boolean isLeftShoulderLowerAndCloser(KinectSkeletonServiceEvent ksse){
		boolean isTheRequestedPose= false;
		
		if(ksse.getLeftShoulder().getZ()<ksse.getRightShoulder().getZ()-100 && ksse.getLeftShoulder().getY()<ksse.getRightShoulder().getY()&&  ksse.getRightHand().getX()>ksse.getRightHip().getX()+300&&  ksse.getLeftHand().getX()>ksse.getRightHip().getX()+300){
			
			isTheRequestedPose=true;
		}
		return isTheRequestedPose;
		
	}
	/**
	 * Specific algorithm to detect whether the user has their right shoulder lower and closer to the camera (at least 10cm) than their left shoulder.
	 * 
	 * 
	 * @param ksse
	 *            The KinectSkeletonServiceEvent containing the Skeleton data of
	 *            the user.
	 * @return True if the user is doing the pose. False otherwise.
	 */
	private boolean isRightShoulderLowerAndCloser(KinectSkeletonServiceEvent ksse){
		boolean isTheRequestedPose= false;
	
		if(ksse.getRightShoulder().getZ()<ksse.getLeftShoulder().getZ()-100 && ksse.getRightShoulder().getY()<ksse.getLeftShoulder().getY()&&   ksse.getLeftHand().getX()<ksse.getLeftHip().getX()-300 &&  ksse.getRightHand().getX()<ksse.getLeftHip().getX()-300){
			
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
		
		if(ksse.getLeftHand().getX()+100>ksse.getRightHip().getX()&&ksse.getRightHand().getX()-100<ksse.getLeftHip().getX()&&ksse.getRightHand().getY()<ksse.getLeftShoulder().getY()&&ksse.getLeftHand().getY()<ksse.getRightShoulder().getY()){
			isTheRequestedPose=true;
		}
		
		return isTheRequestedPose;
	}
	/**
	 * @return the privateKinectSkeletonLauncher
	 */
	public KinectSkeletonLauncher getPrivateKinectSkeletonLauncher() {
		return privateKinectSkeletonLauncher;
	}
	/**
	 * @param privateKinectSkeletonLauncher
	 *            the privateKinectSkeletonLauncher to set
	 */
	public void setPrivateKinectSkeletonLauncher(
			KinectSkeletonLauncher privateKinectSkeletonLauncher) {
		this.privateKinectSkeletonLauncher = privateKinectSkeletonLauncher;
	}

	


}
