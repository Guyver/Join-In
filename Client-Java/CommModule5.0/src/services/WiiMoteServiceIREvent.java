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

import org.wiigee.event.InfraredEvent;

import control.IEventCommModule;

/**
 * Defines the WiiMote IR events.
 * @author Santiago Hors Fraile
 */
public class WiiMoteServiceIREvent implements IEventCommModule{
	
	/**
	 * Represents the coordinates of the IR spot in the WiiMote camera.
	 */
	protected int[][] coordinates;
	/**
	 * Each cell represents the intensity (size) of the IR spot. 
	 */
	protected int[] size;
	/**
	 * Each cell represents if the IR spot is valid or not. It is valid if the spot coordinates are between 0 and 1023 for x and y.
	 */
	protected boolean[] valid;
	
	/**
	 * Sets all fields of this class with the information contained in the given parameter.
	 * @param event The new IR event.
	 */
	public WiiMoteServiceIREvent(InfraredEvent event) {
	
		this.coordinates=event.getCoordinates();
		this.size=event.getSize();
		this.valid = new boolean[4];
		for(int i=0; i<this.coordinates.length; i++) {
			this.valid[i] = (this.coordinates[i][0]<1023 && this.coordinates[i][1]<1023);
		}
	}
	/**
	 * Gets the current field valid.
	 * @return boolean[] The current valid object.
	 */
    public boolean[] getValids() {
        return this.valid;
    }
	/**
	 * Gets a single valid value from field valid.
	 * @param i The selected position of the list.
	 * @return The current valid value that is in the i position.
	 */
	public boolean isValid(int i) {
		return this.valid[i];
	}
	
	/**
	 * Gets the current field coordinates.
	 * @return int[][] The current coordinates object.
	 */
	public int[][] getCoordinates() {
		return this.coordinates;
	}
	/**
	 * Gets the current field size
	 * @return int[] The current size object.
	 */
	public int[] getSize() {
		return this.size;
	}


}
