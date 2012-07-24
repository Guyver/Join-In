package joinIn;

import services.KinectSkeletonServiceEvent;
import iservices.IKinectSkeletonService;



public class SkeletonJointHandler implements IKinectSkeletonService{


	
	KinectSkeletonServiceEvent lastKinectSkeletonServiceEvent;

	
	@Override
	public void kinectUpdate(KinectSkeletonServiceEvent se) {
		lastKinectSkeletonServiceEvent= se;		
		
	}


}
