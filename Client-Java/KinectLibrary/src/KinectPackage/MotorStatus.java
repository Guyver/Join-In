package KinectPackage;

// MotorStatus.java
// Andrew Davison, ad@fivedots.coe.psu.ac.th, October 2011

/* Kinect sensor motor status constants
   from http://openkinect.org/wiki/USB_Devices and
        http://openkinect.org/wiki/Protocol_Documentation
*/

public enum MotorStatus 
{
  STOPPED(0), AT_LIMIT(1), MOVING(4), QUICK_BREAK(8), UNKNOWN(-1);
    // why isn't 2 used?

  private int code;

  private MotorStatus(int c) 
  {  code = c; }

  public int getCode() 
  { return code;  }


  public static MotorStatus of(int code)
  // convert status code value into its MotorStatus enum object
  {
    switch (code) {
      case 0: return STOPPED;
      case 1: return AT_LIMIT;
      case 4: return MOVING;
      case 8: return QUICK_BREAK;
      default: return UNKNOWN;     // any other integer means unknown
    }
  }  // end of of()

}  // end of MotorStatus class