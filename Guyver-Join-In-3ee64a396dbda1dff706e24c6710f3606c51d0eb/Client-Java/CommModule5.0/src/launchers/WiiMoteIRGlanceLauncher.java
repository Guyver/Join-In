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


import iservices.IWiiMoteIRGlanceService;

import java.util.Iterator;


import services.WiiMoteServiceIRGlanceEvent;

import control.DeviceManager;
import control.IListenerCommModule;
import control.LauncherWrapper;
import IRGlancePackage.IRGlanceListener;
import IRGlancePackage.SpeedEvent;

public class WiiMoteIRGlanceLauncher extends LauncherWrapper implements IRGlanceListener{
	
	

	/**
	 * Adds a listener to the list of listeners of the superclass LauncherWrapper.
	 * @param l The listener that have to be added.
	 * @throws Exception 
	 */
	public void addListener (IWiiMoteIRGlanceService l) throws Exception{	
	
			super.addListener(l);
	
	}
	
	/**
	 * Calls to the superclass LauncherWrapper function dropService() and drops one WiiMoted labeled with the given parameter from the DeviceManager.
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
	 * Throws the given SpeedEvent to all listeners in the listenerList as a WiiMoteServiceIRGlanceEvent.
	 * @param event The SpeedEvent.
	 */
	@Override
	public void speedUpdated(SpeedEvent event) {
		WiiMoteServiceIRGlanceEvent se = new WiiMoteServiceIRGlanceEvent(event.source,event.source.getPeriod(),event.speed);	
		Iterator<IListenerCommModule> it = listenersList.iterator();
		while(it.hasNext()){
			IWiiMoteIRGlanceService l = (IWiiMoteIRGlanceService)it.next();
			
			l.iRGlanceReceived(se);	
			
		}			
	}



	
}
