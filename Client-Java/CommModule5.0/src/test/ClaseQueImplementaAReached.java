package test;

import services.KinectUserJointReachPointServiceEvent;
import iservices.IKinectUserJointReachPointService;

public class ClaseQueImplementaAReached implements IKinectUserJointReachPointService{

	@Override
	public void kinectUpdate(KinectUserJointReachPointServiceEvent se) {
		System.out.println("El usuario "+se.getUserId() +" ha alcanzado el punto "+se.getTriggeringPoint().getX()+" con la extremidad "+se.getJoint());
		
	}

}
