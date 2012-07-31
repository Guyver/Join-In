/**
 * Copyright 2012 Santiago Hors Fraile
 * Heavily based on SkelsManager.java by Andrew Davison, October 2011, ad@fivedots.psu.ac.th

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

package KinectPackage;


import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

import org.OpenNI.*;



import javax.vecmath.Vector3d;



public class SkeletonManager
{
  // OpenNI
  private UserGenerator userGenerator;

  // capabilities used by userGeneratorerator
  private SkeletonCapability skeletonCapability;
        // to output skeletal data, including the location of the joints
  private PoseDetectionCapability poseDetectionCapability;
        // to recognize when the user is in a specific position

  private String calibPose = null;
  
  // Java3D
  private HashMap<Integer, Skeleton3D> userSkels3D;
    // maps user IDs --> a 3D skeleton

  private int maximumNumberOfKinectUsers; 
  
  private List<Integer> idOfUsersWhoAreBeingWatched;
 
  public IObserver<UserEventArgs> newUserObserver;
  public IObserver<UserEventArgs> lostUserObserver;
  public IObserver<UserEventArgs> exitUserObserver;
  public IObserver<UserEventArgs> reEnterUserObserver;
  public IObserver<PoseDetectionEventArgs> poseDetectedObserver;
  public IObserver<CalibrationStartEventArgs> calibrationStartObserver;
  public IObserver<CalibrationProgressEventArgs> calibrationCompleteObserver;


  private LEDStatus switchToLEDColor;
  
  
  /**
	 * Contains all KinectUserOutOfScope listeners.
	 */
  List<IKinectUserOutOfScopeListener> kinectUserOutOfScopeListenersList;


  
  public SkeletonManager(UserGenerator userGenerator, int maximumNumberOfKinectUsers)
  {
	this.maximumNumberOfKinectUsers=maximumNumberOfKinectUsers;
    this.userGenerator = userGenerator;
    configure();
    idOfUsersWhoAreBeingWatched=new LinkedList<Integer>();
    userSkels3D = new HashMap<Integer, Skeleton3D>();
    switchToLEDColor=LEDStatus.LED_BLINK_RED_ORANGE;
    kinectUserOutOfScopeListenersList= new CopyOnWriteArrayList<IKinectUserOutOfScopeListener>();

    
  } // end of SkelsManager()
  
  
  
  public SkeletonManager(UserGenerator userGenerator)
  {
    this.userGenerator = userGenerator;
    configure();
    idOfUsersWhoAreBeingWatched=new LinkedList<Integer>();
    userSkels3D = new HashMap<Integer, Skeleton3D>();
    maximumNumberOfKinectUsers= 1;
    switchToLEDColor=LEDStatus.LED_BLINK_RED_ORANGE;
    kinectUserOutOfScopeListenersList= new CopyOnWriteArrayList<IKinectUserOutOfScopeListener>();
   
  } // end of SkelsManager()
	
  /**
	 * Adds a new OutOfScopeListener to the listeners list.
	 * 
	 * @param li
	 *            The new listener to be add.
	 */
	public void addKinectUserOutOfScopeListener(IKinectUserOutOfScopeListener li) {
		this.kinectUserOutOfScopeListenersList.add(li);
	}
  
  private void configure()
  /* create pose and skeleton detection capabilities for the user generator, 
     and set up observers (listeners)   */
  {
	  try {
	      // setup userGeneratorerator pose and skeleton detection capabilities;
	      // should really check these using ProductionNode.isCapabilitySupported()
	      poseDetectionCapability = userGenerator.getPoseDetectionCapability();

	      skeletonCapability = userGenerator.getSkeletonCapability();
	      calibPose = skeletonCapability.getSkeletonCalibrationPose();   // the 'psi' pose
	      skeletonCapability.setSkeletonProfile(SkeletonProfile.ALL);
	             // other possible values: UPPER_BODY, LOWER_BODY, HEAD_HANDS

	      newUserObserver = new NewUserObserver();
	      lostUserObserver = new LostUserObserver();
	      exitUserObserver = new ExitUserObserver();
	      reEnterUserObserver= new ReEnterUserObserver();
	      poseDetectedObserver = new PoseDetectedObserver();
	      calibrationStartObserver=  new CalibrationStartObserver();
	      calibrationCompleteObserver= new CalibrationCompleteObserver();
	      // set up 7 observer
	        
	      
	      userGenerator.getNewUserEvent().addObserver(newUserObserver);  // new user found
	      userGenerator.getLostUserEvent().addObserver(lostUserObserver);  // lost a user
	      userGenerator.getUserExitEvent().addObserver(exitUserObserver);        // user has exited (but may re-enter)
	      userGenerator.getUserReenterEvent().addObserver(reEnterUserObserver);  // user has re-entered
	      
	      poseDetectionCapability.getPoseDetectedEvent().addObserver( poseDetectedObserver);    // for when a pose is detected
	      
	      skeletonCapability.getCalibrationStartEvent().addObserver(calibrationStartObserver);    // calibration is starting
	      skeletonCapability.getCalibrationCompleteEvent().addObserver(calibrationCompleteObserver );   
	             // for when skeleton calibration is completed, and tracking starts
	     
	    } 
	    catch (Exception e) {
	      System.out.println(e);
	      System.exit(1);
	    }
  }  // end of configure()




  public void update()
  // update skeleton of each user being tracked
  {
    try {   
      int[] userIDs = userGenerator.getUsers();     // there may be many users in the scene
      for (int i = 0; i < userIDs.length; i++) {
        int userID = userIDs[i];
        if (skeletonCapability.isSkeletonCalibrating(userID))
          continue;    // test to avoid occasional crashes with isSkeletonTracking()
        if (skeletonCapability.isSkeletonTracking(userID)){
        	//System.out.println("El userSkels es "+userSkels3D);
        	if(!userSkels3D.isEmpty()){
        		userSkels3D.get(userID).update();
        	}
        }
      }
    }
    catch (StatusException e) 
    {  System.out.println(e); }
  }  // end of update()


  public Vector3d getJoint3D(int userIDParam, SkeletonJoint joint){
	
	  Vector3d returningObject = new Vector3d(0,0,0);
	  try{
	  int[] userIDs = userGenerator.getUsers();     // there may be many users in the scene
      for (int i = 0; i < userIDs.length; i++) {
        int userID = userIDs[i];
        if (!skeletonCapability.isSkeletonCalibrating(userID)&& (skeletonCapability.isSkeletonTracking(userID))&& (userID==userIDParam)){
       	 
        	returningObject= userSkels3D.get(userID).getJoint3D(joint);
        	
        }
     
      }
	  }catch(NoJointDataException e){
	
		  e.printStackTrace();
	  }catch(Exception e){
		  
	  }
	  return returningObject;
  }
  
  public UserGenerator getUserGenerator(){
	  return userGenerator;
	  
  }
  /**
	 * Sends a new KinectUserOutOfScopeEvent to the listeners registered in the kinectUserOutOfScopeListenersList.
	 * 
	 * @param keout
	 *            The new KinectEvent
	 */
	private void fireKinectUserOutOfScopeEvent(KinectUserOutOfScopeEvent kuoose) {
		try{
						
			Iterator<IKinectUserOutOfScopeListener> it = kinectUserOutOfScopeListenersList.iterator();
	
			while (it.hasNext()) {
	
				IKinectUserOutOfScopeListener koosl = it.next();
	
				(new Thread(new KinectUserOutOfScopeEventLauncher(koosl, kuoose))).start();
			}
		
		}catch(Exception e){
			
			System.out.println("Error at firing the kinectUserOutOfScopeListenersList in KinectManager");
			e.printStackTrace();
		}
	}
  
  // ----------------- 7 observers -----------------------
  /*   user detection --> pose detection --> skeleton calibration starts -->
       skeleton calibration finish --> skeleton tracking 
       (causes the creation of userSkels3D entry + scene graph)

    +  exit --> re-entry of user
       (3D skeleton is made invisible/visible)
null
    +  lose a user  
       (causes the deletion of its userSkels3D entry + scene graph)
  */
 



	/**
 * @return the switchToLEDColor
 */
