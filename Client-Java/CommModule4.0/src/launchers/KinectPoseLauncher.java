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
	
	private double lastRightLegY;
	
	private double lastLeftLegY;
	
	private boolean rightLegMovingUp;
	private boolean leftLegMovingUp;
	private boolean leftLegMovingDown;
	private boolean rightLegMovingDown;
	
	
	/**
	 * This constructor creates a new KinectPoseLauncher which will detect the pose kinectPose from the user whose ID label is userId.
	 * @param userId The ID label of the user that we want to get the pose from.
	 * @param kinectPose The pose we want to detect.
	 */
	public KinectPoseLauncher(int userId, KinectPoseEnum kinectPose){
		this.setKinectPose(kinectPose);
		this.setUserId(userId);
		lastTimePosingState=false;
		setLastRightLegY(0.0);
		setLastLeftLegY(0.0);
		rightLegMovingUp=false;
		leftLegMovingUp=false;
		leftLegMovingDown=false;
		rightLegMovingDown=false;
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
	 * @param kinectPose the kinectPlastRightLegYose to set
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
				if(
						(this.getKinectPose().name().compareTo(KinectPoseEnum.WALK_LEFT_LEG_UP.name())==0
						||
						this.getKinectPose().name().compareTo(KinectPoseEnum.WALK_RIGHT_LEG_UP.name())==0)
						&&lastTimePosingState==false){ 
					lastTimePosingState=true;
					
					KinectPoseServiceEvent se = new KinectPoseServiceEvent(ke.getUserId(),this.getKinectPose(),posing);		
					Iterator<IListenerCommModule> it = super.listenersList.iterator();
					while(it.hasNext()){
						IKinectPoseService l = (IKinectPoseService)it.next();
						l.kinectPoseUpdate(se);
					}		
				}else if (this.getKinectPose().name().compareTo(KinectPoseEnum.LEFT_HAND_BACK.name())==0||
						this.getKinectPose().name().compareTo(KinectPoseEnum.RIGHT_HAND_BACK.name())==0||
						this.getKinectPose().name().compareTo(KinectPoseEnum.BOTH_HANDS_BACK.name())==0||
						this.getKinectPose().name().compareTo(KinectPoseEnum.OPENED_HUG.name())==0||
						this.getKinectPose().name().compareTo(KinectPoseEnum.CLOSED_HUG.name())==0){
					KinectPoseServiceEvent se = new KinectPoseServiceEvent(ke.getUserId(),this.getKinectPose(),posing);		
					Iterator<IListenerCommModule> it = super.listenersList.iterator();
					while(it.hasNext()){
						IKinectPoseService l = (IKinectPoseService)it.next();
						l.kinectPoseUpdate(se);
					}		
				}
				else{
					
				}
			}else{//No estoy posando, así que voy a ver si tengo que enviar un el moveMentInProgress
				
				boolean movementInProgress= performMovementInProgressLogic(ke,this.getKinectPose());
				
				if (movementInProgress){
					KinectPoseServiceEvent se = new KinectPoseServiceEvent(ke.getUserId(),this.getKinectPose(),false);		
					Iterator<IListenerCommModule> it = super.listenersList.iterator();
					while(it.hasNext()){
						IKinectPoseService l = (IKinectPoseService)it.next();
						l.kinectPoseUpdate(se);
					}		
				}
				
				
				if(lastTimePosingState==true){
					lastTimePosingState=false;
					//System.out.println("Has dejado de posar");
				}else{
					//No decir nada porque no estoy posando
				}
			}
			setLastRightLegY(ke.getRightKnee().getY());
			setLastLeftLegY(ke.getLeftKnee().getY());
	}
	
	
	public boolean performMovementInProgressLogic(KinectSkeletonServiceEvent ksse, KinectPoseEnum poseToMatch){
		
		
		if(poseToMatch.name()=="WALK_LEFT_LEG_UP"){
			
			if(ksse.getRightKnee().getY()-lastRightLegY>1){
				rightLegMovingUp=true;
			}else{
				rightLegMovingUp=false;
			}
			if(lastLeftLegY-ksse.getLeftKnee().getY()>1){
				leftLegMovingDown=true;
			}else{
				leftLegMovingDown=false;
			}
			
			lastRightLegY=ksse.getRightKnee().getY();
			lastLeftLegY=ksse.getLeftKnee().getY();
			
			if(rightLegMovingUp||leftLegMovingDown){
				return true;
			}
			
		}else if(poseToMatch.name()=="WALK_RIGHT_LEG_UP"){
			if(ksse.getLeftKnee().getY()-lastLeftLegY>2){
				leftLegMovingUp=true;
			}else{
				leftLegMovingUp=false;
			}
			if(lastRightLegY-ksse.getRightKnee().getY()>1){
				rightLegMovingDown=true;
			}else{
				rightLegMovingDown=false;
			}
			
			lastRightLegY=ksse.getRightKnee().getY();
			lastLeftLegY=ksse.getLeftKnee().getY();
			
			if(leftLegMovingUp||rightLegMovingDown){
				return true;
			}
		}
		
		
		return false;
		
	
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
		}else if(poseToMatch.name()=="LEFT_HAND_BACK"){
			return isLeftHandBack(ksse);
		}else if(poseToMatch.name()=="RIGHT_HAND_BACK"){
			return isRightHandBack(ksse);
		}else if(poseToMatch.name()=="BOTH_HANDS_BACK"){
			return areBothHandsBack(ksse);
		}else if(poseToMatch.name()=="OPENED_HUG"){
			return isPoseOpenedHug(ksse);
		}else if(poseToMatch.name()=="CLOSED_HUG"){
			return isPoseClosedHug(ksse);
		}else{
			return false;
		}
		
		
	}
	
	


	private boolean areBothHandsBack(KinectSkeletonServiceEvent ksse) {
		boolean isTheRequestedPose = false;

		if (ksse.getLeftHand().getZ() - ksse.getLeftHip().getZ()>200 && ksse.getRightHand().getZ() - ksse.getRightHip().getZ()>200) {
			isTheRequestedPose = true;
		}
 
		return isTheRequestedPose;
	}
	private boolean isLeftHandBack(KinectSkeletonServiceEvent ksse) {
		boolean isTheRequestedPose = false;

		if (ksse.getLeftHand().getZ() - ksse.getLeftHip().getZ()>200 && !( ksse.getRightHand().getZ() - ksse.getRightHip().getZ()>200)) {
			isTheRequestedPose = true;
		}
 
		return isTheRequestedPose;
	}private boolean isRightHandBack(KinectSkeletonServiceEvent ksse) {
		boolean isTheRequestedPose = false;

		if (!(ksse.getLeftHand().getZ() - ksse.getLeftHip().getZ()>200) && ksse.getRightHand().getZ() - ksse.getRightHip().getZ()>200) {
			isTheRequestedPose = true;
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
		double length = 50;

		if (ksse.getLeftKnee().getY() > (ksse.getRightKnee().getY() + length)) {
			isTheRequestedPose = true;
			rightLegMovingUp=false; 
			leftLegMovingDown=false;
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
		double length = 50;

		if (ksse.getRightKnee().getY() > (ksse.getLeftKnee().getY() + length)) {
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
		
		if(ksse.getLeftHand().getX()>ksse.getNeck().getX()&&ksse.getRightHand().getX()<ksse.getNeck().getX()){
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

	/**
	 * @return the lastRightLegY
	 */
	public double getLastRightLegY() {
		return lastRightLegY;
	}

	/**
	 * @param lastRightLegY the lastRightLegY to set
	 */
	public void setLastRightLegY(double lastRightLegY) {
		this.lastRightLegY = lastRightLegY;
	}

	/**
	 * @return the lastLeftLegY
	 */
	public double getLastLeftLegY() {
		return lastLeftLegY;
	}

	/**
	 * @param lastLeftLegY the lastLeftLegY to set
	 */
	public void setLastLeftLegY(double lastLeftLegY) {
		this.lastLeftLegY = lastLeftLegY;
	}

	/**
	 * @return the rightLegMovingUp
	 */
	public boolean isRightLegMovingUp() {
		return rightLegMovingUp;
	}

	/**
	 * @param rightLegMovingUp the rightLegMovingUp to set
	 */
	public void setRightLegMovingUp(boolean rightLegMovingUp) {
		this.rightLegMovingUp = rightLegMovingUp;
	}

	/**
	 * @return the leftLegMovingUp
	 */
	public boolean isLeftLegMovingUp() {
		return leftLegMovingUp;
	}

	/**
	 * @param leftLegMovingUp the leftLegMovingUp to set
	 */
	public void setLeftLegMovingUp(boolean leftLegMovingUp) {
		this.leftLegMovingUp = leftLegMovingUp;
	}

	/**
	 * @return the rightLegMovingDown
	 */
	public boolean isRightLegMovingDown() {
		return rightLegMovingDown;
	}

	/**
	 * @param rightLegMovingDown the rightLegMovingDown to set
	 */
	public void setRightLegMovingDown(boolean rightLegMovingDown) {
		this.rightLegMovingDown = rightLegMovingDown;
	}

	/**
	 * @return the leftLegMovingDown
	 */
	public boolean isLeftLegMovingDown() {
		return leftLegMovingDown;
	}

	/**
	 * @param leftLegMovingDown the leftLegMovingDown to set
	 */
	public void setLeftLegMovingDown(boolean leftLegMovingDown) {
		this.leftLegMovingDown = leftLegMovingDown;
	}

}
