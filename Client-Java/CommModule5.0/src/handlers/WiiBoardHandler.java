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
package handlers;

import control.SharedSocket;
import services.WiiBoardServiceButtonEvent;
import services.WiiBoardServiceDisconnectionEvent;
import services.WiiBoardServiceMassEvent;
import services.WiiBoardServiceStatusEvent;
import iservices.IWiiBoardService;
/**
 * This class handles the WiiBoard Events
 * @author Santiago Hors Fraile
 *
 */
public class WiiBoardHandler implements IWiiBoardService{

	
	@Override
	/**
	 * Sends the WiiBoardServiceButtonEvent to the socket.
	 */
	public void wiiBoardButtonEvent(WiiBoardServiceButtonEvent wiiBoardServiceButtonEvent) {
		SharedSocket.getSharedSocket().performTransference(wiiBoardServiceButtonEvent);
		
	}


	@Override
	/**
	 * Sends the WiiBoardServiceMassEvent to the socket.
	 */
	public void wiiBoardMassEvent(WiiBoardServiceMassEvent wiiBoardServiceMassEvent) {
		SharedSocket.getSharedSocket().performTransference(wiiBoardServiceMassEvent);
		
	}

	@Override
	/**
	 * Sends the WiiBoardServiceStatusEvent to the socket.
	 */
	public void wiiBoardStatusEvent(WiiBoardServiceStatusEvent wiiBoardServiceStatusEvent) {
		SharedSocket.getSharedSocket().performTransference(wiiBoardServiceStatusEvent);
		
	}

	@Override
	/**
	 * Sends the WiiBoardServiceMassEvent to the socket.
	 */
	public void wiiBoardUpdate(WiiBoardServiceMassEvent wiiBoardServiceMassEvent) {
		SharedSocket.getSharedSocket().performTransference(wiiBoardServiceMassEvent);
		
	}


	@Override
	/**
	 * Sends the WiiBoardServiceDisconnectionEvent to the socket.
	 */
	public void wiiBoardDisconnectionEvent(WiiBoardServiceDisconnectionEvent wiiBoardServiceDisconnectionEvent) {
		SharedSocket.getSharedSocket().performTransference(wiiBoardServiceDisconnectionEvent);		
	}

}
