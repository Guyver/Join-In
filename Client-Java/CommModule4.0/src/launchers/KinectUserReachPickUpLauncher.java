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
public class KinectUserReachPickUpLauncher extends LauncherWrapper {
	


	KinectSkeletonLauncher ksLauncher;
	
	
	public KinectSkeletonLauncher getKinectSkeletonLauncher(){
		return ksLauncher;
	}
	
	
	public KinectUserReachPickUpLauncher(int userId){
		
		DeviceManager dm = DeviceManager.getDeviceManager();  
		ksLauncher = dm.getKinectSkeletonLauncher(userId);
		
	}

	public void addListener (IKinectSkeletonService l) throws Exception{	
		ksLauncher.addListener(l);
	
	}


}
