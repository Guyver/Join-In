/**
 * Copyright 2012 Santiago Hors Fraile
 */
package iservices;


import services.KinectPoseServiceEvent;
import control.IListenerCommModule;

public interface IKinectPoseService extends IListenerCommModule {
	/**
	 * Must be implemented to manage the KinectPoseServiceEvent received.
	 * @param se This is the KinectMotorServiceEvent received.
	 */
	public void kinectPoseUpdate(KinectPoseServiceEvent se);
}
