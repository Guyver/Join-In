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

import iservices.*;

import java.util.Iterator;


import kinectThreads.KinectEnhacedSkeletonLauncher;

import KinectPackage.IKinectDataListener;
import KinectPackage.KinectDataEvent;



import services.*;

import control.DeviceManager;
import control.IListenerCommModule;
import control.LauncherWrapper;

/**
 * A Kinect event thrower facade. 
 * @author Santiago Hors Fraile
 */
public class KinectSkeletonLauncher extends LauncherWrapper implements IKinectDataListener{
	/**
	 * The thread that is going to be created to send the user's skeleton date to through the socket.
	 */
	private Thread t1; 
	/**
	 * This object privides more functionalities to the Launcher, including the Thread-running capability to send the data to the server.
	 */
	private KinectEnhacedSkeletonLauncher kinectEnhacedSkeletonLauncher;
	/**
	 * The user's label we want to track his/her skeleton to. 
	 */
	private int userId;
	
	/**
	 * Adds a listener to the list of listeners of the superclass LauncherWrapper.
	 * @param l The listener that have to be added.
	 * @throws Exception 
	 */
	public void addListener (IKinectSkeletonService l) throws Exception{

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

	//Specific functions
	/**
	 * Throws the given KinectEvent to all listeners in the listenerList as a KinectServiceEvent.
	 * @param ne The KinectEvent.
	 */	

	@Override
	public void kinectUpdate(KinectDataEvent ke) {
		
	
		KinectSkeletonServiceEvent se = new KinectSkeletonServiceEvent(ke.getUserId(), ke.getKinectData());		
		Iterator<IListenerCommModule> it = super.listenersList.iterator();
		while(it.hasNext()){
			IKinectSkeletonService l = (IKinectSkeletonService)it.next();
			l.kinectUpdate(se);
			if(kinectEnhacedSkeletonLauncher!=null){
				kinectEnhacedSkeletonLauncher.setLastKinectSkeletonServiceEvent(se);
			}
		}		
	}
	/**
	 * Default constructor
	 */
	public KinectSkeletonLauncher(){
		setUserId(1);
		kinectEnhacedSkeletonLauncher=null;
		t1=null;
	
		
	}
	/**
	 * Constructor with parameter. We assume the the skeleton launcher does not have to send the SkeletonJointData through the socket.
	 * @param userId The user's label we want to track his/her skeleton to.
	 */
	public KinectSkeletonLauncher(int userId){
		
		
		setUserId(userId);
		
	}
	/**
	 * Constructor with parameter.
	 * @param userId The user's label we want to track his/her skeleton to.
	 * @param sendSkeletonJointDataToSocket A boolean parameter that represents whether the skeleton launcher has to send the SkeletonJointData through the socket or not.
	 */
	public KinectSkeletonLauncher(int userId, boolean sendSkeletonJointDataToSocket){
		
		
		setUserId(userId);
		if(sendSkeletonJointDataToSocket){
			restartSendingThread();
		}
		
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
	 * This function (re)starts the thread which sends the data to the server through the socket.
	 */
	public void restartSendingThread(){
	kinectEnhacedSkeletonLauncher= new KinectEnhacedSkeletonLauncher(userId);
	t1 = new Thread(kinectEnhacedSkeletonLauncher);
	t1.start();
}

	
	
	
}
