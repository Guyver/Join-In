/**
 * Copyright 2010 Santiago Hors Fraile and Salvador Jesús Romero

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


import NoninPackage.INoninListener;
import NoninPackage.NoninEvent;
import services.*;

import control.DeviceManager;
import control.IListenerCommModule;
import control.LauncherWrapper;

/**
 * A Nonin event thrower facade. 
 * @author Santiago Hors Fraile
 */
public class NoninLauncher extends LauncherWrapper implements INoninListener{
	

	/**
	 * Adds a listener to the list of listeners of the superclass LauncherWrapper.
	 * @param l The listener that have to be added.
	 * @throws Exception 
	 */
	public void addListener (INoninService l) throws Exception{

		super.addListener(l);

	}
	
	/**
	 * Calls to the superclass LauncherWrapper function dropService and drops one Nonin from the DeviceMnager.
	 */
	@Override
	public void dropService(){
		if(!deviceNotNecessaryAnyLonger){
			super.dropService();
			DeviceManager.getDeviceManager().dropNonin(this);	
		}
	}

	//Specific functions
	/**
	 * Throws the given NoninEvent to all listeners in the listenerList as a NoninServiceEvent.
	 * @param ne The NoninEvent.
	 */
	@Override
	public void noninUpdate(NoninEvent ne) {	
		
		NoninServiceEvent se = new NoninServiceEvent(ne.pulse, ne.oxy, ne.data);		
		Iterator<IListenerCommModule> it = super.listenersList.iterator();
		while(it.hasNext()){
			INoninService l = (INoninService)it.next();
			l.noninUpdate(se);
		}		
	}
	


	
	
	
}
