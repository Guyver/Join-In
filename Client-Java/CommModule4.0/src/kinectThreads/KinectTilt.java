package kinectThreads;

import org.OpenNI.Point3D;
import org.OpenNI.SkeletonJoint;


import control.DeviceManager;

import KinectPackage.KinectManager;
import KinectPackage.MotorStatus;

public class KinectTilt implements Runnable {

	int userId;
	int kinectRealHight; //-1 use previous titl, 0 low, 1 medium , 2 high
	
	public KinectTilt(int userId, int kinectRealHight){
		this.userId=userId;
		this.kinectRealHight=kinectRealHight;
	}
	public KinectTilt(int userId){
		this.userId=userId;
		this.kinectRealHight=0;
	}
	
	@Override
	public void run() {
		KinectManager kinectManager = DeviceManager.getDeviceManager().getKinectManager();
		

		// For a Y resolution of 480, accuracy won't be higher than 240, so,
		// we initialise it to a high value that
		// we know we will never reach.
		
		//DeviceManager.getDeviceManager().kinectStartUpKinectIfNeeded();
		
		while (kinectManager == null) {
			//Active wait
			kinectManager = DeviceManager.getDeviceManager().getKinectManager();
		}
		if(kinectRealHight==0){
			kinectManager.getMotorCommunicator().setAngle(20);
		}else if(kinectRealHight==1){
			kinectManager.getMotorCommunicator().setAngle(0);
		}else if(kinectRealHight==2){
			kinectManager.getMotorCommunicator().setAngle(-20);
		}
		System.out.println("Espero a que se detecte un usuario...");
		DeviceManager.getDeviceManager().waitForUserIsDetected(userId);
		System.out.println("Detectado un usuario");
		try {
			while (kinectManager.getMotorCommunicator().getStatus().compareTo(MotorStatus.MOVING) == 0) {
			
			}
			System.out.println("Espero a que se registre el un usuario...");
			DeviceManager.getDeviceManager().waitForUserIsCalibrated(userId);
			System.out.println("Registrado el usuario");
			/*while (kinectManager.getMotorCommunicator().getStatus().compareTo(MotorStatus.MOVING) == 0) {
				
			}*/
	
			while((kinectManager.getSkeletonManager().getJoint3D(userId, SkeletonJoint.TORSO)).getY()==0.0){
				
			}
			//Vector3d massCenterVector = kinectManager.getSkeletonManager().getJoint3D(userId, SkeletonJoint.TORSO);
		//	Point3D massCenter= new Point3D((float)massCenterVector.getX(), (float)massCenterVector.getY(), (float)massCenterVector.getZ());
			
				Point3D massCenter = kinectManager
			.getDepthGenerator()
			.convertRealWorldToProjective(
					kinectManager.getUserGenerator().getUserCoM(userId));
			double middlePointY = kinectManager.getYResolution() / 2;

			double verticalDistanceToAdjust = massCenter.getY() - middlePointY;

			// 1 pixel in x or y equals 0.75 mm
			double verticalDistanceToAdjustInCm = Math.abs(verticalDistanceToAdjust) * 0.075;
			System.out.println("EL massCenter Z :" + massCenter.getZ());
			System.out.println(massCenter.getZ() + "/(SQR("
					+ verticalDistanceToAdjustInCm + "*"
					+ verticalDistanceToAdjustInCm + "+" + massCenter.getZ()
					+ "*" + massCenter.getZ() + ")");

			double angleToTilt = Math.asin((verticalDistanceToAdjustInCm)
					/ massCenter.getZ())*180/Math.PI;

			
				System.out.println("El ángulo a mover es " + angleToTilt);
				kinectManager.getMotorCommunicator().setAngle(kinectManager.getMotorCommunicator().getAngle()+(int)angleToTilt);
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		
		
	}



}
