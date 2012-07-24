/**Copyright 2012 Santiago Hors Fraile
 * *
 */
package iservices;


import services.KinectUserOutOfScopeServiceEvent;
import control.IListenerCommModule;
/**
 * Must be implemented by any class which wants to receive the motor events from a Kinect
 * @author Santiago Hors Fraile
 */
public interface IKinectUserOutOfScopeService extends IListenerCommModule {
	/**
	 * Must be implemented to manage the KinectMotorServiceEvent received.
	 * @param se This is the KinectUserOutOfScopeServiceEvent received.
	 */
	public void kinectUpdate(KinectUserOutOfScopeServiceEvent se);


	
}
