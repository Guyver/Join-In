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
package handlers;

import kinectThreads.KinectPoseEnum;
import kinectThreads.KinectUserActionEnum;
import control.SharedSocket;
import services.KinectPoseServiceEvent;
import services.KinectSkeletonServiceEvent;
import services.KinectUserActionServiceEvent;
import iservices.IKinectPoseService;
import iservices.IKinectSkeletonService;
/**
 * This class handles the Kinect Pose Events
 * @author Santiago Hors Fraile
 *
 */
public class KinectPoseHandler implements IKinectPoseService, IKinectSkeletonService {
	
	
	String currentMovementState="";
	/**
	 * Represents whether the user's left leg is risen.
	 */
	private boolean leftLegUp;
	/**
	 * Represents whether the user's left leg is risen.
	 */
	private boolean rightLegUp;
	/**
	 * Represents when the user's last left step was done.
	 */
	private long lastLeftStep;
	/**
	 * Represents when the user's last right step was done.
	 */
	private long lastRightStep;
	/**
	 * Represents the last movement state (WALK vs STAND).
	 */
	private String lastMovementState;
	/**
	 * Represents when the user's last step was done.
	 */
	private long lastStep;

	/**
	 * Represents whether the user has done a opened hug movement.
	 */
	private boolean openedHugDone;
	/**
	 * Represents whether the user has done a closed hug movement.
	 */
	private boolean hugCompleted;
	
	/**
	 * Represents the last game state (Active vs Paused) 
	 */
	private String generalGameState; 
	/**
	 * Represents what the last reach level was.
	 */
	private int lastReached;
	/**
	 * Represents the current reach level.
	 */
	private int reached;
	/**
	 * Represents whether the game is paused
	 */
	private boolean pause;
	/**
	 * Represents whether the game is resumed(active)
	 */
	private boolean resume;
	/**
	 * Represents whether the user has done a cancel pose.
	 */
	private boolean cancel;
	/**
	 * Represents whether the last users's pose was a cancel.  
	 */
	private boolean lastCancel;
	/**
	 * Represents when the last cancel pose was done.
	 */
	private long lastTimeCancel;
	/**
	 * Represents whether the user has done an accept pose.
	 */
	private boolean accept;
	/**
	 * Represents whether the last users's pose was an accept.  
	 */
	private boolean lastAccept;
	/**
	 * Represents when the last accept pose was done.
	 */
	private long lastTimeAccept;
	/**
	 * Represents whether the user has done an pickedUpFromLeft pose.
	 */
	private boolean pickedUpFromLeft;
	/**
	 * Represents whether the last users's pose was a pick up from the left side.  
	 */
	private boolean lastPickedUpFromLeft;
	/**
	 * Represents when the last picking up from the left side pose was done.
	 */
	private long lastTimePickedUpFromLeft;
	/**
	 * Represents whether the user has done an pickedUpFromRight pose.
	 */
	private boolean pickedUpFromRight;
	/**
	 * Represents whether the last users's pose was a pick up from the right side.  
	 */
	private boolean lastPickedUpFromRight;
	/**
	 * Represents when the last picked up from the right side pose was done.
	 */
	private long lastTimePickedUpFromRight;
	/**
	 * Constructor without parameters. Initializes all the necessary variables.
	 */
	public KinectPoseHandler() {
		leftLegUp=false;
		rightLegUp=false;
		lastLeftStep=0;
		lastRightStep=0;	
		lastMovementState="";
		lastStep=0;	
		openedHugDone=false;
		hugCompleted=false;
		generalGameState= "Active";
		lastReached=0;
		reached=0;
		pause=false;
		resume=false;	
		accept=false;
		lastAccept=false;
		lastTimeAccept=0;		
		cancel=false;
		lastCancel=false;
		lastTimeCancel=0;		
		pickedUpFromLeft=false;
		lastPickedUpFromLeft=false;
		lastTimePickedUpFromLeft=0;	
	}

