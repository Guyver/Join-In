/**Copyright 2012 Santiago Hors Fraile
 * *
 */
package iservices;

import services.KinectTotalSpaceTravelledForATimeServiceEvent;
import control.IListenerCommModule;
/**
 * Must be implemented by any class which wants to receive the the total space that a joint has gone over events from a Kinect.
 * @author Santiago Hors Fraile
 */
public interface IKinectTotalSpaceTravelledForATimeService extends IListenerCommModule{
	/**
	 * Must be implemented to manage the KinectAbsoluteSpaceForATimeEvent received.
	 * @param se This is the KinectAbsoluteSpaceForATimeEvent received.
	 */
	public void kinectUpdate(KinectTotalSpaceTravelledForATimeServiceEvent se); 
}
