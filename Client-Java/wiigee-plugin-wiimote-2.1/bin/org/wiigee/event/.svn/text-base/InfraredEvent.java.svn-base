/*
 * wiigee - accelerometerbased gesture recognition
 * Copyright (C) 2007, 2008, 2009 Benjamin Poppinga
 * 
 * Developed at University of Oldenburg
 * Contact: wiigee@benjaminpoppinga.de
 *
 * This file is part of wiigee.
 *
 * wiigee is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package org.wiigee.event;

import java.util.EventObject;
import org.wiigee.device.Device;

/**
 * Defines the IR events. 
 * 
 * An infrared event consists of a set of coordinates, containing values
 * from [0, 1024] in width to [0, 768] in height. for each point there is
 * a given size and if the detected infrared spot is valid.
 *
 * @author Benjamin 'BePo' Poppinga, upgraded by Santiago Hors Fraile
 */
public class InfraredEvent extends EventObject {

	private static final long serialVersionUID = 1L;
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
	 * Sets all fields of this class with the information contained in the given parameters.
	 * @param source The new WiiMote.
	 * @param coordinates The new coordinates.
	 * @param size The new size.
	 */
	public InfraredEvent(Device source, int[][] coordinates, int[] size) {
		super(source);
		this.coordinates=coordinates;
		this.size=size;
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