	@Override
	/**
	 * Updates the state of this handler with the KinectPoseServiceEvent received.
	 */
	public void kinectPoseUpdate(KinectPoseServiceEvent se) {

		if (se.getKinectPose().name().compareTo(KinectPoseEnum.WALK_LEFT_LEG_UP.name()) == 0) {
		
			if (!leftLegUp) {
				lastLeftStep = System.currentTimeMillis();
				lastStep = lastLeftStep;
				lastMovementState = KinectUserActionEnum.WALK.name();
				leftLegUp = true;
				rightLegUp = false;
			}
		} else if (se.getKinectPose().name().compareTo(KinectPoseEnum.WALK_RIGHT_LEG_UP.name()) == 0) {
			if (!rightLegUp) {
				lastRightStep = System.currentTimeMillis();
				lastStep = lastRightStep;
				lastMovementState = KinectUserActionEnum.WALK.name();
				rightLegUp = true;
				leftLegUp = false;
			}
		} else if (se.getKinectPose().name()
				.compareTo(KinectPoseEnum.OPENED_HUG.name()) == 0) {

			openedHugDone = true;
		} else if (se.getKinectPose().name()
				.compareTo(KinectPoseEnum.CLOSED_HUG.name()) == 0) {

			if (openedHugDone) {
				openedHugDone = false;
				hugCompleted = true;
			}
		}else if(se.getKinectPose().name().compareTo(KinectPoseEnum.CROSSED_HANDS_ABOVE_SHOULDERS.name())==0){
			pause=true;
			resume=false;
		}else if(se.getKinectPose().name().compareTo(KinectPoseEnum.HANDS_ABOVE_SHOULDERS_PSI_POSE.name())==0){
			resume=true;
			pause=false;
		}else if(se.getKinectPose().name().compareTo(KinectPoseEnum.LEFT_HAND_ABOVE_LEFT_SHOULDER.name())==0 ||
				se.getKinectPose().name().compareTo(KinectPoseEnum.RIGHT_HAND_ABOVE_RIGHT_SHOULDER.name())==0){
			accept=true;
			if(lastAccept==false){
				lastTimeAccept=System.currentTimeMillis();
			}
			
		}else if(se.getKinectPose().name().compareTo(KinectPoseEnum.LEFT_HAND_BENEATH_LEFT_ELBOW_SEPARATED_FROM_LEFT_HIP.name())==0||
				se.getKinectPose().name().compareTo(KinectPoseEnum.RIGHT_HAND_BENEATH_RIGHT_ELBOW_SEPARATED_FROM_RIGHT_HIP.name())==0){
			cancel=true;
			if(lastCancel==false){
				lastTimeCancel=System.currentTimeMillis();
			}
			
		}else if(se.getKinectPose().name().compareTo(KinectPoseEnum.LEFT_SHOULDER_LOWER_AND_CLOSER.name())==0){
			pickedUpFromRight=true;
			lastTimePickedUpFromRight=System.currentTimeMillis();
		}else if(se.getKinectPose().name().compareTo(KinectPoseEnum.RIGHT_SHOULDER_LOWER_AND_CLOSER.name())==0){
			pickedUpFromLeft=true;
			lastTimePickedUpFromLeft=System.currentTimeMillis();
		}
	
		sendToTheSharedSocketIfNecessary();
	}

