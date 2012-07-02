/**
 * Copyright 2012 Santiago Hors Fraile
 * Heavily based on Skeleton3D.java by Andrew Davison, October 2011, ad@fivedots.psu.ac.th

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


import javax.media.j3d.*;
import javax.vecmath.*;




public class Skeleton3D
{
  // scaling from Kinect coords to 3D scene coords
  private static final float XY_SCALE = 1/500.0f;
  private static final float Z_SCALE = -1/1000.0f;

  // positioning of skeleton so feet rest on the checkerboard floor
  private static final float Y_OFFSET = 2.5f;
  private static final float Z_OFFSET = 5.0f;


  // collections of joints and limbs making up the skeleton
  private ArrayList<Joint3D> joints3D;


  private BranchGroup skelBG;      // top of the skeleton graph
  // private BranchGroup partsBG;     // where all the joints and limbs are attached

  private Switch visSW;            // for skeleton visibility
  private boolean isVisible;

HashMap<SkeletonJoint, Joint3D> jointsMap = null;

  public Skeleton3D(int userID, SkeletonCapability skelCap, boolean useFilter)
  {
    // create top of scene graph for the skeleton
    BranchGroup partsBG = buildSkelGraph();  
         // all the joints and limbs are attached to partsBG

    
    jointsMap=            new HashMap<SkeletonJoint, Joint3D>();
        // used for looking up joints when I create the limbs

    // build joints
    joints3D = new ArrayList<Joint3D>();
    
    buildJoints(joints3D, userID, skelCap, partsBG, jointsMap, useFilter);


   
  } // end of Skeleton3D()



  private BranchGroup buildSkelGraph()
  // creates the top-level scene graph for the skeleton
  {
    // skelBG-->visSW-->moveTG-->partsBG
    BranchGroup partsBG = new BranchGroup();
    partsBG.setCapability(Group.ALLOW_CHILDREN_READ);
    partsBG.setCapability(Group.ALLOW_CHILDREN_WRITE);

    Transform3D t3d = new Transform3D();
    t3d.set(new Vector3f(0, Y_OFFSET, Z_OFFSET));   // so feet are on the floor
    TransformGroup moveTG = new TransformGroup(t3d);
    moveTG.addChild(partsBG);

    // create switch for visibility
    visSW = new Switch();
    visSW.setCapability(Switch.ALLOW_SWITCH_WRITE);
    visSW.addChild( moveTG );
    visSW.setWhichChild( Switch.CHILD_ALL);   // visible initially
    isVisible = true;

    skelBG = new BranchGroup();
    skelBG.setCapability(BranchGroup.ALLOW_DETACH);   // so can be 'destroyed'
    skelBG.addChild(visSW);

    return partsBG;    // where all the joints and limbs are attached
  }  // end of buildSkelGraph()


  public BranchGroup getBG()
  {  return skelBG; }



  private void buildJoints(ArrayList<Joint3D> joints3D, int userID,
                          SkeletonCapability skelCap,
                          BranchGroup partsBG,
                          HashMap<SkeletonJoint, Joint3D> jointsMap,
                          boolean useFilter)
  /* create Joint3D objects, and connect their scene 
     graphs to the skeleton at partsBG  */
  {
    Joint3D j3d;
    for(SkeletonJoint joint : SkeletonJoint.values()) {
      j3d = buildJoint3D(joint, userID, skelCap, useFilter);
      if (j3d != null) {
        partsBG.addChild( j3d.getTG() );
        joints3D.add(j3d);
        jointsMap.put(joint, j3d);   // build map for later
      }
    }
  }  // end of buildJoints()



  private Joint3D buildJoint3D(SkeletonJoint joint, int userID,SkeletonCapability skelCap, boolean useFilter)
  // create a Joint3D object
  {
    if (!skelCap.isJointAvailable(joint) || !skelCap.isJointActive(joint)) {
      /* To deal with absence of WAIST,
         LEFT_COLLAR, LEFT_WRIST, LEFT_FINGER_TIP, LEFT_ANKLE,
         RIGHT_COLLAR, RIGHT_WRIST, RIGHT_FINGER_TIP, RIGHT_ANKLE
      */
       System.out.println(joint + " not available");
      return null;
    }

    Joint3D j3d;
    if(useFilter){
	    if (joint == SkeletonJoint.HEAD){
	      j3d = new Joint3D(joint, 0.22f, XY_SCALE, Z_SCALE, 
	                                           userID, skelCap);   // bigger head
	    }else{
	      j3d = new Joint3D(joint, XY_SCALE, Z_SCALE, userID, skelCap);
	    }
    }else{
    	if (joint == SkeletonJoint.HEAD){
  	      j3d = new Joint3D(joint, 0.22f, 1, 1, userID, skelCap);   // bigger head
  	    }else{
  	      j3d = new Joint3D(joint, 1, 1, userID, skelCap);
  	    }
    }
   
    return j3d;
  }  // end of buildJoint3D()

  public void update()
  //  Update the skeleton's joints and limbs if the skeleton is visible
  {
    if (!isVisible)
      return;

    // update joints
    for(Joint3D j3d : joints3D)
      j3d.update();

   
  }  // end of update()



  public void delete()
  // remove the entire skeleton from the scene graph
  {  skelBG.detach();  }
  
  
  public Vector3d getJoint3D(SkeletonJoint joint) throws NoJointDataException{

	  Joint3D j3d = jointsMap.get(joint);
	  
	 Vector3d returningObject= j3d.getPos();
	 if(returningObject==null){
		returningObject= new Vector3d(0,0,0);
	 } 
	 return returningObject;
	
	  
  }


} // end of Skeleton3D class

