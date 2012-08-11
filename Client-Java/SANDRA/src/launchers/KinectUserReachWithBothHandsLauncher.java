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

import iservices.IKinectSkeletonService;
import control.DeviceManager;
import control.LauncherWrapper;
/**
 * 
 * @author Santiago Hors Fraile
 */
public class KinectUserReachWithBothHandsLauncher extends LauncherWrapper {
	

	
	/**
	 * 
	 * @return the skeleton launcher that we use
	 */
	public KinectSkeletonLauncher getKinectSkeletonLauncher(){
		return DeviceManager.getDeviceManager().getCreatedKinectSkeletonLauncher();
	}	
	
	/**
	 * Default constructor
	 */
	public KinectUserReachWithBothHandsLauncher(){  
		DeviceManager.getDeviceManager().getKinectSkeletonLauncher();
	}
	
	/**
	 * Adds a listener to the list of listeners of the superclass LauncherWrapper.
	 * @param l The listener that have to be added.
	 * @throws Exception 
	 */
	public void addListener (IKinectSkeletonService l) throws Exception{	
		DeviceManager.getDeviceManager().getCreatedKinectSkeletonLauncher().addListener(l);
	
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

}