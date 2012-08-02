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


import iservices.IKinectUserOutOfScopeService;

import java.util.Iterator;

import services.KinectUserOutOfScopeServiceEvent;

import KinectPackage.IKinectUserOutOfScopeListener;
import KinectPackage.KinectUserOutOfScopeEvent;




import control.DeviceManager;
import control.IListenerCommModule;
import control.LauncherWrapper;
/**
 * A Kinect event thrower facade. 
 * @author Santiago Hors Fraile
 */
public class KinectUserOutOfScopeLauncher extends LauncherWrapper implements IKinectUserOutOfScopeListener{

	
	/**
	 * Adds a listener to the list of listeners of the superclass LauncherWrapper.
	 * @param l The listener that have to be added.
	 * @throws Exception 
	 */
	public void addListener (IKinectUserOutOfScopeService l) throws Exception{

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
	
	
	@Override
	/**
	 * Throws the given KinectUserOutOfScopeEvent to all listeners in the listenerList as a KinectUserOutOfScopeServiceEvent.
	 * @param ne The KinectUserOutOfScopeEvent.
	 */	
	public void kinectUpdate(KinectUserOutOfScopeEvent kuoose) {
		
		
		
		KinectUserOutOfScopeServiceEvent se = new KinectUserOutOfScopeServiceEvent(kuoose.getUserId());		
		Iterator<IListenerCommModule> it = super.listenersList.iterator();
		while(it.hasNext()){
			IKinectUserOutOfScopeService l = (IKinectUserOutOfScopeService)it.next();
			l.kinectUpdate(se);
		}		
		
	}

	
	
	


	


}
