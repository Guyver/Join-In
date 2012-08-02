/**
 * Copyright 2012 Santiago Hors Fraile

  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
 */


package kinectThreads;


import iservices.IKinectSkeletonService;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;


import services.KinectSkeletonServiceEvent;



/**
 * This class re-launches the KinectSkeleton events so that they can be received and processed as KinectPoses.
 * @author Santiago Hors Fraile
 *
 */
public class KinectPoseManager implements IKinectSkeletonService{
	
	/**
	 * Contains all KinectPose listeners.
	 */
	List<IKinectPoseListener> listenersList = new CopyOnWriteArrayList<IKinectPoseListener>();
	
	
	/**
	 * Adds a new Listener to the listeners list.
	 * 
	 * @param li
	 *            The new listener to be add.
	 */
	public void addListener(IKinectPoseListener li) {
		this.listenersList.add(li);

	}

	/**
	 * Removes the KinectPoseListener given as parameter.
	 * 
	 * @param li
	 *            The listener to be removed.
	 */
	public void removeListener(IKinectPoseListener li) {
		this.listenersList.remove(li);
	}

	/**
	 * Sends a new KinectPoseEvent to the listeners registered in the listenerLists
	 * 
	 * @param ke
	 *            The new KinectEvent
	 */
	private void fireKinectSkeletonEvent(KinectSkeletonServiceEvent ke) {
		Iterator<IKinectPoseListener> it = listenersList.iterator();

		while (it.hasNext()) {

			IKinectPoseListener kl = it.next();

			(new Thread(new EventLauncher(kl, ke))).start();
		}

	}

	/**
	 * @author Santiago Hors Fraile
	 */
	class EventLauncher implements Runnable {
		/**
		 * Represents the interface of the Kinect listener.
		 */
		IKinectPoseListener kl;
		/**
		 * Represents the Kinect event.
		 */
		KinectSkeletonServiceEvent ke;

		/**
		 * Sets the fields of this inner class with the given parameter.
		 * 
		 * @param nl
		 *            The new IKinectListener.
		 * @param ne
		 *            The new KinectEvent.
		 */
		EventLauncher(IKinectPoseListener kpl, KinectSkeletonServiceEvent kpe) {
			this.kl = kpl;
			this.ke = kpe;
		}

		/**
		 * Calls to noninUpdate while it is running.
		 */
		@Override
		public void run() {
		
			kl.kinectUpdate(ke);
		}
	}

	/**
	 * This function fires KinectSkeletonEvents to the listeners that has been registered in this class when a new KinectSkeletonServiceEvent is thrown by the SkeletonLauncher this class is registered as listener to.
	 */
	@Override
	public void kinectUpdate(KinectSkeletonServiceEvent se) {
		fireKinectSkeletonEvent(se);
		
	}

	



	
}
