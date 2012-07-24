package joinIn;

import java.io.IOException;

import launchers.KinectSkeletonLauncher;
import launchers.KinectUserHugLauncher;
import launchers.KinectUserMovementLauncher;
import test.ClaseQueImplementaAPose;

import control.DeviceManager;


public class StartDevices {
	public static void main (String args []) throws IOException
	{		
		System.setProperty("bluecove.jsr82.psm_minimum_off", "true");
		try{
			String serverIP= args[0];
			short serverPort= Short.parseShort(args[1]);
			new StartDevices(serverIP, serverPort);
		}catch(Exception e){
			e.printStackTrace();
		}
	}



	public StartDevices(String serverIP, short serverPort) throws Exception
	{    	
		int theUserIWant=1;
    	int maximumNumberOfKinectUsers =1;
    	DeviceManager dm = DeviceManager.getDeviceManager(serverIP, serverPort,maximumNumberOfKinectUsers);
    	 
    	 
    	
    	
     	KinectUserMovementLauncher kumLauncher;
    	KinectUserHugLauncher kuhLauncher;
    	
    	
    	  
    	KinectSkeletonLauncher ksl= dm.getKinectSkeletonLauncher(theUserIWant);
		
		SkeletonJointHandler cqaks= new SkeletonJointHandler();
		
		ksl.addListener(cqaks);
    	
    	
    	kumLauncher= dm.getKinectUserMovementLauncher(theUserIWant);
    	kuhLauncher= dm.getKinectUserHugLauncher(theUserIWant);
    	
    	ClaseQueImplementaAPose jiji = new ClaseQueImplementaAPose();
    	
      	
    	kumLauncher.addListener(jiji);
    	kuhLauncher.addListener(jiji);
    
    	
    	Thread t1= new Thread(jiji);
    	t1.start();
    	
    	TiltingClass tiltingClass= new TiltingClass(dm);
    	
    	Thread t2 = new Thread(tiltingClass);
    	t2.start();
  
		
	
	}





}