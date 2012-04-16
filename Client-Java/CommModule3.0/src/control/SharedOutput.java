package control;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Semaphore;

import javax.vecmath.Vector3d;

import org.OpenNI.SkeletonJoint;

import services.KinectSkeletonServiceEvent;
import test.SocketUtils;

import com.google.gson.Gson;

public class SharedOutput {
	
	static private SharedOutput rc = null;
	//static Semaphore sem; 
	
	static public SharedOutput getSharedOutput() {
		
	        if (rc == null) {
	            rc = new SharedOutput();
	          //  sem = new Semaphore(1,true);
	        }
	        return rc;
     }


	public void performTransference(IEventCommModule se){
		/*try {
			sem.acquire();
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}*/
		SocketUtils su = null;
		
		while (su == null) {
			try {
				
				su = SocketUtils.getSocket("193.156.105.166", 7540);
			} catch (Exception e) {
				su = null;
			}
		}
		try {
	
			
			Gson gson = new Gson();
			String s ="" ;
			if(se instanceof KinectSkeletonServiceEvent){
			
				
				Map<SkeletonJoint, Vector3d> map = new HashMap<SkeletonJoint, Vector3d>();
				map.put(SkeletonJoint.HEAD, ((KinectSkeletonServiceEvent) se).getHead());
				map.put(SkeletonJoint.LEFT_ELBOW, ((KinectSkeletonServiceEvent) se).getLeftElbow());
				map.put(SkeletonJoint.LEFT_FOOT, ((KinectSkeletonServiceEvent) se).getLeftFoot());
				map.put(SkeletonJoint.LEFT_HAND, ((KinectSkeletonServiceEvent) se).getLeftHand());
				map.put(SkeletonJoint.LEFT_HIP, ((KinectSkeletonServiceEvent) se).getLeftHip());
				map.put(SkeletonJoint.LEFT_KNEE, ((KinectSkeletonServiceEvent) se).getLeftKnee());
				map.put(SkeletonJoint.LEFT_SHOULDER, ((KinectSkeletonServiceEvent) se).getLeftShoulder());
				map.put(SkeletonJoint.NECK, ((KinectSkeletonServiceEvent) se).getNeck());
				map.put(SkeletonJoint.RIGHT_ELBOW, ((KinectSkeletonServiceEvent) se).getRightElbow());
				map.put(SkeletonJoint.RIGHT_FOOT, ((KinectSkeletonServiceEvent) se).getRightFoot());
				map.put(SkeletonJoint.RIGHT_HAND, ((KinectSkeletonServiceEvent) se).getRightHand());
				map.put(SkeletonJoint.RIGHT_HIP, ((KinectSkeletonServiceEvent) se).getRightHip());
				map.put(SkeletonJoint.RIGHT_KNEE, ((KinectSkeletonServiceEvent) se).getRightKnee());
				map.put(SkeletonJoint.RIGHT_SHOULDER, ((KinectSkeletonServiceEvent) se).getRightShoulder());
				map.put(SkeletonJoint.TORSO, ((KinectSkeletonServiceEvent) se).getTorso());
				s= gson.toJson(map);
			}
	
			
			s+='\n';
			
			
			
			su.sendMessage(s);
			
			System.out.println("Sending: "+s.toString());
			//Thread.sleep(500);
			//sem.release();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
	
		
		
	}
}