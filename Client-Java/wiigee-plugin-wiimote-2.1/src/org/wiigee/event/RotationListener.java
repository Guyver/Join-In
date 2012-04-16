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

import java.util.EventListener;

/**
 * Must be implemented by all classes which want to deal with rotation listeners.
 * Implement this interface to get informed about events only
 * occuring if a Wii Motion Plus is attached. If implemented and added
 * as a Listener, the RotationSpeedEvents and RotationEvent would be
 * retrieved.
 *
 * @author Benjamin 'BePo' Poppinga, upgraded by Santiago Hors Fraile
 */
public interface RotationListener extends EventListener {

	/**
	 * Must be implemented to manage rotation speed events.
	 * @param event The new RotationSpeedEvent.
	 */
    public abstract void rotationSpeedReceived(RotationSpeedEvent event);

    /**
     * Must be implemented to manage rotation evetns.
     * @param event The new RotationEvent.
     */
    public abstract void rotationReceived(RotationEvent event);

    /**
     * Must be implemented to manage the calibrationFinished report.
     */
	public abstract void calibrationFinished();

}