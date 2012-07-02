package KinectPackage;

// Joint3D.java
// Andrew Davison, October 2011, ad@fivedots.coe.psu.ac.th

/* Joint3D defines the Java 3D scene graph for 
   a skeleton joint:
       moveTG-->visSW-->sphere

   The geometry is a sphere with a specfied radius. 
   It's appearance is a combination of a blue lighted material.
    
   moveTG allows the joint to be moved, and is the connection point
   to the rest of the 3D scene.

   visSW allows it to be made visible/invisible.

   There are two public methods:
     * update() - for updating the position of the joint;
     * setVisibility() - for changing its visibility
*/

import javax.media.j3d.*;
import com.sun.j3d.utils.geometry.*;

import javax.vecmath.*;

import org.OpenNI.*;



public class Joint3D
{

 

  private static final float RADIUS = 0.15f;

  // OpenNI
  private SkeletonCapability skelCap;
  private SkeletonJoint joint;

  private int userID;   // of skeleton containing this joint

  // the joint's scene graph: 
  //       moveTG-->visSW-->sphere
  private TransformGroup moveTG;    
  private Transform3D t3d;          // used for accessing a TG's transform
  private Switch visSW;            // for joint visibility


  private SmoothPosition smoothPosns;
  // private float radius;

  private float xyScale;
  private float zScale;
      // scaling from Kinext coords to 3D scene coords




  public Joint3D(SkeletonJoint joint, float xyScale, float zScale,
                                    int userID, SkeletonCapability skelCap)
  {  this(joint, RADIUS, xyScale, zScale, userID, skelCap);  }


  public Joint3D(SkeletonJoint j, float radius, float xyScale, float zScale,
                                       int userID, SkeletonCapability skelCap)
  {
    joint = j;
    this.xyScale = xyScale;
    this.zScale = zScale;

    this.userID = userID;
    this.skelCap = skelCap;

    smoothPosns = new SmoothPosition();

    Appearance app = new Appearance();

   
    // make the sphere with normals for lighting, and texture support
    Sphere sphere = new Sphere(radius, Primitive.GENERATE_NORMALS, app); 

    // create switch for visibility
    visSW = new Switch();
    visSW.setCapability(Switch.ALLOW_SWITCH_WRITE);
    visSW.addChild( sphere );
    visSW.setWhichChild( Switch.CHILD_ALL);   // visible initially



    // create a transform group for moving the sphere
    t3d = new Transform3D();
    moveTG = new TransformGroup(t3d);
    moveTG.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
    moveTG.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
    moveTG.addChild(visSW);
   }  // end of Joint3D()


  public SkeletonJoint getJoint()
  {  return joint; }


  public TransformGroup getTG()
  {  return moveTG;  }



  // -------------------- update position ---------------------


  public void update()
  // called by Skeleton3D
  {
    org.OpenNI.Point3D pos = getKinectPos();   // may be null
    setPos(pos);
  }


  private org.OpenNI.Point3D getKinectPos()
  // query Kinect for position of joint (may be null)
  {
    if (!skelCap.isJointAvailable(joint) || !skelCap.isJointActive(joint)) {
      System.out.println(joint + " not available");
      return null;
    }

    
    SkeletonJointPosition pos = null;
    try {
      pos = skelCap.getSkeletonJointPosition(userID, joint);
    }catch(StatusException e) {
    	
    }
    if (pos == null) {
      System.out.println("No update for " + joint);
      return null;
    }

    if (pos.getConfidence() == 0)
      return null;   

    return pos.getPosition();
  }  // end of getKinectPos()



  private void setPos(org.OpenNI.Point3D pos)
  // set joint position and visibility
  {
    if (pos == null)
      smoothPosns.addPosition(null);
    else {   // store a scaled position
      double x = (double) pos.getX()*xyScale;
      double y = (double) pos.getY()*xyScale;
      double z = (double) pos.getZ()*zScale;
      smoothPosns.addPosition( new Vector3d(x, y, z));
    }

    // use the average position to translate the joint
    Vector3d sPos = smoothPosns.getPosition();
    if (sPos != null) {
      
      t3d.set(sPos); 
      moveTG.setTransform(t3d); 
    }
  
  }  // end of setPos()


  public Vector3d getPos()
  {  return smoothPosns.getPosition();  }   // scaled position (or null)



  


}  // end of Joint3D class