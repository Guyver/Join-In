package test;

import kinectThreads.KinectPoseEnum;
import kinectThreads.KinectUserActionEnum;
import control.SharedOutput;

import services.KinectUserActionServiceEvent;
import services.KinectPoseServiceEvent;
import services.KinectUserHugServiceEvent;
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
	
	long lastRotationPerfomed;
	String lastRotationState;
	
	boolean openedHugDone;
	boolean hugCompleted;
	
	boolean wait;

	long lastInProgressEventReceived;

	
	public ClaseQueImplementaAPose(){
		wait=false;
				leftLegUp=false;
		rightLegUp=false;
		lastLeftStep=0;
		lastRightStep=0;
		standStillInARaw=0;
		lastMovementState="";
		lastStep=0;
		sharedOutput= SharedOutput.getSharedOutput();
		
		lastRotationPerfomed=0;
		lastRotationState="";
		
		openedHugDone=false;
		hugCompleted=false;
		lastInProgressEventReceived=0;
	}
	
	@Override
	public void kinectPoseUpdate(KinectPoseServiceEvent se) {
		userId= se.getUserId();
		
		
		
		if(!se.isPosing()){//Será porque está en proceso
			wait=true;
			lastInProgressEventReceived=System.currentTimeMillis();
			
		}else {
		
			wait= false;
			
			
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
			}else if(se.getKinectPose().name().compareTo(KinectPoseEnum.BOTH_HANDS_BACK.name())==0){
			lastMovementState=KinectUserActionEnum.BACKWARDS.name();
			lastStep=System.currentTimeMillis();
			rightLegUp=false;
			leftLegUp=false;
			lastMovementState=KinectUserActionEnum.BACKWARDS.name();
			}else if(se.getKinectPose().name().compareTo(KinectPoseEnum.LEFT_HAND_BACK.name())==0){
			lastRotationPerfomed=System.currentTimeMillis();
			lastRotationState=KinectUserActionEnum.TURN_LEFT.name();
			}else if(se.getKinectPose().name().compareTo(KinectPoseEnum.RIGHT_HAND_BACK.name())==0){
			lastRotationPerfomed=System.currentTimeMillis();
			lastRotationState=KinectUserActionEnum.TURN_RIGHT.name();
			}else if(se.getKinectPose().name().compareTo(KinectPoseEnum.OPENED_HUG.name())==0){
			openedHugDone=true;
			}else if(se.getKinectPose().name().compareTo(KinectPoseEnum.CLOSED_HUG.name())==0){
				if(openedHugDone){
				openedHugDone=false;
				hugCompleted=true;
				}
			}
		}
	}

	
	@Override
	public void run() {
		String currentMovementState="";
		String currentRotationState="";
		while(true){
	
			//Walking or running
			if(currentMovementState.compareTo(KinectUserActionEnum.BACKWARDS.name())!=0&&System.currentTimeMillis()-lastStep>1000&& lastMovementState.compareTo(KinectUserActionEnum.STAND.name())!=0){		
				if(!wait){
					lastMovementState=KinectUserActionEnum.STAND.name();
					rightLegUp=false;
					leftLegUp=false;
				}else{
					if(System.currentTimeMillis()-lastInProgressEventReceived>150){
						wait=false;
					}
					System.out.println("Debería parar pero por la mejor no lo hago");
					
				}
				
			//Moving backwards
			}else if(currentMovementState.compareTo(KinectUserActionEnum.BACKWARDS.name())==0&&System.currentTimeMillis()-lastStep>1&& lastMovementState.compareTo(KinectUserActionEnum.STAND.name())!=0){		
				
					lastMovementState=KinectUserActionEnum.STAND.name();
					rightLegUp=false;
					leftLegUp=false;
					
			}
				
			 if(System.currentTimeMillis()>lastRotationPerfomed){
				 lastRotationState=KinectUserActionEnum.NO_ROTATION.name();
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
