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

package iservices;

import services.NunchukServiceButtonPressedEvent;
import services.NunchukServiceButtonReleasedEvent;

import control.IListenerCommModule;

/**
 * Must be implemented by any class which wants to receive the events from the Nunchuk buttons
 * @author Santiago Hors Fraile
 */
public interface INunchukButtonsService extends IListenerCommModule {
	
	/**
	 * Must be implemented to manage the NunchukServiceButtonPressedEvent received.
	 * @param se This is the NunchukServiceButtonPressedEvent received.
	 */
	void nunchukButtonPressedEventReceived(NunchukServiceButtonPressedEvent se);
	/**
	 * Must be implemented to manage the NunchukServiceButtonReleasedEvent received.
	 * @param se This is the NunchukServiceButtonReleasedEvent received.
	 */
	void nunchukButtonReleasedEventReceived(NunchukServiceButtonReleasedEvent se);

}
