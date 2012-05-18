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
			new StartDevices();
		}catch(Exception e){
			e.printStackTrace();
		}
	}



	public StartDevices() throws Exception
	{    	
		int theUserIWant=1;
    	int maximumNumberOfKinectUsers =1;
     	//DeviceManager dm = DeviceManager.getDeviceManager("127.0.0.1", 7540,maximumNumberOfKinectUsers);
    	DeviceManager dm = DeviceManager.getDeviceManager("193.156.105.158", 7540,maximumNumberOfKinectUsers);
    	 
    	 
    	
    	
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
    	
    	
  
		
	
	}

}