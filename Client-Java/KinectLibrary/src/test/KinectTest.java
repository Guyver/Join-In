package test;



import org.OpenNI.SkeletonJoint;

import KinectPackage.IKinectListener;
import KinectPackage.KinectEvent;
import KinectPackage.KinectManager;





public class KinectTest implements IKinectListener
{
 

	KinectManager km;
	public static void main (String [] args) throws Exception {
		
		KinectTest obj = new KinectTest();
		obj.testing();
		

	}
	
	public void testing(){
		
		KinectTest tes = new KinectTest();
		km = new KinectManager(1);
		km.addListener(tes);
		
		try {
			km.connect();
		} catch (Exception e) {
		
			e.printStackTrace();
		}
		
	}

	@Override
	public void kinectUpdate(KinectEvent ke) {
		

		
		if(ke.getKinectData().getSkeletonManager().getJoint3D(1, SkeletonJoint.RIGHT_HAND).getX()!=0.0){
			System.out.println(ke.getKinectData().getSkeletonManager().getJoint3D(1, SkeletonJoint.RIGHT_HAND));
		}
	
	}

} 
