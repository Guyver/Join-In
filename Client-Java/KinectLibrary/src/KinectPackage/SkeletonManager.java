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
  
  private List<Integer> idOfUsersWhoAreBeingTracked;
 
  public IObserver<UserEventArgs> newUserObserver;
  
  public SkeletonManager(UserGenerator userGenerator, int maximumNumberOfKinectUsers)
  {
	this.maximumNumberOfKinectUsers=maximumNumberOfKinectUsers;
    this.userGenerator = userGenerator;
    configure();
    idOfUsersWhoAreBeingTracked=new LinkedList<Integer>();
    userSkels3D = new HashMap<Integer, Skeleton3D>();

  } // end of SkelsManager()
  
  
  
  public SkeletonManager(UserGenerator userGenerator)
  {
    this.userGenerator = userGenerator;
    configure();
    idOfUsersWhoAreBeingTracked=new LinkedList<Integer>();
    userSkels3D = new HashMap<Integer, Skeleton3D>();
    maximumNumberOfKinectUsers= 1;
  } // end of SkelsManager()

  


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
      // set up 7 observers
      userGenerator.getNewUserEvent().addObserver(newUserObserver);  // new user found
      userGenerator.getLostUserEvent().addObserver(new LostUserObserver());  // lost a user
      userGenerator.getUserExitEvent().addObserver(new ExitUserObserver());        // user has exited (but may re-enter)
      userGenerator.getUserReenterEvent().addObserver(new ReEnterUserObserver());  // user has re-entered

      poseDetectionCapability.getPoseDetectedEvent().addObserver( new PoseDetectedObserver());    // for when a pose is detected
      
      skeletonCapability.getCalibrationStartEvent().addObserver( new CalibrationStartObserver());    // calibration is starting
      skeletonCapability.getCalibrationCompleteEvent().addObserver( new CalibrationCompleteObserver());   
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
        if (skeletonCapability.isSkeletonTracking(userID))
          userSkels3D.get(userID).update();
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
  
  
  // ----------------- 7 observers -----------------------
  /*   user detection --> pose detection --> skeleton calibration starts -->
       skeleton calibration finish --> skeleton tracking 
       (causes the creation of userSkels3D entry + scene graph)

    +  exit --> re-entry of user
       (3D skeleton is made invisible/visible)

    +  lose a user  
       (causes the deletion of its userSkels3D entry + scene graph)
  */
 

	class NewUserObserver implements IObserver<UserEventArgs> {
	
		public void update(IObservable<UserEventArgs> observable,
				UserEventArgs args) {
				
		
					System.out.println("Detected new user " + args.getId());
					
					try {  
						if(idOfUsersWhoAreBeingTracked.size()<maximumNumberOfKinectUsers&&!idOfUsersWhoAreBeingTracked.contains(new Integer(args.getId()))){
							idOfUsersWhoAreBeingTracked.add(new Integer(args.getId()));
							System.out.println("Taking care of user "+ args.getId());
					
							poseDetectionCapability.startPoseDetection(calibPose,args.getId());
					  
						}
						
						if(idOfUsersWhoAreBeingTracked.size()==maximumNumberOfKinectUsers){
							
							userGenerator.getNewUserEvent().deleteObserver(newUserObserver);
						
							System.out.println("I stop detecting more users");
						}else{
							
						}
						// try to detect a pose for the new user
					
					} catch (StatusException e) {
						e.printStackTrace();
					}
				
		}
		
  }  // end of NewUserObserver inner class



  class LostUserObserver implements IObserver<UserEventArgs>
  {
    public void update(IObservable<UserEventArgs> observable, UserEventArgs args)
    { 
    	
	      int userID = args.getId();
	      System.out.println("Lost track of user " + userID);
	      if(idOfUsersWhoAreBeingTracked.contains(new Integer(userID))){
	    	  idOfUsersWhoAreBeingTracked.remove(new Integer(userID));
	    	  try{
    		  userGenerator.getNewUserEvent().addObserver(newUserObserver);
	    	  }catch(StatusException e){
	    		  e.printStackTrace();
	    	  }
    		  
    	  }
	   	      
	      // delete skeleton from userSkels3D and the scene graph
	      Skeleton3D skel = userSkels3D.remove(userID);
	      
	      
	      
	      if (skel == null)
	        return;
	      skel.delete();
	     
    }
  } // end of LostUserObserver inner class



  class ExitUserObserver implements IObserver<UserEventArgs>
  {
    public void update(IObservable<UserEventArgs> observable, UserEventArgs args) {
    	
				int userID = args.getId();
				System.out.println("Exit of user " + userID);
				 Skeleton3D skel ;
				
				// make 3D skeleton invisible when user exits
				 skel = userSkels3D.get(userID);
				
				if (skel == null)
					return;
    	
    }
  } // end of ExitUserObserver inner class



  class ReEnterUserObserver implements IObserver<UserEventArgs>
  {
    public void update(IObservable<UserEventArgs> observable, UserEventArgs args)
    { 	
    	
      int userID = args.getId();
      System.out.println("Reentry of user " + userID);
    
      // make 3D skeleton visible when user re-enters
      Skeleton3D skel = userSkels3D.get(userID);
      if (skel == null)
        return;
    
    }
  } // end of ReEnterUserObserver inner class



  class PoseDetectedObserver implements IObserver<PoseDetectionEventArgs>
  {
	public void update(IObservable<PoseDetectionEventArgs> observable,
                                                     PoseDetectionEventArgs args)
    {
	
      int userID = args.getUser();
     
	      System.out.println(args.getPose() + " pose detected for user " + userID);
	      try {
	        // finished pose detection; switch to skeleton calibration
	    	
	        poseDetectionCapability.stopPoseDetection(userID);
	        skeletonCapability.requestSkeletonCalibration(userID, true);
	      }
	      catch (StatusException e)
	      {  e.printStackTrace(); }
	    
    }
  }  // end of PoseDetectedObserver inner class




  class CalibrationStartObserver implements IObserver<CalibrationStartEventArgs>
  {
	 
    public void update(IObservable<CalibrationStartEventArgs> observable,
                                        CalibrationStartEventArgs args)
    { System.out.println("Calibration started for user " + args.getUser());  }
  }  // end of CalibrationStartObserver inner class



  class CalibrationCompleteObserver implements IObserver<CalibrationProgressEventArgs>
  {
  
	public void update(IObservable<CalibrationProgressEventArgs> observable,
                                                    CalibrationProgressEventArgs args)
    {
      int userID = args.getUser();
                                                                        
      try {
        if (args.getStatus() == CalibrationProgressStatus.OK) {
          // calibration succeeded; move to skeleton tracking
          System.out.println("Starting tracking user " + userID);
          skeletonCapability.startTracking(userID);

          // create skeleton3D in userSkels3D, and add to scene
          Skeleton3D skel = new Skeleton3D(userID, skeletonCapability, false);
          userSkels3D.put(userID, skel);
      
      
        } 
        else   // calibration failed; return to pose detection
      
        	poseDetectionCapability.startPoseDetection(calibPose, userID);
      }
      catch (StatusException e)
      {  e.printStackTrace(); }
     
    }
  }  // end of CalibrationCompleteObserver inner class

 
  
} // end of SkelsManager class

