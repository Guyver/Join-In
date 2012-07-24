/**
 * 
 */
package test;

import org.wiigee.event.GestureEvent;
import org.wiigee.event.GestureListener;

/**
 * @author santiago
 *
 */
public class MiGestureListener implements GestureListener{

	/* (non-Javadoc)
	 * @see org.wiigee.event.GestureListener#gestureReceived(org.wiigee.event.GestureEvent)
	 */
	@Override
	public void gestureReceived(GestureEvent arg0) {
		arg0.getProbability();
		arg0.getId();
	
	}

}
