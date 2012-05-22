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

import org.wiigee.device.*;

/**
 * 
 * This Event would be generated if a button on a wiimote has been
 * pressed by user. It contains the source (wiimote) and an integer
 * representation of which button has been pressed. Please note that
 * there exist enumeration constants in the class, so you don't
 * have to use this integer values directly.
 * 
 * The fixed number values have been changed in version 1.5.7 from previous versions.
 *
 * @author Benjamin 'BePo' Poppinga, updated by Santiago Hors Fraile
 */
public class ButtonPressedEvent extends ActionStartEvent {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// Fixed number values. The values have changed in version 1.5.7
	public static final int BUTTON_2 = 1;
	public static final int BUTTON_1 = 2;
	public static final int BUTTON_B = 4;
	public static final int BUTTON_A = 8;
	public static final int BUTTON_MINUS = 16;
	public static final int BUTTON_HOME =  32768;
	public static final int BUTTON_LEFT = 256;
	public static final int BUTTON_RIGHT = 512;
	public static final int BUTTON_DOWN = 1024;
	public static final int BUTTON_UP = 2048;
	public static final int BUTTON_PLUS = 4096;
	
	int button;

	/**
	 * Create a WiimoteButtonPressedEvent with the Wiimote source whose
	 * Button has been pressed and the integer representation of the button.
	 * 
	 * @param source The Nunchuk or Wiimote.
	 * @param button The integer representation of the button.
	 */
	public ButtonPressedEvent(Device source, int button) {
		super(source);
		this.button=button;
		
		if(source.getRecognitionButton()==button) {
			this.recognitionbutton=true;
		} else if(source.getTrainButton()==button) {
			this.trainbutton=true;
		} else if(source.getCloseGestureButton()==button) {
			this.closegesturebutton=true;
		}
	}
	/**
	 * Gets the integer representation of the button.
	 * @return int The integer representation of the button.
	 */
	public int getButton() {
		return this.button;
	}
	
}
