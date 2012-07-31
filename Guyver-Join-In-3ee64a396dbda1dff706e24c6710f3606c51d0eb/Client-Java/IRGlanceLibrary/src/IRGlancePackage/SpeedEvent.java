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

package IRGlancePackage;

/**
 * Defines the SpeedEvent
 * @author  Santiago Hors Fraile
 */
public class SpeedEvent {
	
	/**
	 * Represents the rate in which the timer is working. It is measured in milliseconds.
	 */
	public int period;
	/**
	 * Represents the number of changes in the detection of IR spots.
	 */
	public int speed;
	/**
	 * Represents the IRGlance object that launches the event.
	 */
	public IRGlance source;
	
	/**
	 * Sets the field of this class with the given parameters.
	 * @param source The new IRglance object that launches the event.
	 * @param period The new period.
	 * @param speed The new speed.
	 */
	public SpeedEvent (IRGlance source, int period, int speed){
		this.period= period;
		this.speed= speed;
		this.source= source;
	}
	
}
