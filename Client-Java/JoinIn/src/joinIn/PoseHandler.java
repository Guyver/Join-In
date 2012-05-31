package joinIn;

import kinectThreads.KinectPoseEnum;
import kinectThreads.KinectUserActionEnum;
import control.SharedOutput;
import services.KinectPoseServiceEvent;
import services.KinectUserActionServiceEvent;
import services.KinectUserHugServiceEvent;
import iservices.IKinectPoseService;

public class PoseHandler implements IKinectPoseService, Runnable {

	boolean leftLegUp;
	boolean rightLegUp;
	long lastLeftStep;
	long lastRightStep;
	int standStillInARaw;
	String lastMovementState;
	long lastStep;
	int userId;
	SharedOutput sharedOutput;

	long lastRotationPerfomed;
	String lastRotationState;

	boolean openedHugDone;
	boolean hugCompleted;

	

	public PoseHandler() {

		leftLegUp = false;
		rightLegUp = false;
		lastLeftStep = 0;
		lastRightStep = 0;
		standStillInARaw = 0;
		lastMovementState = "";
		lastStep = 0;
		sharedOutput = SharedOutput.getSharedOutput();

		lastRotationPerfomed = 0;
		lastRotationState = "";

		openedHugDone = false;
		hugCompleted = false;
		
	}

	@Override
	public void kinectPoseUpdate(KinectPoseServiceEvent se) {
		userId = se.getUserId();

		
		

			if (se.getKinectPose().name()
					.compareTo(KinectPoseEnum.WALK_LEFT_LEG_UP.name()) == 0) {

				
				if (!leftLegUp) {
				lastLeftStep = System.currentTimeMillis();
				lastStep = lastLeftStep;
					if (System.currentTimeMillis() - lastRightStep > 500) {
						lastMovementState = KinectUserActionEnum.WALK.name();
					} else {
						lastMovementState = KinectUserActionEnum.RUN.name();
					}
					leftLegUp = true;
					rightLegUp = false;
				}
			} else if (se.getKinectPose().name()
					.compareTo(KinectPoseEnum.WALK_RIGHT_LEG_UP.name()) == 0) {

				
				if (!rightLegUp) {
					lastRightStep = System.currentTimeMillis();
				lastStep = lastRightStep;
					if (System.currentTimeMillis() - lastLeftStep > 500) {
						lastMovementState = KinectUserActionEnum.WALK.name();
					} else {
						lastMovementState = KinectUserActionEnum.RUN.name();
					}
					rightLegUp = true;
					leftLegUp = false;
				}
			} else if (se.getKinectPose().name()
					.compareTo(KinectPoseEnum.BOTH_HANDS_BACK.name()) == 0) {
				lastMovementState = KinectUserActionEnum.BACKWARDS.name();
				lastStep = System.currentTimeMillis();
				rightLegUp = false;
				leftLegUp = false;
				lastMovementState = KinectUserActionEnum.BACKWARDS.name();
			} else if (se.getKinectPose().name()
					.compareTo(KinectPoseEnum.RIGHT_SHOULDER_CLOSER_TO_THE_KINECT_THAN_THE_LEFT_SHOULDER.name()) == 0) {
				lastRotationPerfomed = System.currentTimeMillis();
				lastRotationState = KinectUserActionEnum.TURN_LEFT.name();
			} else if (se.getKinectPose().name()
					.compareTo(KinectPoseEnum.LEFT_SHOULDER_CLOSER_TO_THE_KINECT_THAN_THE_RIGHT_SHOULDER.name()) == 0) {
				lastRotationPerfomed = System.currentTimeMillis();
				lastRotationState = KinectUserActionEnum.TURN_RIGHT.name();
			} else if (se.getKinectPose().name()
					.compareTo(KinectPoseEnum.OPENED_HUG.name()) == 0) {
				openedHugDone = true;
			} else if (se.getKinectPose().name()
					.compareTo(KinectPoseEnum.CLOSED_HUG.name()) == 0) {
				if (openedHugDone) {
					openedHugDone = false;
					hugCompleted = true;
				}
			} else if (se.getKinectPose().name().compareTo(KinectPoseEnum.STAND.name()) == 0 && lastMovementState.compareTo(KinectUserActionEnum.BACKWARDS.name())!=0) {
				
				lastMovementState = KinectUserActionEnum.STAND.name();
				rightLegUp=false;
				leftLegUp=false;
				lastStep=System.currentTimeMillis();
				
			}
	}

	
	@Override
	public void run() {
		String currentMovementState="";
		String currentRotationState="";
		while(true){
			
			if((lastMovementState.compareTo(KinectUserActionEnum.WALK.name())==0||lastMovementState.compareTo(KinectUserActionEnum.RUN.name())==0)&&System.currentTimeMillis()-lastStep>1000){
				System.out.println("You're trying to cheat!");
				lastMovementState = KinectUserActionEnum.STAND.name();
				
			}else if(lastMovementState.compareTo(KinectUserActionEnum.BACKWARDS.name())==0&&System.currentTimeMillis()-lastStep>100){		
				
				lastMovementState=KinectUserActionEnum.STAND.name();
				rightLegUp=false;
				leftLegUp=false;
				
			}
				
			 if(System.currentTimeMillis()-lastRotationPerfomed>100){
				 lastRotationState=KinectUserActionEnum.NO_ROTATION.name();
			 }
		
			
			
			if(currentMovementState.compareTo(lastMovementState)!=0){
				currentMovementState=lastMovementState;
				KinectUserActionServiceEvent juas= new KinectUserActionServiceEvent(userId,lastMovementState);
				sharedOutput.performTransference(juas);
				
				System.out.println("+++++++++++++++++++");
				System.out.println(" Now you're "+currentMovementState);
				System.out.println("++++++++++++++++++++");
				
			}
			
			if(currentRotationState.compareTo(lastRotationState)!=0){
				currentRotationState=lastRotationState;
				KinectUserActionServiceEvent juas= new KinectUserActionServiceEvent(userId,lastRotationState);
				sharedOutput.performTransference(juas);
				
				System.out.println("+++++++++++++++++++");
				System.out.println("Turning "+currentRotationState);
				System.out.println("++++++++++++++++++++");
				
			}
			
			if(hugCompleted){
				hugCompleted=false;
				KinectUserHugServiceEvent juas= new KinectUserHugServiceEvent(userId);
				sharedOutput.performTransference(juas);
				System.out.println("+++++++++++++++++++");
				System.out.println("HUG");
				System.out.println("++++++++++++++++++++");
			}
		}
		
	}



}
