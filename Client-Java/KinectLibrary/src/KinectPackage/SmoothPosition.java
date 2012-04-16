package KinectPackage;

// SmoothPosition.java
// Andrew Davison, October 2011, ad@fivedots.psu.ac.th

/* Store a list of Vector3d positions, up to a maximum of 
   MAX_POSNS, and return the average of those positions.

   If a null position is 'added' then the oldest position
   (the first one in the list) is deleted.

   When there are MAX_POSNS in the list, the addition of a
   new position causes the oldest one to be discarded.
*/


import java.util.*;
import javax.vecmath.*;


public class SmoothPosition
{
  private final static int MAX_POSNS = 10;

  private ArrayList<Vector3d> posns;


  public SmoothPosition()
  {
    posns = new ArrayList<Vector3d>();
  }  // end of SmoothPosition()


  public void addPosition(Vector3d p)
  {
    if (p == null) {
      if (!posns.isEmpty())
        posns.remove(0);    // remove oldest element when null 'added'
    }
    else {
      if (posns.size() == MAX_POSNS) 
        posns.remove(0);     // remove oldest
      posns.add(p);
    }
  }  // end of addPosition()



  public Vector3d getPosition()
  // return average of all the positions (or null)
  {
    if (posns.isEmpty())
      return null;  // since no positions to average
    else {
      double xSum = 0;
      double ySum = 0;
      double zSum = 0;
      int count = 0;
      for(Vector3d v : posns) {
        xSum += v.getX();
        ySum += v.getY();
        zSum += v.getZ();
        count++;
      }
      return new Vector3d(xSum/count, ySum/count, zSum/count);
    }
  }  // end of getPosition()


}  // end of SmoothPosition class