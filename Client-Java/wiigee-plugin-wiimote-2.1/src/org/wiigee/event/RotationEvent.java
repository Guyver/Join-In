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
import org.wiigee.device.Wiimote;

/**
 * A RotationEvents contains the current relative rotation to the last
 * given reset position. If the device has never been resetted before,
 * the last position is the Wiimotes initial position. This event contains
 * all three angles - pitch, yaw, roll - which are only determined using
 * the Wii Motion Plus extension. There wouldn't be a RotationEvent without
 * this extension.
 *
 * @author Benjamin 'BePo' Poppinga, upgraded by Santiago Hors Fraile
 */
public class RotationEvent extends EventObject {

	private static final long serialVersionUID = 1L;

	/**
	 * Represents the pitch of the WiiMotionPlus.
	 */
    protected double pitch;

	/**
	 * Represents the yaw of the WiiMotionPlus.
	 */
	protected double yaw;

	/**
	 * Represents the roll of the WiiMotionPlus.
	 */
    protected double roll;
    /**
     * Represents the wiimote.
     */
    protected Wiimote wiimote;
    
    /**
     * Sets the fields of this class with the given parameters.
     * @param source The new WiiMote to whom the WiiMotionPlus is attached.
     * @param pitch The new pitch.
     * @param roll The new roll.
     * @param yaw The new yaw.
     */
	public RotationEvent(Wiimote source, double pitch, double roll, double yaw) {
		super(source);
		
        this.pitch = pitch;
        this.roll = roll;
        this.yaw = yaw;
        this.wiimote= source;
	}

	/**
	 * Gets the current field pitch.
	 * @return double The current pitch.
	 */
	public double getPitch() {
        return this.pitch;
    }
	/**
	 * Gets the current field yaw.
	 * @return double The current yaw.
	 */
    public double getYaw() {
        return this.yaw;
    }
	/**
	 * Gets the current field roll.
	 * @return double The current roll.
	 */
    public double getRoll() {
        return this.roll;
    }
    /**
	 * Gets the current field wiimote.
	 * @return double The current wiimote.
	 */
    public Wiimote getWiimote(){
    	return this.wiimote;
    	
    }
}