	@Override
	/**
	 * Updates the sate of this handler with the KinectSkeletonServiceEvent received.
	 */
	public void kinectUpdate(KinectSkeletonServiceEvent se) {
		double hip = (se.getLeftHip().getY()+se.getRightHip().getY())/2;
		double step = (se.getHead().getY()-hip+300)/11;
		if(se.getRightHand().getY()>step*10 && se.getLeftHand().getY()>step*10){
			reached=10;
		}else if(se.getRightHand().getY()>step*9 && se.getLeftHand().getY()>step*9){
			reached=9;
		}else if (se.getRightHand().getY()>step*8 && se.getLeftHand().getY()>step*8){
			reached=8;
		}else if (se.getRightHand().getY()>step*7 && se.getLeftHand().getY()>step*7){
			reached=7;
		}else if (se.getRightHand().getY()>step*6 && se.getLeftHand().getY()>step*6){
			reached=6;
		}else if (se.getRightHand().getY()>step*5 && se.getLeftHand().getY()>step*5){
			reached=5;
		}else if (se.getRightHand().getY()>step*4 && se.getLeftHand().getY()>step*4){
			reached=4;
		}else if (se.getRightHand().getY()>step*3 && se.getLeftHand().getY()>step*3){
			reached=3;
		}else if (se.getRightHand().getY()>step*2 && se.getLeftHand().getY()>step*2){
			reached=2;
		}else if (se.getRightHand().getY()>step*1 && se.getLeftHand().getY()>step*1){
			reached=1;
		}else{
			reached=0;
		}
		sendToTheSharedSocketIfNecessary();
	}
	
	

private void sendToTheSharedSocketIfNecessary() {
	
		if((lastMovementState.compareTo(KinectUserActionEnum.WALK.name())==0)&&System.currentTimeMillis()-lastStep>1000)
		{
			lastMovementState = KinectUserActionEnum.STAND.name();			
		}
		
		if(accept && System.currentTimeMillis()-lastTimeAccept>2000)
		{
			accept=false;
			lastAccept=false;
		}	
		
		if(cancel && System.currentTimeMillis()-lastTimeCancel>2000)
		{
			cancel=false;
			lastCancel=false;
		}	
		
		if(pickedUpFromLeft && System.currentTimeMillis()-lastTimePickedUpFromLeft>1000)
		{
			pickedUpFromLeft=false;
			lastPickedUpFromLeft=false;
			
		}
		
		if(pickedUpFromRight && System.currentTimeMillis()-lastTimePickedUpFromRight>1000)
		{
			pickedUpFromRight=false;
			lastPickedUpFromRight=false;
		}
		
		if(reached!=lastReached){
			lastReached=reached;
			KinectUserActionServiceEvent kuase = new KinectUserActionServiceEvent (KinectUserActionEnum.REACHED.name(),  reached);
			SharedSocket.getSharedSocket().performTransference(kuase);
			System.out.println("+++++++++++++++++++");
			System.out.println(" REACHED "+ reached);
			System.out.println("++++++++++++++++++++");
			
		} else if(currentMovementState.compareTo(lastMovementState)!=0){
			currentMovementState=lastMovementState;
			KinectUserActionServiceEvent kuase= new KinectUserActionServiceEvent(lastMovementState);
			SharedSocket.getSharedSocket().performTransference(kuase);
			System.out.println("+++++++++++++++++++");
			System.out.println(" ACTION "+currentMovementState);
			System.out.println("++++++++++++++++++++");
			
		} else if(hugCompleted){
			hugCompleted=false;
			KinectUserActionServiceEvent kuase= new KinectUserActionServiceEvent(KinectUserActionEnum.HUG.name());
			SharedSocket.getSharedSocket().performTransference(kuase);
			System.out.println("+++++++++++++++++++");
			System.out.println("HUG");
			System.out.println("++++++++++++++++++++");
		} else if(pause&& generalGameState.compareTo("Active")==0){
			pause=false;
			generalGameState="Paused";
			KinectUserActionServiceEvent kuase= new KinectUserActionServiceEvent(KinectUserActionEnum.PAUSE.name());
			SharedSocket.getSharedSocket().performTransference(kuase);
			System.out.println("+++++++++++++++++++");
			System.out.println("GAME PAUSE");
			System.out.println("++++++++++++++++++++");
		} else if(resume && generalGameState.compareTo("Paused")==0){
			pause=false;
			generalGameState="Active";
			KinectUserActionServiceEvent kuase= new KinectUserActionServiceEvent(KinectUserActionEnum.RESUME.name());
			SharedSocket.getSharedSocket().performTransference(kuase);
			System.out.println("+++++++++++++++++++");
			System.out.println("GAME ACTIVE");
			System.out.println("++++++++++++++++++++");
		} else 
		
		if(cancel && lastCancel==false){
			cancel=false;
			lastCancel=true;
			KinectUserActionServiceEvent kuase= new KinectUserActionServiceEvent(KinectUserActionEnum.CANCEL.name());
			SharedSocket.getSharedSocket().performTransference(kuase);
			System.out.println("+++++++++++++++++++");
			System.out.println("CANCEL");
			System.out.println("++++++++++++++++++++");
		}else if(accept && lastAccept==false){
			accept=false;
			lastAccept=true;
			KinectUserActionServiceEvent kuase= new KinectUserActionServiceEvent(KinectUserActionEnum.ACCEPT.name());
			SharedSocket.getSharedSocket().performTransference(kuase);
			System.out.println("+++++++++++++++++++");
			System.out.println("ACCEPT");
			System.out.println("++++++++++++++++++++");
		}else if(pickedUpFromLeft && lastPickedUpFromLeft==false){
			pickedUpFromLeft=false;
			lastPickedUpFromLeft=true;
			pickedUpFromRight=false;
			lastPickedUpFromRight=false;
			KinectUserActionServiceEvent kuase= new KinectUserActionServiceEvent(KinectUserActionEnum.PICKED_UP_FROM_LEFT.name());
			SharedSocket.getSharedSocket().performTransference(kuase);
			System.out.println("+++++++++++++++++++");
			System.out.println("PICKED UP FROM LEFT");
			System.out.println("++++++++++++++++++++");
		}else if(pickedUpFromRight && lastPickedUpFromRight==false){
			pickedUpFromRight=false;
			lastPickedUpFromRight=true;
			pickedUpFromLeft=false;
			lastPickedUpFromLeft=false;
			KinectUserActionServiceEvent kuase= new KinectUserActionServiceEvent(KinectUserActionEnum.PICKED_UP_FROM_RIGHT.name());
			SharedSocket.getSharedSocket().performTransference(kuase);
			System.out.println("+++++++++++++++++++");
			System.out.println("PICKED UP FROM RIGHT");
			System.out.println("++++++++++++++++++++");
		}
	}
}