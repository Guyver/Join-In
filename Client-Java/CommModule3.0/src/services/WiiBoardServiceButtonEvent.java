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

import control.IEventCommModule;

import edu.unsw.cse.wiiboard.event.WiiBoardButtonEvent;

/**
 * Defines the WiiBoard button events.
 * @author Santiago Hors Fraile
 */
public class WiiBoardServiceButtonEvent implements IEventCommModule{

	
	/**
	 * True if the button is currently pushed down.
	 */
	public boolean isPressed;
	
	/**
	 * Determines if the button is released after being pressed down.
	 * Note: If the button is in the pressed down state when the board is connecting,
	 * the first button event that is expected to report a release might not.
	 * 
	 * True if the button was pressed down and now is not pressed down
	 */
	public boolean isReleased;

	/**
	 * Sets the fields of this class with the given parameter.
	 * @param buttonEvent The new button event.
	 */
	public WiiBoardServiceButtonEvent(WiiBoardButtonEvent buttonEvent) {
		this.isPressed = buttonEvent.isPressed();
		this.isReleased = buttonEvent.isReleased();
	}

}
