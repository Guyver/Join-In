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

import org.wiigee.event.AccelerationEvent;

import control.IEventCommModule;

/**
 * Defines the WiiMote acceleration events.
 * @author Santiago Hors Fraile
 */
public class WiiMoteServiceAccelerationEvent implements IEventCommModule{
	/**
	 * Represents the x, y and z accelerations.
	 */
	public double X, Y, Z;
	/**
	 * Represents the absolute value of the acceleration.
	 */
	public double absvalue;
	
	/**
	 * Sets the fields of this class with the information contained in the given parameter.
	 * @param event The new acceleration event.
	 */
	public WiiMoteServiceAccelerationEvent(AccelerationEvent event) {
		this.X = event.getX();
		this.Y = event.getY();
		this.Z = event.getZ();
		this.absvalue= event.getAbsValue();
	}


}
