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




import auxiliaryForAndroid.Point;
import control.IEventCommModule;
/**
 * Defines the Nunchuk analgo stick event 
 * @author Santiago Hors Fraile
 */
public class NunchukAnalogStickServiceEvent implements IEventCommModule{
	/**
	 * Represents the 2D point of the analog stick.
	 */
	protected Point point;
	/**
	 * Represents the Wiimote object to whom the Nunchuk is attached.
	 */
	protected Object source;
	/**
	 * Represents the angle of the analog stick.
	 */
	protected double angle;
	/**
	 * Represents the titl of the analog stick.
	 */
	protected double tilt;
	
	/**
	 * Sets the fields of this class with the given parameters.
	 * @param source The new Wiimote.
	 * @param point The new point.
	 */
	public NunchukAnalogStickServiceEvent(Object source, Point point) {
		this.source = source;
		this.point= point;
	}

	/**
	 * Gets the current Wiimote object.
	 * @return Wiimote The current Wiimote object.
	 */
	public Object getSource() {
		return source;
	}
	/**
	 * Gets the current point.
	 * @return Point The current Point.
	 */
	public Point getPoint() {
		return point;
	}
	/**
	 * Gets the current angle.
	 * @return double The current angle.
	 */
	public double getAngle(){
		return angle;
	}
	/**
	 * Gets the current tilt.
	 * @return double The current tilt.
	 */
	public double tilt(){
		return tilt;
	}

}
