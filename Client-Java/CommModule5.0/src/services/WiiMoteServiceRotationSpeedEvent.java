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

import org.wiigee.event.RotationSpeedEvent;

import control.IEventCommModule;
/**
 * Defines the WiiMote rotation speed events.
 * @author Santiago Hors Fraile
 *
 */
public class WiiMoteServiceRotationSpeedEvent implements IEventCommModule{
	
	/**
	 * Represents the raw angle velocity of the x axis.
	 */
	protected double psi;
	/**
	 * Represents the raw angle velocity of the y axis.
	 */
    protected double theta;
	/**
	 * Represents the raw angle velocity of the z axis.
	 */
    protected double phi;


    
    /**
     * Sets the fields of this class with the information contained in the given parameter.
     * @param event The new RotationSpeedEvent.
     */
    public WiiMoteServiceRotationSpeedEvent(RotationSpeedEvent event) {
    	this.psi = event.getPsi();
        this.theta = event.getTheta();
        this.phi = event.getPhi();
     }
	/**
	 * Gets the current field psi.
	 * @return double The current psi.
	 */
	public double getPsi() {
        return this.psi;
    }
	/**
	 * Gets the current field theta.
	 * @return double The current theta.
	 */
    public double getTheta() {
        return this.theta;
    }
	/**
	 * Gets the current field phi.
	 * @return double The current phi.
	 */
    public double getPhi() {
        return this.phi;
    }
}
