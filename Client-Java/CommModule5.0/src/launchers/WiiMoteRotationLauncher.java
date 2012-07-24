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


import org.wiigee.event.RotationEvent;
import org.wiigee.event.RotationListener;
import org.wiigee.event.RotationSpeedEvent;

import services.*;

import control.DeviceManager;
import control.IListenerCommModule;
import control.LauncherWrapper;

public class WiiMoteRotationLauncher extends LauncherWrapper implements RotationListener {

	/**
	 * Adds a listener to the list of listeners of the superclass LauncherWrapper.
	 * @param l The listener that have to be added.
	 * @throws Exception 
	 */
	public void addListener (IWiiMoteRotationService  l) throws Exception{	
	
			super.addListener(l);

	}
	
	/**
	 * Calls to the superclass LauncherWrapper function dropService and drops one WiiMoted labeled with the given parameter from the DeviceManager.
	 * It also sets the WiiMotionPlus of that WiiMote to false.
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
	 * Throws the given RotationEvent to all listeners from listenersList as a WiiMoteServiceRotationEvent.
	 * @param arg0 The RotationEvent.
	 */
	@Override
	public void rotationReceived(RotationEvent arg0) {
		
		
		WiiMoteServiceRotationEvent se = new WiiMoteServiceRotationEvent(arg0);	
		Iterator<IListenerCommModule> it = listenersList.iterator();
		
	
			while(it.hasNext()){
				IWiiMoteRotationService l = (IWiiMoteRotationService)it.next();
				l.rotationReceived(se);		
			}			
		
	}
	/**
	 * Throws the given RotationSpeedEvent to all listeners from listenersList as a WiiMoteServiceRotationSpeedEvent.
	 * @param arg0 The RotationSpeedEvent.
	 */
	@Override
	public void rotationSpeedReceived(RotationSpeedEvent arg0) {
		WiiMoteServiceRotationSpeedEvent se = new WiiMoteServiceRotationSpeedEvent(arg0);	
		Iterator<IListenerCommModule> it = listenersList.iterator();
		
	
			while(it.hasNext()){
				IWiiMoteRotationSpeedService l = (IWiiMoteRotationSpeedService)it.next();
				l.rotationSpeedReceived(se);		
			}		
		
		
	}
	/**
	 * Calls the calibrationFinished function in all listeners from listenersList.
	 */
	@Override
	public void calibrationFinished() {
	
		Iterator<IListenerCommModule> it = listenersList.iterator();
	
			while(it.hasNext()){
				ICalibrationFinishedService l = (ICalibrationFinishedService)it.next();
				l.calibrationFinished();		
			}	
		
	}		
	



}
