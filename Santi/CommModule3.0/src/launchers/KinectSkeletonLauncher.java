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

import KinectPackage.IKinectListener;
import KinectPackage.KinectEvent;


import services.*;

import control.DeviceManager;
import control.IListenerCommModule;
import control.LauncherWrapper;

/**
 * A Kinect event thrower facade. 
 * @author Santiago Hors Fraile
 */
public class KinectSkeletonLauncher extends LauncherWrapper implements IKinectListener{
	

	
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
	public void kinectUpdate(KinectEvent ke) {
		
	
		KinectSkeletonServiceEvent se = new KinectSkeletonServiceEvent(ke.getUserId(), ke.getKinectData());		
		Iterator<IListenerCommModule> it = super.listenersList.iterator();
		while(it.hasNext()){
			IKinectSkeletonService l = (IKinectSkeletonService)it.next();
			l.kinectUpdate(se);
		}		
		
	}
	


	
	
	
}
