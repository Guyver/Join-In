package test;


import services.KinectSkeletonServiceEvent;
import iservices.IKinectSkeletonService;



public class ClaseQueImplementaAKinectSkeleton implements IKinectSkeletonService{


	
	KinectSkeletonServiceEvent lastKinectSkeletonServiceEvent;

	
	@Override
	public void kinectUpdate(KinectSkeletonServiceEvent se) {
		lastKinectSkeletonServiceEvent= se;		
		
	}


}
