package joinIn;

import kinectThreads.KinectPoseEnum;
import kinectThreads.KinectUserActionEnum;
import control.SharedOutput;
import services.KinectPoseServiceEvent;
import services.KinectUserActionServiceEvent;
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
	
	boolean touchingOppositeEar;
	long lastTimeTouchedEar;
	
	String generalGameState; 
	
	boolean lastCrossedArms;
	
	int lastReached;
	int reached;
	
	boolean exit;
	boolean lastExit;
	long lastTimeExit;
	
	boolean cancel;
	boolean lastCancel;
	long lastTimeCancel;
	
	boolean accept;
	boolean lastAccept;
	long lastTimeAccept;

	

	public PoseHandler() {
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

		touchingOppositeEar=false;
		lastTimeTouchedEar=0;
		
		generalGameState= "Active";
		lastCrossedArms =false; 
		
		lastReached=0;
		reached=0;
		
		exit=false;
		lastExit=false;
		lastTimeExit=0;
		
		accept=false;
		lastAccept=false;
		lastTimeAccept=0;
		
		cancel=false;
		lastCancel=false;
		lastTimeCancel=0;
		
	}

	@Override
	public void kinectPoseUpdate(KinectPoseServiceEvent se) {

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
				.compareTo(KinectPoseEnum.OPENED_HUG.name()) == 0) {
		//	System.out.println("Opened hug");
			openedHugDone = true;
		} else if (se.getKinectPose().name()
				.compareTo(KinectPoseEnum.CLOSED_HUG.name()) == 0) {
		//	System.out.println("Closed hug");
			if (openedHugDone) {
				openedHugDone = false;
				hugCompleted = true;
			}
		}else if (se.getKinectPose().name().compareTo(KinectPoseEnum.TOUCHING_OPPOSITE_SHOULDER.name()) == 0) {		
				touchingOppositeEar=true;
				lastTimeTouchedEar=System.currentTimeMillis();
		}else if(se.getKinectPose().name().compareTo(KinectPoseEnum.CROSSED_HANDS_ABOVE_SHOULDERS.name())==0){
			exit=true;
			lastTimeExit=System.currentTimeMillis();
		}else if(se.getKinectPose().name().compareTo(KinectPoseEnum.LEFT_HAND_ABOVE_LEFT_SHOULDER.name())==0 ||
				se.getKinectPose().name().compareTo(KinectPoseEnum.RIGHT_HAND_ABOVE_RIGHT_SHOULDER.name())==0){
			accept=true;
			lastTimeAccept=System.currentTimeMillis();
		}else if(se.getKinectPose().name().compareTo(KinectPoseEnum.LEFT_HAND_BENEATH_LEFT_ELBOW_SEPARATED_50_CM_FROM_LEFT_HIP.name())==0||
				se.getKinectPose().name().compareTo(KinectPoseEnum.RIGHT_HAND_BENEATH_RIGHT_ELBOW_SEPATED_50_CM_FROM_RIGHT_HIP.name())==0){
			cancel=true;
			lastTimeCancel=System.currentTimeMillis();
		}
		

	}

	
	@Override
	public void run() {
		String currentMovementState="";

		while(true){
	
			try {
				Thread.sleep(400);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			if((lastMovementState.compareTo(KinectUserActionEnum.WALK.name())==0||lastMovementState.compareTo(KinectUserActionEnum.RUN.name())==0)&&System.currentTimeMillis()-lastStep>1000){
				//System.out.println("You're trying to cheat!");
				lastMovementState = KinectUserActionEnum.STAND.name();			
			}

			if(touchingOppositeEar && System.currentTimeMillis()-lastTimeTouchedEar>1000)
			{
				touchingOppositeEar=false;
				lastCrossedArms=false;
		
			}
			
			if(exit && System.currentTimeMillis()-lastTimeExit>1000)
			{
				exit=false;
				lastExit=false;
		
			}
			if(cancel && System.currentTimeMillis()-lastTimeCancel>1000)
			{
				cancel=false;
				lastCancel=false;
		
			}	
			if(accept && System.currentTimeMillis()-lastTimeAccept>1000)
			{
				accept=false;
				lastAccept=false;
		
			}
			
			
			if(reached!=lastReached){
				lastReached=reached;
				KinectUserActionServiceEvent juas = new KinectUserActionServiceEvent (KinectUserActionEnum.REACHED.name(),  reached);
				sharedOutput.performTransference(juas);
				System.out.println("+++++++++++++++++++");
				System.out.println(" REACHED "+ reached);
				System.out.println("++++++++++++++++++++");
				
			} else
			
			
			if(touchingOppositeEar && (touchingOppositeEar != lastCrossedArms)){
			
				KinectUserActionServiceEvent juas= null;
				if(generalGameState.compareTo("Active")==0 ){
					juas = new KinectUserActionServiceEvent(KinectUserActionEnum.PAUSE.name());
					generalGameState="Paused";
					lastCrossedArms=true;
					System.out.println("+++++++++++++++++++");
					System.out.println(" GAME PAUSED ");
					System.out.println("++++++++++++++++++++");
					
					}
				else if(generalGameState.compareTo("Paused")==0 ){
					juas = new KinectUserActionServiceEvent(KinectUserActionEnum.RESUME.name());
					generalGameState="Active";
					lastCrossedArms=true;
					System.out.println("+++++++++++++++++++");
					System.out.println(" GAME RESUMED ");
					System.out.println("++++++++++++++++++++");
				}
				
				sharedOutput.performTransference(juas);
	
			} else 
	
		
			
			if(currentMovementState.compareTo(lastMovementState)!=0){
				currentMovementState=lastMovementState;
			
				KinectUserActionServiceEvent juas= new KinectUserActionServiceEvent(lastMovementState);
				sharedOutput.performTransference(juas);
				
				System.out.println("+++++++++++++++++++");
				System.out.println(" ACTION "+currentMovementState);
				System.out.println("++++++++++++++++++++");
				
			} else 
			
			
	
			
			if(hugCompleted){
				hugCompleted=false;
				KinectUserActionServiceEvent juas= new KinectUserActionServiceEvent(KinectUserActionEnum.HUG.name());
				sharedOutput.performTransference(juas);
				
				System.out.println("+++++++++++++++++++");
				System.out.println("HUG");
				System.out.println("++++++++++++++++++++");
			} else 
			
			if(exit && lastExit==false){
				exit=false;
				lastExit=true;
				KinectUserActionServiceEvent juas= new KinectUserActionServiceEvent(KinectUserActionEnum.QUIT.name());
				sharedOutput.performTransference(juas);
				System.out.println("+++++++++++++++++++");
				System.out.println("EXIT");
				System.out.println("++++++++++++++++++++");
			} else 
			
			
			if(cancel && lastCancel==false){
				cancel=false;
				lastCancel=true;
				KinectUserActionServiceEvent juas= new KinectUserActionServiceEvent(KinectUserActionEnum.CANCEL.name());
				sharedOutput.performTransference(juas);
				System.out.println("+++++++++++++++++++");
				System.out.println("CANCEL");
				System.out.println("++++++++++++++++++++");
			} else 
			
			if(accept && lastAccept==false){
				accept=false;
				lastAccept=true;
				KinectUserActionServiceEvent juas= new KinectUserActionServiceEvent(KinectUserActionEnum.ACCEPT.name());
				sharedOutput.performTransference(juas);
				System.out.println("+++++++++++++++++++");
				System.out.println("ACCEPT");
				System.out.println("++++++++++++++++++++");
			}
		}
		
	}
		
	



}
