package test;

import kinectThreads.KinectPoseEnum;
import kinectThreads.KinectUserActionEnum;
import control.SharedOutput;
import services.KinectUserActionServiceEvent;
import services.KinectPoseServiceEvent;
import iservices.IKinectPoseService;

public class ClaseQueImplementaAPose implements IKinectPoseService, Runnable {

	boolean leftLegUp;
	boolean rightLegUp;
	long lastLeftStep;
	long lastRightStep;
	int standStillInARaw;
	String lastMovementState;
	long lastStep;
	int userId;
	SharedOutput sharedOutput;
	
	long lastRisedHand;
	String lastRotationState;
	
	public ClaseQueImplementaAPose(){
		leftLegUp=false;
		rightLegUp=false;
		lastLeftStep=0;
		lastRightStep=0;
		standStillInARaw=0;
		lastMovementState="";
		lastStep=0;
		sharedOutput= SharedOutput.getSharedOutput();
		
		lastRisedHand=0;
		lastRotationState="";
	}
	
	@Override
	public void kinectPoseUpdate(KinectPoseServiceEvent se) {
		userId= se.getUserId();

		if(se.getKinectPose().name().compareTo(KinectPoseEnum.WALK_LEFT_LEG_UP.name())==0){
			lastLeftStep=System.currentTimeMillis();
			lastStep=lastLeftStep;
			if(!leftLegUp){
				if(System.currentTimeMillis()-lastRightStep>800){
						lastMovementState=KinectUserActionEnum.WALK.name();
				}else{
						lastMovementState=KinectUserActionEnum.RUN.name();
				}
				leftLegUp=true;
				rightLegUp=false;
			}
		}else if(se.getKinectPose().name().compareTo(KinectPoseEnum.WALK_RIGHT_LEG_UP.name())==0){
			lastRightStep=System.currentTimeMillis();
			lastStep=lastRightStep;
			if(!rightLegUp){
				if(System.currentTimeMillis()-lastLeftStep>800){
					lastMovementState=KinectUserActionEnum.WALK.name();
				}else{
					lastMovementState=KinectUserActionEnum.RUN.name();
				}
				rightLegUp=true;
				leftLegUp=false;				
			}
		}else if(se.getKinectPose().name().compareTo(KinectPoseEnum.HANDS_BACK.name())==0){
			lastMovementState=KinectUserActionEnum.BACKWARDS.name();
			lastStep=System.currentTimeMillis();
			rightLegUp=false;
			leftLegUp=false;
			lastMovementState=KinectUserActionEnum.BACKWARDS.name();
		}else if(se.getKinectPose().name().compareTo(KinectPoseEnum.RISED_LEFT_HAND.name())==0){
			lastRisedHand=System.currentTimeMillis();
			lastRotationState=KinectUserActionEnum.TURN_LEFT.name();
		}else if(se.getKinectPose().name().compareTo(KinectPoseEnum.RISED_RIGHT_HAND.name())==0){
			lastRisedHand=System.currentTimeMillis();
			lastRotationState=KinectUserActionEnum.TURN_RIGHT.name();
		}
	}

	
	@Override
	public void run() {
		String currentMovementState="";
		String currentRotationState="";
		while(true){
	
			if(currentMovementState.compareTo(KinectPoseEnum.HANDS_BACK.name())!=0&&System.currentTimeMillis()-lastStep>1000&& lastMovementState.compareTo(KinectPoseEnum.STAND.name())!=0){		
		
				lastMovementState=KinectPoseEnum.STAND.name();
				rightLegUp=false;
				leftLegUp=false;
			}else if(currentMovementState.compareTo(KinectPoseEnum.HANDS_BACK.name())==0&&System.currentTimeMillis()-lastStep>200&& lastMovementState.compareTo(KinectPoseEnum.STAND.name())!=0){		
				lastMovementState=KinectPoseEnum.STAND.name();
				rightLegUp=false;
				leftLegUp=false;
			}
				
			 if(System.currentTimeMillis()-lastRisedHand>500){
				 lastRotationState="NO_ROTATION";
			 }
		
			
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
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
				System.out.println("Girando "+currentRotationState);
				System.out.println("++++++++++++++++++++");
				
			}
			
		}
		
	}

}
