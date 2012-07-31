package test;



import org.OpenNI.SkeletonJoint;

import KinectPackage.IKinectDataListener;
import KinectPackage.IKinectUserOutOfScopeListener;
import KinectPackage.KinectDataEvent;
import KinectPackage.KinectManager;
import KinectPackage.KinectUserOutOfScopeEvent;





public class KinectTest implements IKinectDataListener, IKinectUserOutOfScopeListener
{
 

	KinectManager km;
	public static void main (String [] args) throws Exception {
		
		KinectTest obj = new KinectTest();
		obj.testing();
		

	}
	
	public void testing(){
		
		KinectTest tes = new KinectTest();
		km = new KinectManager(1);
		km.addKinectDataListener(tes);
		
		km.addKinectUserOutOfScopeListener(tes);
		
		try {
			km.connect();
		} catch (Exception e) {
		
			e.printStackTrace();
		}
		
	}

	@Override
	public void kinectUpdate(KinectDataEvent ke) {
		

		
		if(ke.getKinectData().getSkeletonManager().getJoint3D(1, SkeletonJoint.RIGHT_HAND).getX()!=0.0){
			System.out.println(ke.getKinectData().getSkeletonManager().getJoint3D(1, SkeletonJoint.RIGHT_HAND));
		}
	
	}

	@Override
	public void kinectUpdate(KinectUserOutOfScopeEvent kuoose) {
		System.out.println("HAS SALIDO!!!!!!!!! ");
		
	}

} 
