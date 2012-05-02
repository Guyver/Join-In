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
	String lastState;
	long lastStep;
	int userId;
	SharedOutput sharedOutput;
	
	public ClaseQueImplementaAPose(){
		leftLegUp=false;
		rightLegUp=false;
		lastLeftStep=0;
		lastRightStep=0;
		standStillInARaw=0;
		lastState=" ";
		lastStep=0;
		sharedOutput= SharedOutput.getSharedOutput();
	}
	
	@Override
	public void kinectPoseUpdate(KinectPoseServiceEvent se) {
		userId= se.getUserId();

		if(se.getKinectPose().name().compareTo(KinectPoseEnum.WALK_LEFT_LEG_UP.name())==0){
		
			
			lastLeftStep=System.currentTimeMillis();
			lastStep=lastLeftStep;
		
			if(!leftLegUp){
			
				if(System.currentTimeMillis()-lastRightStep>800){

						lastState=KinectUserActionEnum.WALK.name();
			
				}else{
				
						lastState=KinectUserActionEnum.RUN.name();
					
				}
				
				leftLegUp=true;
				rightLegUp=false;
			
			}
			
		}else if(se.getKinectPose().name().compareTo(KinectPoseEnum.WALK_RIGHT_LEG_UP.name())==0){

			lastRightStep=System.currentTimeMillis();
			lastStep=lastRightStep;
			
			if(!rightLegUp){
				if(System.currentTimeMillis()-lastLeftStep>800){
				
					lastState=KinectUserActionEnum.WALK.name();
			
				}else{
				
					lastState=KinectUserActionEnum.RUN.name();
			
				}
			
				rightLegUp=true;
				leftLegUp=false;
			
				
			}
			
		}
		
	}

	
	@Override
	public void run() {
		String currentState="";
		while(true){
	
			if(System.currentTimeMillis()-lastStep>1500&& lastState.compareTo(KinectPoseEnum.STAND.name())!=0){		
		
				lastState=KinectPoseEnum.STAND.name();
				rightLegUp=false;
				leftLegUp=false;
			}
	
			//This delay is introduced on purpose. DO NOT REMOVE
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
			if(currentState.compareTo(lastState)!=0){
				
				currentState=lastState;
			
				KinectUserActionServiceEvent juas= new KinectUserActionServiceEvent(userId,lastState);
				sharedOutput.performTransference(juas);
				
				System.out.println("+++++++++++++++++++");
				System.out.println(" Now you're "+currentState);
				System.out.println("++++++++++++++++++++");
				
				//Send the new state to the socket.
			}
			
		}
		
	}

}
