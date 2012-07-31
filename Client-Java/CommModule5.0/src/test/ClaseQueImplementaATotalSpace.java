package test;


import services.KinectTotalSpaceTravelledForATimeServiceEvent;

import iservices.IKinectTotalSpaceTravelledForATimeService;

public class ClaseQueImplementaATotalSpace implements IKinectTotalSpaceTravelledForATimeService{

	

	@Override
	public void kinectUpdate(KinectTotalSpaceTravelledForATimeServiceEvent se) {
	
		System.out.println("La distancia en X total es: "+se.getSpace().getX());
		
	}



}
