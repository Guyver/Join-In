package test;

import services.KinectAbsoluteSpaceForATimeServiceEvent;
import iservices.IKinectAbsoluteSpaceForATimeService;

public class ClaseQueImplementaAAbsoluteSpace implements IKinectAbsoluteSpaceForATimeService{

	@Override
	public void kinectUpdate(KinectAbsoluteSpaceForATimeServiceEvent se) {
		System.out.println("La distancia en X total es: "+se.getSpace().getX());
		
	}



}
