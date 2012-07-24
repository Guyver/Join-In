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

import IRGlancePackage.IRGlance;

/**
 * Defines the WiiMote IRGlance events
 * @author Santiago Hors Fraile
 */
public class WiiMoteServiceIRGlanceEvent implements IEventCommModule{
	/**
	 * Represents the rate in which the timer is working. It is measured in miliseconds.
	 * 
	 */
	public int period;
	/**
	 * Represents the number of changes in the detection of IR spots during one period.
	 */
	public int speed;
	/**
	 * Represents the Wiimote object.
	 */
	public IRGlance source;
	/**
	 * Sets the fields of this class with the given parameters.
	 * @param source The new Wiimote.
	 * @param period The new period.
	 * @param speed The new speed.
	 */ 
	public WiiMoteServiceIRGlanceEvent (IRGlance source, int period, int speed){
		this.period= period;
		this.speed= speed;
		this.source= source;
	}


	
}
