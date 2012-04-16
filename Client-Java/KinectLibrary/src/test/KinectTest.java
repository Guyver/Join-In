package test;


import KinectPackage.IKinectListener;
import KinectPackage.KinectEvent;
import KinectPackage.KinectManager;




public class KinectTest implements IKinectListener
{
 

	public static void main (String [] args) throws Exception {
		KinectTest obj = new KinectTest();
		obj.testing();
	

	}
	
	public void testing(){
		KinectTest tes = new KinectTest();
		KinectManager km = new KinectManager();
		km.addListener(tes);
	
		try {
			km.connect();
		} catch (Exception e) {
		
			e.printStackTrace();
		}
		
	}

@Override
public void kinectUpdate(KinectEvent ke) {
	


	
	}

} 