public LEDStatus getSwitchToLEDColor() {
	return switchToLEDColor;
}



/**
 * @param switchToLEDColor the switchToLEDColor to set
 */
public void setSwitchToLEDColor(LEDStatus switchToLEDColor) {
	this.switchToLEDColor = switchToLEDColor;
}
/**
 * @author Santiago Hors Fraile
 */
	class NewUserObserver implements IObserver<UserEventArgs> {

		@Override
		public void update(IObservable<UserEventArgs> observable,
				UserEventArgs args) {

			System.out.println("Detected new user " + args.getId());

			try {
				if (idOfUsersWhoAreBeingWatched.size() < maximumNumberOfKinectUsers
						&& !idOfUsersWhoAreBeingWatched.contains(new Integer(
								args.getId()))) {

					idOfUsersWhoAreBeingWatched.add(new Integer(args.getId()));

					switchToLEDColor = LEDStatus.LED_ORANGE;

					if (idOfUsersWhoAreBeingWatched.size() == maximumNumberOfKinectUsers) {

						System.out.println("The number of users is "
								+ userGenerator.getNumberOfUsers());

						userGenerator.getNewUserEvent().deleteObserver(
								newUserObserver);

						System.out.println("I stop detecting more users");
					}

					System.out.println("Taking care of user " + args.getId());

					poseDetectionCapability.startPoseDetection(calibPose,
							args.getId());

				} else {

				}

				// try to detect a pose for the new user

			} catch (StatusException e) {
				e.printStackTrace();
			}

		}

	} // end of NewUserObserver inner class
	/**
	 * @author Santiago Hors Fraile
	 */
	class LostUserObserver implements IObserver<UserEventArgs> {
		@Override
		public void update(IObservable<UserEventArgs> observable,
				UserEventArgs args) {

			int userID = args.getId();
			System.out.println("Lost track of user " + userID);

			try {

				userGenerator.getNewUserEvent().addObserver(newUserObserver);

			} catch (StatusException e) {
				e.printStackTrace();
			}

			// Fire KinectUserOutOfScopeEvent
			fireKinectUserOutOfScopeEvent(new KinectUserOutOfScopeEvent(userID));

		}
	} // end of LostUserObserver inner class
	/**
	 * @author Santiago Hors Fraile
	 */
	class ExitUserObserver implements IObserver<UserEventArgs> {
		@Override
		public void update(IObservable<UserEventArgs> observable,
				UserEventArgs args) {

			int userID = args.getId();

			switchToLEDColor = LEDStatus.LED_BLINK_RED_ORANGE;

			idOfUsersWhoAreBeingWatched.remove(new Integer(userID));

			// Fire KinectUserOutOfScopeEvent
			fireKinectUserOutOfScopeEvent(new KinectUserOutOfScopeEvent(userID));

			userSkels3D.remove(userID);

		}
	} // end of ExitUserObserver inner class
	/**
	 * @author Santiago Hors Fraile
	 */
	class ReEnterUserObserver implements IObserver<UserEventArgs> {
		@Override
		public void update(IObservable<UserEventArgs> observable,
				UserEventArgs args) {

			int userID = args.getId();
			System.out.println("Reentry of user " + userID);

			switchToLEDColor = LEDStatus.LED_GREEN;
			// make 3D skeleton visible when user re-enters

			Skeleton3D skel = new Skeleton3D(userID, skeletonCapability, false);
			userSkels3D.put(userID, skel);

		}
	} // end of ReEnterUserObserver inner class

	class PoseDetectedObserver implements IObserver<PoseDetectionEventArgs> {
		@Override
		public void update(IObservable<PoseDetectionEventArgs> observable,
				PoseDetectionEventArgs args) {

			int userID = args.getUser();

			System.out.println(args.getPose() + " pose detected for user "
					+ userID);

			try {
				// finished pose detection; switch to skeleton calibration

				poseDetectionCapability.stopPoseDetection(userID);
				skeletonCapability.requestSkeletonCalibration(userID, true);
			} catch (StatusException e) {
				e.printStackTrace();
			}

		}
	} // end of PoseDetectedObserver inner class

	class CalibrationStartObserver implements
			IObserver<CalibrationStartEventArgs> {

		@Override
		public void update(IObservable<CalibrationStartEventArgs> observable,
				CalibrationStartEventArgs args) {
			switchToLEDColor = LEDStatus.LED_BLINK_GREEN;
			System.out
					.println("Calibration started for user " + args.getUser());

		}
	} // end of CalibrationStartObserver inner class

	class CalibrationCompleteObserver implements
			IObserver<CalibrationProgressEventArgs> {

		@Override
		public void update(
				IObservable<CalibrationProgressEventArgs> observable,
				CalibrationProgressEventArgs args) {
			int userID = args.getUser();

			try {
				if (args.getStatus() == CalibrationProgressStatus.OK) {
					// calibration succeeded; move to skeleton tracking
					System.out.println("Starting tracking user " + userID);
					skeletonCapability.startTracking(userID);

					switchToLEDColor = LEDStatus.LED_GREEN;

					// create skeleton3D in userSkels3D, and add to scene
					Skeleton3D skel = new Skeleton3D(userID,
							skeletonCapability, false);
					userSkels3D.put(userID, skel);

				} else { // calibration failed; return to pose detection

					poseDetectionCapability.startPoseDetection(calibPose,
							userID);
				}
			} catch (StatusException e) {
				e.printStackTrace();
			}

		}
	} // end of CalibrationCompleteObserver inner class

	/**
	 * @author Santiago Hors Fraile
	 */
	class KinectUserOutOfScopeEventLauncher implements Runnable {
		/**
		 * Represents the interface of the KinectUserOutOfScope listener.
		 */
		IKinectUserOutOfScopeListener kuoosl;
		/**
		 * Represents the KinectUserOutOfScope event.
		 */
		KinectUserOutOfScopeEvent kuoose;

		/**
		 * Sets the fields of this inner class with the given parameter.
		 * 
		 * @param kuoosl
		 *            The new IKinectUserOutOfScopeListener.
		 * @param kuoose
		 *            The new KinectUserOutOfScopeEvent.
		 */
		KinectUserOutOfScopeEventLauncher(IKinectUserOutOfScopeListener kuoosl,
				KinectUserOutOfScopeEvent kuoose) {
			this.kuoosl = kuoosl;
			this.kuoose = kuoose;
		}

		/**
		 * Calls to noninUpdate while it is running.
		 */
		@Override
		public void run() {

			kuoosl.kinectUpdate(kuoose);
		}
	}
} // end of SkelsManager class

