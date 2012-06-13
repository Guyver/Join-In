/**
 * Copyright 2012 Santiago Hors Fraile,
 * Based on TrackerPanel3D.java by Andrew Davison, October 2011, ad@fivedots.psu.ac.th


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

package KinectPackage;

import org.OpenNI.*;


import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Semaphore;

public class KinectManager implements Runnable {

	
	// OpenNI
	private Context context;
	private UserGenerator userGenerator;
	private SkeletonManager skeletonManager;
	private MotorCommunicator motorCommunicator;
	private DepthGenerator depthGenerator;
	private ImageGenerator imageGenerator;
	/**
	 * This field represents the maximum number of users that can be tracked by the Kinect at the same time.
	 */
	private int  maximumNumberOfKinectUsers;
	/**
	 * This semaphore serializes the KinectEvent firing to the listeners.
	 */
	private Semaphore sem;
	/**
	 * Contains all Kinect listeners.
	 */
	List<IKinectListener> listenersList = new CopyOnWriteArrayList<IKinectListener>();
	/**
	 * Represents whether the Kinect is running or not.
	 */
	private volatile boolean isRunning;

	/**
	 * @return the isRunning
	 */
	public boolean isRunning() {
		return isRunning;
	}

	/**
	 * @param isRunning the isRunning to set
	 */
	public void setRunning(boolean isRunning) {
		this.isRunning = isRunning;
	}

	/**
	 * @return the context
	 */
	public Context getContext() {
		return context;
	}

	/**
	 * @param context the context to set
	 */
	public void setContext(Context context) {
		this.context = context;
	}

	/**
	 * @return the skeletonManager
	 */
	public SkeletonManager getSkeletonManager() {
		return skeletonManager;
	}

	/**
	 * @param skeletonManager the skeletonManager to set
	 */
	public void setSkeletonManager(SkeletonManager skeletonManager) {
		this.skeletonManager = skeletonManager;
	}

	/**
	 * @return the userGenerator
	 */
	public UserGenerator getUserGenerator() {
		return userGenerator;
	}

	/**
	 * @param userGenerator the userGenerator to set
	 */
	public void setUserGenenerator(UserGenerator userGenerator) {
		this.userGenerator = userGenerator;
	}

	/**
	 * @return the motorCommunicator
	 */
	public MotorCommunicator getMotorCommunicator() {
		return motorCommunicator;
	}

	/**
	 * @param motorCommunicator the motorCommunicator to set
	 */
	public void setMotorCommunicator(MotorCommunicator motorCommunicator) {
		this.motorCommunicator = motorCommunicator;
	}

	/**
	 * @return the depthGenerator
	 */
	public DepthGenerator getDepthGenerator() {
		return depthGenerator;
	}

	/**
	 * @param depthGenerator the depthGenerator to set
	 */
	public void setDepthGenerator(DepthGenerator depthGenerator) {
		this.depthGenerator = depthGenerator;
	}

	/**
	 * @return the imageGenerator
	 */
	public ImageGenerator getImageGenerator() {
		return imageGenerator;
	}

	/**
	 * @param imageGenerator the imageGenerator to set
	 */
	public void setImageGenerator(ImageGenerator imageGenerator) {
		this.imageGenerator = imageGenerator;
	}

	/**
	 * Adds a new Listener to the listeners list.
	 * 
	 * @param li
	 *            The new listener to be add.
	 */
	public void addListener(IKinectListener li) {
		this.listenersList.add(li);
	}

	/**
	 * Removes the KinectListener given as parameter.
	 * 
	 * @param li
	 *            The listener to be removed.
	 */
	public void removeListener(IKinectListener li) {
		this.listenersList.remove(li);
	}

	/**
	 * Default constructor. It sets the maximum number of users that can be tracked by the Kinect at the same time to 1.
	 */
	public KinectManager() {
		this.maximumNumberOfKinectUsers=1;
		setMotorCommunicator(new MotorCommunicator());
		
		configOpenNI();
		sem = new Semaphore(1,true);
	}
	/**
	 * This constructor creates a new KinectManager and sets the maximum number of users that can be tracked by the Kinect at the same time with the given parameter.
	 * @param maximumNumberOfKinectUsers The maximum number of users that can be tracked by the Kinect at the same time.
	 */
	public KinectManager(int maximumNumberOfKinectUsers) {
		this.maximumNumberOfKinectUsers=maximumNumberOfKinectUsers;
		setMotorCommunicator(new MotorCommunicator());
		
		configOpenNI();
		sem = new Semaphore(1,true);
	}

	/**
	 * Tries to connect with the Kinect. If it connects to one, the Kinect is
	 * activated.
	 * 
	 * @throws Exception
	 *             In case that it could not connect.
	 */
	public void connect() throws Exception {
		if (context != null) {
			System.out.println("Initializing Kinect");
			activate();

		} else {
			System.out.println("An error arose when initializing the Kinect");
			throw new Exception();
		}

	}

	/**
	 * Creates a new thread of this class.
	 */
	private void activate() {
		(new Thread(this)).start();
	
	}

	// ------------------------OpenNI ---------------

	/**
	 * Sets up all the parameters related to OpenNI.
	 */
	private void configOpenNI()
	// create context, user generator, and skeletons
	{
		try {
			context = new Context();

			// add the NITE Licence
			License licence = new License("PrimeSense",
					"0KOIk2JeIBYClPWVnMoRKn5cdY4=");
			context.addLicense(licence);
			context.setGlobalMirror(true); // set mirror mode

			userGenerator = UserGenerator.create(context);
			skeletonManager = new SkeletonManager(userGenerator, maximumNumberOfKinectUsers);
			depthGenerator = DepthGenerator.create(context);
			imageGenerator = ImageGenerator.create(context);

			context.startGeneratingAll();

		} catch (Exception e) {
			System.out.println(e);
			System.exit(1);
		}
	} // end of configOpenNI()

	

	
	/**
	 * Disconnects the Kinect.
	 */
	public void disconnect() {

		isRunning = false;
		motorCommunicator.close();
		try {
			context.stopGeneratingAll();
			depthGenerator.stopGenerating();
			imageGenerator.stopGenerating();
			skeletonManager.getUserGenerator().stopGenerating();
		} catch (StatusException e) {
			e.printStackTrace();
		}
		context.release();

	}

	/**
	 * Sends a new KinectEvent to the listeners registered in the listenerLists.
	 * 
	 * @param keout
	 *            The new KinectEvent
	 */
	private void fireKinectEvent(KinectEvent ke) {
		try{
			sem.acquire();
			
			Iterator<IKinectListener> it = listenersList.iterator();
	
			while (it.hasNext()) {
	
				IKinectListener kl = ((IKinectListener) it.next());
	
				(new Thread(new EventLauncher(kl, ke))).start();
			}
			sem.release();
		}catch(Exception e){
			
			System.out.println("Error at firing the Kinect Event in KinectManager");
			e.printStackTrace();
		}
	}

	/**
	 * Runs the thread.
	 */
	public void run() {

		isRunning = true;
		while (isRunning) {
			
			try {
				context.waitAnyUpdateAll();
			} catch (StatusException e) {
				System.out.println(e);
				System.exit(1);
			}
			skeletonManager.update(); // get the skeletons manager to carry out
										// the updates
			if(skeletonManager.getSwitchToLEDColor()!=motorCommunicator.getLED()){
				motorCommunicator.setLED(skeletonManager.getSwitchToLEDColor());
			}
			
			for (int i = 0; i < userGenerator.getNumberOfUsers(); i++) {

				KinectEvent ke = new KinectEvent(i, new KinectData(motorCommunicator, skeletonManager));
				fireKinectEvent(ke);
			}
			
			
		
		}
		// close down
		try {
			context.stopGeneratingAll();
		} catch (StatusException e) {
		}
		context.release();
		System.exit(0);
	} // end of run()

	/**
	 * 
	 * @return The resolution of the Kinect along the x axis
	 */

	public int getXResolution() {
		int xResolution = 0;
		try {
			DepthGenerator depthGenerator = DepthGenerator.create(context);
			DepthMetaData dmd = depthGenerator.getMetaData();
			xResolution = dmd.getXRes();
		} catch (GeneralException e) {

			e.printStackTrace();
		}
		return xResolution;

	}

	/**
	 * 
	 * @return The resolution of the Kinect along the Y axis
	 */
	public int getYResolution() {
		int yResolution = 0;
		try {
			DepthGenerator depthGenerator = DepthGenerator.create(context);
			DepthMetaData dmd = depthGenerator.getMetaData();
			yResolution = dmd.getYRes();
		} catch (GeneralException e) {
			e.printStackTrace();
		}
		return yResolution;

	}

	/**
	 * 
	 * @return The resolution of the Kinect along the Z axis
	 */
	public int getZResolution() {
		int zResolution = 0;
		try {
			DepthGenerator depthGenerator = DepthGenerator.create(context);
			DepthMetaData dmd = depthGenerator.getMetaData();
			zResolution = dmd.getZRes();
		} catch (GeneralException e) {
			e.printStackTrace();
		}
		return zResolution;
	}

	/**
	 * @author Santiago Hors Fraile
	 */
	class EventLauncher implements Runnable {
		/**
		 * Represents the interface of the Kinect listener.
		 */
		IKinectListener kl;
		/**
		 * Represents the Kinect event.
		 */
		KinectEvent ke;

		/**
		 * Sets the fields of this inner class with the given parameter.
		 * 
		 * @param nl
		 *            The new IKinectListener.
		 * @param ne
		 *            The new KinectEvent.
		 */
		EventLauncher(IKinectListener kl, KinectEvent ke) {
			this.kl = kl;
			this.ke = ke;
		}

		/**
		 * Calls to noninUpdate while it is running.
		 */
		public void run() {

			kl.kinectUpdate(ke);
		}
	}

}
