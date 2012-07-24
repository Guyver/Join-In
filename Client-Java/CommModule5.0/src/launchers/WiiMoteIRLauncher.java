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

import org.wiigee.event.InfraredEvent;
import org.wiigee.event.InfraredListener;

import services.*;

import control.DeviceManager;
import control.IListenerCommModule;
import control.LauncherWrapper;

public class WiiMoteIRLauncher extends LauncherWrapper implements InfraredListener{

	/**
	 * Adds a listener to the list of listeners of the superclass LauncherWrapper.
	 * @param l The listener that have to be added.
	 * @throws Exception 
	 */
	public void addListener (IWiiMoteIRService l) throws Exception{	
			super.addListener(l);
	
	}
	
	/**
	 * Calls to the superclass LauncherWrapper function dropService and drops one WiiMoted labeled with the given parameter from the DeviceManager.
	 * @param label The label of the WiiMote whose service is going to be dropped.
	 */
	public void dropService(int label){
		if(!deviceNotNecessaryAnyLonger){
			super.dropService();
			DeviceManager.getDeviceManager().dropWiiMote(label, this);	
		}
	}


	//Specific functions
	/**
	 * Throws the given InfraredEvent to all listeners in the listenerList as a WiiMoteServiceIREvent.
	 * @param event The InfraredEvent.
	 */
	@Override
	public void infraredReceived(InfraredEvent event) {
		WiiMoteServiceIREvent se = new WiiMoteServiceIREvent(event);	
		Iterator<IListenerCommModule> it = listenersList.iterator();
		while(it.hasNext()){
			IWiiMoteIRService l = (IWiiMoteIRService)it.next();
			l.iRReceived(se);		
		}			
		
	}





}
