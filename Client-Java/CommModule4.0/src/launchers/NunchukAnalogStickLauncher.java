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

import iservices.INunchukAnalogStickService;

import java.util.Iterator;

import org.wiigee.event.AnalogStickEvent;
import org.wiigee.event.AnalogStickListener;
import org.wiigee.event.DataListener;
import org.wiigee.event.ExtensionEvent;
import org.wiigee.event.ExtensionListener;

import services.NunchukAnalogStickServiceEvent;

import control.DeviceManager;
import control.IListenerCommModule;
import control.LauncherWrapper;

public class NunchukAnalogStickLauncher extends LauncherWrapper implements AnalogStickListener{


	/**
	 * Adds a listener to the list of listeners of the superclass LauncherWrapper.
	 * @param l The listener that have to be added.
	 * @throws Exception 
	 */
	public void addListener (INunchukAnalogStickService l) throws Exception{	
	
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
	 * Throws the given AnalogStickEvent to all listeners in the listenerList as a NunchukAnalogStickServiceEvent.
	 * @param event The AnalogStickEvent.
	 */
	@Override
	public void analogStickChanged(AnalogStickEvent event) {			
	
		NunchukAnalogStickServiceEvent se = new NunchukAnalogStickServiceEvent(	event.getSource().getMote(), event.getPoint());	
		Iterator<IListenerCommModule> it = listenersList.iterator();
		while(it.hasNext()){
			INunchukAnalogStickService l = (INunchukAnalogStickService)it.next();
			l.analogStickEventReceived(se);		
		}			
	}

	/**
	 * Not implemented because it does not belong to the throwing analog stick event logic.
	 */
	@Override
	public void add(Class<DataListener> arg0, DataListener arg1) {
		
	}
	/**
	 * Not implemented because it does not belong to the throwing analog stick event logic.
	 */
	@Override
	public void extensionConnected(ExtensionEvent arg0) {
		
	}
	/**
	 * Not implemented because it does not belong to the throwing analog stick event logic.
	 */
	@Override
	public void extensionDisconnected(ExtensionEvent arg0) {
		
	}
	/**
	 * Not implemented because it does not belong to the throwing analog stick event logic.
	 */
	@Override
	public ExtensionListener[] getListeners(Class<ExtensionListener> arg0) {
		
		return null;
	}
	/**
	 * Not implemented because it does not belong to the throwing analog stick event logic.
	 */
	@Override
	public void remove(Class<ExtensionListener> arg0, ExtensionListener arg1) {
		
	}

}
