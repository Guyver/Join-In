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

import org.wiigee.device.Wiimote;

import control.IEventCommModule;

/**
 * Defines the Nunchuck acceleration events.
 * @author Santiago Hors Fraile
 */
public class NunchukAccelerationServiceEvent implements IEventCommModule{
	
	/**
	 * Represents the x value obtained by the Nunchuk accelerometer.
	 */
	private int x;
	/**
	 * Represents the y value obtained by the Nunchuk accelerometer.
	 */
	private int y;
	/**
	 * Represents the z value obtained by the Nunchuk accelerometer.
	 */
	private int z;
	/**
	 * Represents the Wiimote object to whom the Nunchuk is attached.
	 */
	private Wiimote source;
	
	/**
	 * Sets the fields of this class with the given parameters.
	 * @param source The new Wiimote.
	 * @param x The new x value.
	 * @param y The new y value.
	 * @param z The new z value.
	 */
	public NunchukAccelerationServiceEvent (Wiimote source, int x, int y, int z) {
		this.source = source;
		this.x = x;
		this.y = y;
		this.z = z;
	}

	
	/**
	 * Gets the current x value.
	 * @return int The current x value.
	 */
	public int getX() {
		return x;
	}
	/**
	 * Gets the current y value.
	 * @return int The current y value.
	 */
	public int getY() {
		return y;
	}
	/**
	 * Gets the current z value.
	 * @return int The current z value.
	 */
	public int getZ() {
		return z;
	}
	/**
	 * Gets the current Wiimote object.
	 * @return Wiimote The current Wiimote object.
	 */
	public Wiimote getSource() {
		return source;
	}
}