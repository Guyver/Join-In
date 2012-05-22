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

import edu.unsw.cse.wiiboard.event.WiiBoardButtonEvent;

import edu.unsw.cse.wiiboard.event.WiiBoardListener;
import edu.unsw.cse.wiiboard.event.WiiBoardMassEvent;
import edu.unsw.cse.wiiboard.event.WiiBoardStatusEvent;
import services.*;

import control.DeviceManager;
import control.IListenerCommModule;
import control.LauncherWrapper;


public class WiiBoardLauncher extends LauncherWrapper implements WiiBoardListener{


	/**
	 * Adds a listener to the list of listeners of the superclass LauncherWrapper.
	 * @param l The listener that have to be added.
	 * @throws Exception 
	 */
	public void addListener (IWiiBoardService l) throws Exception{
	
			super.addListener(l);

	}
	
	/**
	 * Calls to the superclass LauncherWrapper function dropService and drops one WiiBoard from the DeviceManager.
	 */
	public void dropService(){
		if(!deviceNotNecessaryAnyLonger){
			super.dropService();
			DeviceManager.getDeviceManager().dropWiiBoard(this);
		}
	}

	
	//Specific functions
	/**
	 * Throws the given WiiBoardButtonEvent to all listeners in the listenerList as a WiiBoardServiceButtonEvent.
	 * @param buttonEvent The WiiBoardButtonEvent.
	 */
	public void wiiBoardButtonEvent(WiiBoardButtonEvent buttonEvent) {
		WiiBoardServiceButtonEvent se = new WiiBoardServiceButtonEvent(buttonEvent);
		Iterator<IListenerCommModule> it = listenersList.iterator();
		while(it.hasNext()){
			IWiiBoardService l = (IWiiBoardService)it.next();
			l.wiiBoardButtonEvent(se);	
		}		
	}

	/**
	 * Calls the wiiBoardDisconnected function in all listeners contained in the listenerList.
	 */
	public void wiiBoardDisconnected() {
		Iterator<IListenerCommModule> it = listenersList.iterator();
		while(it.hasNext()){
			IWiiBoardService l = (IWiiBoardService)it.next();
			l.wiiBoardDisconnected();		
		}		
	}

	/**
	 * Throws the given WiiBoardMassEvent to all listeners in the listenerList as a WiiBoardServiceMassEvent.
	 * @param massEvent The WiiBoardMassEvent.
	 */
	public void wiiBoardMassReceived(WiiBoardMassEvent massEvent) {		
		WiiBoardServiceMassEvent se = new WiiBoardServiceMassEvent(massEvent);
		Iterator<IListenerCommModule> it = listenersList.iterator();
		while(it.hasNext()){
			IWiiBoardService l = (IWiiBoardService)it.next();
			l.wiiBoardUpdate(se);		
		}		
	}

	/**
	 * Throws the given WiiBoardStatusEvent to all listeners in the listenerList as a WiiBoardServiceStatusEvent.
	 * @param statusEvent The WiiBoardStatusEvent.
	 */
	public void wiiBoardStatusReceived(WiiBoardStatusEvent statusEvent) {		
		WiiBoardServiceStatusEvent se = new WiiBoardServiceStatusEvent(statusEvent);		
		Iterator<IListenerCommModule> it = listenersList.iterator();
		while(it.hasNext()){
			IWiiBoardService l = (IWiiBoardService)it.next();
			l.wiiBoardStatusEvent(se);	
		}			
	}

	


}
