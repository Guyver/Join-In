package KinectPackage;

// LEDStatus.java
// Andrew Davison, ad@fivedots.coe.psu.ac.th, October 2011

// Kinect sensor LED status constants
// from http://openkinect.org/wiki/USB_Devices

public enum LEDStatus 
{
  LED_OFF(0),
  LED_GREEN(1),
  LED_RED(2),
  LED_ORANGE(3), 
  LED_BLINK_ORANGE(4),
  LED_BLINK_GREEN(5),
  LED_BLINK_RED_ORANGE(6);

  private int code;

  private LEDStatus(int c) 
  {  code = c; }

  public short getCode() 
  { return (short)code;  }

}  // end of LEDStatus class