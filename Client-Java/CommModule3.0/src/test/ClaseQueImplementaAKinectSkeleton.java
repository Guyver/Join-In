package test;



import java.io.IOException;

import control.DeviceManager;
import control.SharedOutput;

import services.KinectSkeletonServiceEvent;
import iservices.IKinectSkeletonService;



public class ClaseQueImplementaAKinectSkeleton implements IKinectSkeletonService{

	int numberOfPackets;
	long lastTimeISent;
	
	KinectSkeletonServiceEvent lastKinectSkeletonServiceEvent;
	
	public ClaseQueImplementaAKinectSkeleton(){
		lastTimeISent=0;
		numberOfPackets=0;
	} 
	
	
	
	@Override
	public void kinectUpdate(KinectSkeletonServiceEvent se) {
		lastKinectSkeletonServiceEvent= se;
	
		
	/*	long currentTime=System.currentTimeMillis();
		
		if(System.currentTimeMillis()-lastTimeISent>41){
			lastTimeISent=currentTime;
			SharedOutput.getSharedOutput().performTransference(se);
			System.out.println("The above string was the package number: "+numberOfPackets++);
		}*/
		
	}

/*

	@Override
	public void run() {
		SocketUtils su = SocketUtils.getSocket("193.156.105.166", 7540);
		while(true){
			
			try {
				while(true){
				
					String receivedMessage = su.readMessage();
					System.out.println("receivedMessage = " +receivedMessage);
					
					SharedOutput.getSharedOutput().performTransference(lastKinectSkeletonServiceEvent);
					//System.out.println("The above string was the package number: "+numberOfPackets++);
					
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
*/
}
