/**
 * Copyright 2010 Santiago Hors Fraile and Salvador Jesús Romero

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

package test;


import java.io.IOException;

import kinectThreads.KinectEnhacedSkeletonLauncher;
import kinectThreads.KinectPoseEnum;



import launchers.KinectAbsoluteSpaceForATimeLauncher;

import launchers.KinectPoseLauncher;
import launchers.KinectSkeletonLauncher;
import launchers.KinectTotalSpaceTravelledForATimeLauncher;
import launchers.KinectUserJointReachPointLauncher;
import launchers.NoninLauncher;
import launchers.NunchukAnalogStickLauncher;
import launchers.NunchukButtonsLauncher;
import launchers.WiiBoardLauncher;
import launchers.WiiMoteAccelerationLauncher;
import launchers.WiiMoteButtonsLauncher;
import launchers.WiiMoteIRGlanceLauncher;
import launchers.WiiMoteIRLauncher;
import launchers.WiiMoteRotationLauncher;
import control.DeviceManager;

public class TestGrande 
{


	String eventstring="";
	WiiMoteAccelerationLauncher naal, naal2;
	WiiMoteButtonsLauncher wmbl;  
	WiiMoteRotationLauncher asdf;
	NunchukAnalogStickLauncher nasl;
	NunchukButtonsLauncher nb;
	WiiMoteRotationLauncher r;
	NoninLauncher nonLaunch;
	NunchukButtonsLauncher botn;
	WiiMoteIRLauncher qwerty;
	WiiMoteIRGlanceLauncher irgl;
	WiiBoardLauncher bb;
	KinectSkeletonLauncher ksl;
	KinectUserJointReachPointLauncher kujrpl;
	KinectAbsoluteSpaceForATimeLauncher kasfatl;
	KinectTotalSpaceTravelledForATimeLauncher ktstfatl;

	
	
	public static void main (String args []) throws IOException
	{		
		System.setProperty("bluecove.jsr82.psm_minimum_off", "true");
		try{
			new TestGrande();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	

    public TestGrande() throws Exception
    {    	
    	
    	int theUserIWant=1;
    	int maximumNumberOfKinectUsers =1;
    	
    	//DeviceManager dm = DeviceManager.getDeviceManager("127.0.0.1", 7540,maximumNumberOfKinectUsers);
    	DeviceManager dm = DeviceManager.getDeviceManager("193.156.105.166", 7540,maximumNumberOfKinectUsers);
    	
    	
    	//dm.adjustKinectForTheBestTilt(theUserIWant);
    
    	/*
    	
    	KinectPoseLauncher kplWalkLeftLegUp;
    	KinectPoseLauncher kplWalkRightLegUp;
    	KinectPoseLauncher kplStand;
    	
    	kplWalkLeftLegUp= dm.getKinectPoseLauncher(theUserIWant, KinectPoseEnum.WALK_LEFT_LEG_UP);
    	kplWalkRightLegUp= dm.getKinectPoseLauncher(theUserIWant, KinectPoseEnum.WALK_RIGHT_LEG_UP);
    	kplStand= dm.getKinectPoseLauncher(theUserIWant, KinectPoseEnum.STAND);
        
    	ClaseQueImplementaAPose jiji = new ClaseQueImplementaAPose();
    	
    	kplWalkLeftLegUp.addListener(jiji);
    	kplWalkRightLegUp.addListener(jiji);
    	kplStand.addListener(jiji);
    	Thread t1= new Thread(jiji);
    	t1.start();
    	
    	*/
    
    	ksl= dm.getKinectSkeletonLauncher(theUserIWant);
		
		ClaseQueImplementaAKinectSkeleton cqaks= new ClaseQueImplementaAKinectSkeleton();
		
		ksl.addListener(cqaks);
		
	
		
		
		
		/*
		
		IKinectAbsoluteSpaceForATimeService kasfats = new ClaseQueImplementaAAbsoluteSpace();
		dm.kinectAbsoluteSpaceForATime(1, SkeletonJoint.RIGHT_HAND, 3000, kasfats);
		
		*/
		
		
		
		
		/*
		
		kujrpl= dm.getKinectUserJointReachPointLauncher(theUserIWant, SkeletonJoint.RIGHT_HAND, new Point3D(300,300,1000), new Point3D(100,100,1000));
		IKinectUserJointReachPointService cqiar= new ClaseQueImplementaAReached();
		kujrpl.addListener(cqiar);
		*/
		/*
		kasfatl= dm.getKinectAbsoluteSpaceForATimeLauncher(theUserIWant, SkeletonJoint.RIGHT_HAND, 4000);
		IKinectAbsoluteSpaceForATimeService blabla= new ClaseQueImplementaAAbsoluteSpace();
		kasfatl.addListener(blabla);
		while(true){
			
			if(!kasfatl.isRunning()){
				kasfatl.startCounting();
			}else{
				Thread.sleep(kasfatl.getTime()/100);
			}
		}
	*/
		/*
		ktstfatl= dm.getKinectTotalSpaceTravelledForATimeLauncher(theUserIWant, SkeletonJoint.RIGHT_HAND, 4000);
		IKinectTotalSpaceTravelledForATimeService blabla= new ClaseQueImplementaATotalSpace();
		ktstfatl.addListener(blabla);
	*/
		/*while(true){
			
				if(!ktstfatl.isRunning()){
					ktstfatl.startCounting();
				}else{
					Thread.sleep(ktstfatl.getTime()/100);
				}
			
		}
		*/
		
		//dm.kinectAbsoluteSpaceForATime(1, SkeletonJoint.RIGHT_HAND, 4000);
		//dm.kinectTotalSpaceTravelledForATime(1, SkeletonJoint.RIGHT_HAND, 4000);
	
		/*
		System.out.println("Nuestro resultado final es: "+dm.getKinectUsers().get(1).getKinectAbsoluteSpaceForATimeHashMap().get(SkeletonJoint.RIGHT_HAND).getX()+", "+
				dm.getKinectUsers().get(1).getKinectAbsoluteSpaceForATimeHashMap().get(SkeletonJoint.RIGHT_HAND).getY()+ ", "+dm.getKinectUsers().get(1).getKinectAbsoluteSpaceForATimeHashMap().get(SkeletonJoint.RIGHT_HAND).getZ());
*/
    	/*System.out.println("Nuestro resultado final es: "+dm.getKinectUsers().get(1).getKinectTotalSpaceTravelledForATimeHashMap().get(SkeletonJoint.RIGHT_HAND).getX()+", "+
				dm.getKinectUsers().get(1).getKinectTotalSpaceTravelledForATimeHashMap().get(SkeletonJoint.RIGHT_HAND).getY()+ ", "+dm.getKinectUsers().get(1).getKinectTotalSpaceTravelledForATimeHashMap().get(SkeletonJoint.RIGHT_HAND).getZ());	
    	 */
				
    }

	
}


