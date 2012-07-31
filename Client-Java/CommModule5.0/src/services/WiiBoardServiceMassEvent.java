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

import edu.unsw.cse.wiiboard.event.WiiBoardMassEvent;
/**
 * Defines the WiiBoard mass events.
 * @author Santiago Hors Fraile
 */
public class WiiBoardServiceMassEvent implements IEventCommModule {

	/**
	 * Represents the total weight on the WiiBoard
	 */
	public double totalWeight;
	/**
	 * Represents the weight on the top left section of the WiiBoard.
	 */
	public double topLeft; 
	/**
	 * Represents the weight on the top left section of the WiiBoard.
	 */
	public double topRight;
	/**
	 * Represents the weight on the bottom left section of the WiiBoard.
	 */
	public double bottomLeft;
	/**
	 * Represents the weight on the bottom right section of the WiiBoard.
	 */
	public double bottomRight;
	
	/**
	 * Sets the fields of this class with the data contained in the given parameter.
	 * @param massEvent The new mass status report.
	 */
	public WiiBoardServiceMassEvent(WiiBoardMassEvent massEvent) {
		totalWeight = massEvent.getTotalWeight();
		topLeft = massEvent.getTopLeft();
		topRight= massEvent.getTopRight();
		bottomLeft = massEvent.getBottomLeft();
		bottomRight = massEvent.getBottomRight();
	}




}
