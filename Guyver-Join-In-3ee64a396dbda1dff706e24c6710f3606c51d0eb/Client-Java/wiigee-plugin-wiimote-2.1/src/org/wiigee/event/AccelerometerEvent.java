/*
 * Copyright 2007-2008 Volker Fritzsch
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 */
package org.wiigee.event;


/**
 * Defines the AccelerometerEvents.
 * <p>
 * @author <a href="mailto:vfritzsch@users.sourceforge.net">Volker Fritzsch</a>, upgraded by Santiago Hors Fraile
 */
public class AccelerometerEvent<T> {

	/**
	 * Represents the acceleration on the x axis.
	 */
	private int x;
	/**
	 * Represents the acceleration on the y axis.
	 */
	private int y;
	/**
	 * Represents the acceleration on the z axis.
	 */
	private int z;
	/**
	 * Represents the WiiMote or Nunchuk.
	 */
	private T source;
	
	/**
	 * Sets the fields of this class with the given paramters.
	 * @param source The new WiiMote or Nunchuk. 
	 * @param x The new x acceleration value.
	 * @param y The new y acceleration value.
	 * @param z The new z acceleration value.
	 */
	public AccelerometerEvent(T source, int x, int y, int z) {
		this.source = source;
		this.x = x;
		this.y = y;
		this.z = z;
	}

	/**
	 * Gets the field x.
	 * @return int The field x.
	 */
	public int getX() {
		return x;
	}
	/**
	 * Gets the field y.
	 * @return int The field y.
	 */
	public int getY() {
		return y;
	}
	/**
	 * Gets the field z.
	 * @return int The field z.
	 */
	public int getZ() {
		return z;
	}
	/**
	 * Gets the field source.
	 * @return int The field source.
	 */
	public T getSource() {
		return source;
	}

}
