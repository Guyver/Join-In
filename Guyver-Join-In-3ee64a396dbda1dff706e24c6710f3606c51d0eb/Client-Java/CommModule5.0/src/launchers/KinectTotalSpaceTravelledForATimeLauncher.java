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

package launchers;


import iservices.IKinectTotalSpaceTravelledForATimeService;

import java.util.Iterator;



import org.OpenNI.SkeletonJoint;

import services.KinectTotalSpaceTravelledForATimeServiceEvent;

import kinectThreads.IKinectTotalSpaceTravelledForATimeListener;
import kinectThreads.KinectTotalSpaceTravelledForATime;
import kinectThreads.KinectTotalSpaceTravelledForATimeEvent;

import control.DeviceManager;
import control.IListenerCommModule;

import control.LauncherWrapper;
/**
 * A Kinect event thrower facade. 
 * @author Santiago Hors Fraile
 */
public class KinectTotalSpaceTravelledForATimeLauncher extends LauncherWrapper implements IKinectTotalSpaceTravelledForATimeListener{
	
	/**
	 * The user's label ID whose joint we want calculate the space it goes over throughout the given time.
	 */
	private int userId;
	/**
	 * The user's joint we want to calculate the space it goes over throughout the given time.
	 */
	private SkeletonJoint joint;
	/**
	 * The time throughout we are going to track the user's joint.
	 */
	private long time;
	/**
	 * This attribute represents whether the thread is running or not.
	 */
	private boolean running;
	/**
	 * Creates a new KinectTotalSpaceTravelledForATimeLauncher and initializes it with the given parameters.
	 * @param userId The user's label ID whose joint we want calculate the space it goes over throughout the given time.
	 * @param joint The user's joint we want to calculate the space it goes over throughout the given time.
	 * @param time The time throughout we are going to track the user's joint.
	 */
	public KinectTotalSpaceTravelledForATimeLauncher(int userId, SkeletonJoint joint, long time) {
		this.userId=userId;
		this.joint=joint;
		this.time=time;
		this.running=false;
	}
	/**
	 * Starts the calculation of the space.
	 */
	public void startCounting(){
		running=true;
		Thread t1 = new Thread(new KinectTotalSpaceTravelledForATime(userId, joint, time,this));
		t1.start();
	
	}
	/**
	 * Starts the calculation of the space, but uses a time different that the one that was given in the constructor.
	 * Attention: this does not change the time attribute!
	 */
	public void startCounting(long overwrittenTime){
		running=true;
		Thread t1 = new Thread(new KinectTotalSpaceTravelledForATime(userId, joint, overwrittenTime, this));
		t1.start();
	
	}
	
	/**
	 * Adds a listener to the list of listeners of the superclass LauncherWrapper.
	 * @param l The listener that have to be added.
	 * @throws Exception 
	 */
	public void addListener (IKinectTotalSpaceTravelledForATimeService l) throws Exception{
		super.addListener(l);

	}
	
	/**
	 * Calls to the superclass LauncherWrapper function dropService and drops the Kinect from the DeviceMnager.
	 */
	@Override
	public void dropService(){
		if(!deviceNotNecessaryAnyLonger){
			super.dropService();
			DeviceManager.getDeviceManager().dropKinect(this);	
		}
	}

	/**
	 * Throws the given KinectTotalSpaceTravelledForATimeEvent to listeners in the listenerList as a KinectTotalSpaceTravelledForATimeServiceEvent.
	 * @param event The KinectTotalSpaceTravelledForATimeEvent.
	 */
	@Override
	public void kinectTotalSpaceTravelledForATimeUpdate(KinectTotalSpaceTravelledForATimeEvent ke) {
		KinectTotalSpaceTravelledForATimeServiceEvent se = new KinectTotalSpaceTravelledForATimeServiceEvent(ke.getUserId(),ke.getJoint(),ke.getSpace(), time);		
	
		Iterator<IListenerCommModule> it = super.listenersList.iterator();
		while(it.hasNext()){
			IKinectTotalSpaceTravelledForATimeService l = (IKinectTotalSpaceTravelledForATimeService)it.next();
			l.kinectUpdate(se);
		}		
		
	}
	/**
	 * @return the running
	 */
	public boolean isRunning() {
		return running;
	}

	/**
	 * @param running the running to set
	 */
	public void setRunning(boolean running) {
		this.running = running;
	}
	
	/**
	 * @return the userId
	 */
	public int getUserId() {
		return userId;
	}

	/**
	 * @param userId the userId to set
	 */
	public void setUserId(int userId) {
		this.userId = userId;
	}

	/**
	 * @return the joint
	 */
	public SkeletonJoint getJoint() {
		return joint;
	}

	/**
	 * @param joint the joint to set
	 */
	public void setJoint(SkeletonJoint joint) {
		this.joint = joint;
	}

	/**
	 * @return the time
	 */
	public long getTime() {
		return time;
	}

	/**
	 * @param time the time to set
	 */
	public void setTime(long time) {
		this.time = time;
	}
}
