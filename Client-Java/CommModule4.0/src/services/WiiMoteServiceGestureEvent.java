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


import org.wiigee.event.GestureEvent;
import org.wiigee.logic.ProcessingUnit;

import control.IEventCommModule;

/**
 * Defines the WiiMote gesture event.
 * @author Santiago Hors Fraile.
 */
public class WiiMoteServiceGestureEvent implements IEventCommModule {

	  /**
	   * Represents the gesture identifier.
	   */
	  int id;
	  /**
	   * Represents if the gesture is valid or not.
	   */
	  boolean valid;
	  /**
	   * Represents the probability of that the gesture is the one that it is thought.
	   */
	  double probability;
	  /**
	   * Represents the procesing unit object.
	   */
	  ProcessingUnit analyzer;
	
	
	/**
	 * Sets the fields of this class with the information contained in the given parameter.
	 * @param event The new gesture event.
	 */

	public WiiMoteServiceGestureEvent(GestureEvent event) {
		this.valid = event.isValid();
		this.id = event.getId();
		this.probability = event.getProbability();
		this.analyzer = event.getSource();
	}

}
