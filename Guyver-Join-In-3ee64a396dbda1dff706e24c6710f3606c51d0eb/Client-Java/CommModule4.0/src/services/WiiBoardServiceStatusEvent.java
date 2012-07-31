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
package services;

import control.IEventCommModule;

import edu.unsw.cse.wiiboard.event.WiiBoardStatusEvent;

/**
 * Defines the WiiBoard status event.
 * @author Santiago Hors Fraile
 */
public class WiiBoardServiceStatusEvent implements IEventCommModule{

	/**
	 * Represents the battery life of the batteries of the WiiBoard
	 */
	public double battery;
	/**
	 * Represents the LED light state of the WiiBoard.
	 */
	public boolean lightState;
	
	/**
	 * Sets the fields of this class with the data contained in the given parameter.
	 * @param status The new status report of the WiiBoard.
	 */
	public WiiBoardServiceStatusEvent(WiiBoardStatusEvent status) {
		this.battery= status.batteryLife();
		this.lightState = status.isLEDon();
		
		
	}

}
