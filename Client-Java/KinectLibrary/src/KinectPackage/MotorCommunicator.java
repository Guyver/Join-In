package KinectPackage;

// MotorCommunicator.java
// Andrew Davison, ad@fivedots.coe.psu.ac.th, November 2011

/* Class for accessing the Kinect Sensor's motor
   USB info from  http://openkinect.org/wiki/USB_Devices

   --------

   This Java coded utilizes the libraries 
      * libusbjava (http://libusbjava.sourceforge.net/)
          **** with a MODIFIED version of Device.updateMaxPacketSize()
               due to Sivan Toledo to deal with devices with 0 endpoints

      * libusb-win32 (http://sourceforge.net/apps/trac/libusb-win32/wiki)

   Other requirements:
     - the motor must be plugged into a USB port;

     - a libusb-win32 device driver for the motor must have been created and
       installed into Windows (use inf-wizard.exe)

   Usage:
      > compile MotorCommunicator.java
      > run MotorCommunicator
*/


import ch.ntb.usb.*;


public class MotorCommunicator
{
  private static final short VENDOR_ID = (short)0x045e;
  private static final short PRODUCT_ID = (short)0x02b0;
          // the IDs were obtained by looking at the kinect motor using USBDeview

  // tilt range: + is up; - is down; 0 is straight forward
  private static final double MAX_ANGLE = 31;
  private static final double MIN_ANGLE = -31;


  public static final double ACCEL_COUNT = 819.0;    // counts/g
      // see http://www.kionix.com/accelerometers/accelerometer-KXSD9.html


  private Device dev = null;   // used to communicate with the USB device



  public MotorCommunicator()
  { 
    //System.out.println("Looking for device: (vendor: " + toHexString(VENDOR_ID) + "; product: " + toHexString(PRODUCT_ID) + ")");
    dev = USB.getDevice(VENDOR_ID, PRODUCT_ID);
    if (dev == null)
      System.out.println("Device not found");

    //System.out.println("Max packet size: " +
    //             dev.getDeviceDescriptor().getBMaxPacketSize0());
    try {
    //  System.out.println("Opening device");
      dev.open(1, 0, -1); 
      // open device with configuration 1, interface 0 and no alt interface
      //System.out.println("Opened device");
    }
    catch (USBException e) {
      System.out.println(e);
      System.exit(1);
    }
  }  // end of MotorCommunicator()

/*
  private String toHexString(int b)
  // chanage the hexadecimal integer into "0x.." string format
  {  
    String hex = Integer.toHexString(b);  
    if (hex.length() == 1)
      return "0x0" + hex;
    else
      return "0x" + hex;
  }  // end of toHexString()
*/

	
  public void close()
  {
    System.out.println("Closing device");
    try {
      if (dev != null)
        dev.close();
    }
    catch (USBException e) {
      System.out.println(e);
      System.exit(1);
    }
  }  // end of close()
	


  public void wait(int ms)
  // sleep for the specified no. of millisecs
  { 
    // System.out.println("Waiting " + ms + " ms...");
    try {
      Thread.sleep(ms);
    }
    catch(InterruptedException e) {}
  }  // end of wait()



  // -------------- set commands ------------------


  public boolean isReady()
  {
    byte[] buf = new byte[1];
    int rval = sendMessage(0xC0, 0x10, 0, buf, 1);
    // System.out.println("isReady: " + buf[0] + "; rval: " + rval);
    if (rval == -1)
      return false;
    return (buf[0] == 0x22);      // 0x22 (34) means is ready
  }  // end of isReady()



  private int sendMessage(int requestType, int request, int value,
                              byte[] data, int size)
  /* send a control transfer; the byte array, which may be modified */
  {
    int rval = -1;
    try {
      rval = dev.controlMsg(requestType, request, value, 0, 
                                      data, size, 2000, false);
      if (rval < 0)
        System.out.println("Control Transfer Error (" + rval + "):\n  " + 
                                             LibusbJava.usb_strerror() );
    }
    catch (USBException e) {
      System.out.println(e);
    }
    return rval;
  }  // end of sendMessage()



  public void setLED(LEDStatus status)
  {
    System.out.println("Setting LED to " + status);
    sendMessage(0x40, 0x06, status.getCode(),  new byte[1], 0);
  }  // end of setLED()



  public void setAngle(int angle)
  /* Send a USB control transfer to tilt the motor to the given angle;
     The Kinect angle range is -31 to 31, otherwise the engine may stall.
     + is up; - is down; 0 is the horizon
  */
  {
    System.out.println("Rotate to angle " + angle);
    if ((angle < MIN_ANGLE) || (angle > MAX_ANGLE))
      System.out.println("Angle outside tilt range: " + angle);
    else
      sendMessage(0x40, 0x31, (short)(2*angle),  new byte[1], 0);
  }  // end of setAngle()



  // -------------- get commands ------------------


  public byte[] getMotorInfo()
  // send a USB control transfer to the motor to read info
  {
    byte[] buf = new byte[10];
    sendMessage(0xC0, 0x32, 0, buf, 10);   // don't test result
    return buf;
  }  // end of getMotorInfo()



  public void printMotorInfo()
  {
    byte[] buf = getMotorInfo();
    System.out.print("motor data: ");
    for (int i=0; i < buf.length; i++)
      System.out.print("" + buf[i] + " ");
    System.out.println();
  }  // end of printMotorInfo()



  public int getSpeed()
  {
    byte[] buf = getMotorInfo();
    return (int) buf[1];
  }  // end of getSpeed()

  

  public int[] getAccel()
  // acceleration counts
  {
    byte[] buf = getMotorInfo();
    int[] accel = new int[3];   // for x, y, z accelerations counts
    // each acceleration is stored in a byte pair
    accel[0] = (int) (((short)buf[2] << 8) | buf[3]);  // x
    accel[1] = (int) (((short)buf[4] << 8) | buf[5]);  // y
    accel[2] = (int) (((short)buf[6] << 8) | buf[7]);  // z
    return accel;
  }  // end of getAccel()


  public double[] getAccelG()
  // accelerations as real g forces
  {
    int[] accel = getAccel();
    double[] accelG = new double[3];
    accelG[0] = accel[0]/ACCEL_COUNT;  // x
    accelG[1] = accel[1]/ACCEL_COUNT;  // y
    accelG[2] = accel[2]/ACCEL_COUNT;  // z
    return accelG;
  }  // end of getAccelG()


  public void printAccel()
  {
    int[] accel = getAccel();
    System.out.println("Acceleration counts (x,y,z): (" +
             accel[0]  + ", " + accel[1] + ", " +  accel[2] + ")" );
  }  // end of printAccel()


  public void printAccelG()
  {
    double[] accelG = getAccelG();
    System.out.printf("Acceleration (x,y,z): (%.3f, %.3f, %.3f)\n", 
                                            accelG[0], accelG[1], accelG[2]);
  }  // end of printAccelG()


  public int getAngle()
  {
    byte[] buf = getMotorInfo();

    if (buf[8] == -128) {
      System.out.println("Angle unavailable since Kinect is moving");
      return buf[8];
    }
    return ((int)buf[8])/2;   // Kinect returns a value that is 2*the actual angle
  }  // end of getAngle()


  public MotorStatus getStatus()
  {
    byte[] buf = getMotorInfo();
    int status = (int) buf[9];
    return MotorStatus.of(status);
  }  // end of getStatus()
  




}  // end of MotorCommunicator class
