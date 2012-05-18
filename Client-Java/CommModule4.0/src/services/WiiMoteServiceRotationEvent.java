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
import org.wiigee.event.RotationEvent;

import control.IEventCommModule;

/**
 * Defines the WiiMote rotation events
 * @author Santiago Hors Fraile
 */
public class WiiMoteServiceRotationEvent implements IEventCommModule{

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
     * Represents the wiimote
     */
    protected Wiimote wiimote;
    
    
    
    
    /**
     * Sets the fields of this class with the information contained in the given parameter.
     * @param event The new RotationEvent.
     */
    
	public WiiMoteServiceRotationEvent(RotationEvent event) {
        this.pitch = event.getPitch();
        this.roll = event.getRoll();
        this.yaw = event.getYaw();
        this.wiimote=event.getWiimote();
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
     * Gets the wiimote.
     * @return wiimote The current wiimote.
     */
    public Wiimote getWiimote(){
    	return this.wiimote;
    }
}
