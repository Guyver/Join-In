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

import java.awt.Point;

import org.wiigee.device.Nunchuk;

/**
 * Defines the analog stick event.
 * <p>
 * @author <a href="mailto:vfritzsch@users.sourceforge.net">Volker Fritzsch</a>, upgraded by Santiago Hors Fraile
 */
public class AnalogStickEvent {

	/**
	 * Represents the point in which the analog stick is.
	 */
	protected Point point;
	
	/**
	 * Represents the Nunchuk.
	 */
	protected Nunchuk source;
	
	/**
	 * Represents the angle of the analog stick.
	 */
	protected double angle;
	
	/**
	 * Represents the tilt of the analog stick.
	 */
	protected double tilt;
	
	/**
	 * Initializes the fields of this class with the given parameters.
	 * @param source The new Nunchuk.
	 * @param point The new point in which the analog stick is.
	 */
	public AnalogStickEvent(Nunchuk source, Point point) {
		this.source = source;
		this.point = new Point(point.x-125, point.y-130);
		
	}
	/**
	 * Gets the field source.
	 * @return Nunchuk The current Nunchuk.
	 */
	public Nunchuk getSource() {
		return source;
	}
	/**
	 * Gets the field point.
	 * @return Point The current point.
	 */
	public Point getPoint() {
		return point;
	}
	/**
	 * Gets the field angle.
	 * @return double The current angle.
	 */
	public double getAngle(){
		return angle;
	}
	/**
	 * Gets the field tilt.
	 * @return double The current tilt.
	 */
	public double tilt(){
		return tilt;
	}
}
