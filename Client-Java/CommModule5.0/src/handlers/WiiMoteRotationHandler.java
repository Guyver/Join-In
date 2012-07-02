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

import services.WiiMoteServiceRotationEvent;
import iservices.IWiiMoteRotationService;
import control.SharedSocket;
/**
 * This class handles the WiiMote rotation Events
 * @author Santiago Hors Fraile
 *
 */
public class WiiMoteRotationHandler implements IWiiMoteRotationService{

	@Override
	/**
	 * Sends the WiiMoteServiceRotationEvent to the socket.
	 */
	public void rotationReceived(WiiMoteServiceRotationEvent wiiMoteServiceRotationEvent) {
		SharedSocket.getSharedSocket().performTransference(wiiMoteServiceRotationEvent);

		
	}
}
