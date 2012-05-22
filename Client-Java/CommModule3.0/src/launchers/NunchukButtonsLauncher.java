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

import iservices.INunchukButtonsService;

import java.util.Iterator;

import org.wiigee.event.NunchukButtonListener;
import org.wiigee.event.NunchukButtonPressedEvent;
import org.wiigee.event.NunchukButtonReleasedEvent;

import services.NunchukServiceButtonPressedEvent;
import services.NunchukServiceButtonReleasedEvent;

import control.DeviceManager;
import control.IListenerCommModule;
import control.LauncherWrapper;

public class NunchukButtonsLauncher extends LauncherWrapper implements NunchukButtonListener{

	
	/**
	 * Adds a listener to the list of listeners of the superclass LauncherWrapper.
	 * @param l The listener that have to be added.
	 * @throws Exception 
	 */
	public void addListener (INunchukButtonsService l) throws Exception{	

			super.addListener(l);
	
		
	}
	/**
	 * Calls to the superclass LauncherWrapper function dropService and drops one WiiMoted labeled with the given parameter from the DeviceManager.
	 * It also sets the Nunchuk of that WiiMote to false.
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
	 * Throws the given NunchukButtonPressedEvent to all listeners in the listenerList as a NunchukServiceButtonPressedServiceEvent.
	 * @param event The NunchukButtonPressedEvent.
	 */
	@Override
	public void buttonPressedReceived(NunchukButtonPressedEvent event) {
		NunchukServiceButtonPressedEvent se = new NunchukServiceButtonPressedEvent(event);	
		Iterator<IListenerCommModule> it = listenersList.iterator();
		while(it.hasNext()){
			INunchukButtonsService l = (INunchukButtonsService)it.next();
			l.nunchukButtonPressedEventReceived(se);		
		}	 				
	}
	/**
	 * Throws the given NunchukButtonReleasedEvent to listeners in the listenerList as a NunchukServiceButtonReleasedServiceEvent.
	 * @param event The NunchukButtonReleasedEvent.
	 */
	@Override
	public void buttonReleasedReceived(NunchukButtonReleasedEvent event) {
		NunchukServiceButtonReleasedEvent se = new NunchukServiceButtonReleasedEvent(event);	
		Iterator<IListenerCommModule> it = listenersList.iterator();
		while(it.hasNext()){
			INunchukButtonsService l = (INunchukButtonsService)it.next();
			l.nunchukButtonReleasedEventReceived(se);		
		}	 						
	}
}
