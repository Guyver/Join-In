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

import org.wiigee.event.NunchukButtonPressedEvent;

import control.IEventCommModule;
/**
 * Defines the Nunchuk button pressed events.
 * @author Santiago Hors Fraile
 */
public class NunchukServiceButtonPressedEvent extends NunchukServiceButton implements IEventCommModule{
	/**
	 * Represents the pressed button.
	 */
	public int button;

	/**
	 * Sets the pressed button with the given parameter.
	 * @param event The new pressed button.
	 */
	public NunchukServiceButtonPressedEvent(NunchukButtonPressedEvent event) {
		this.button=event.getButton();
	}
	
	
}
