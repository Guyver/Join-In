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

package test;

import IRGlancePackage.IRGlanceListener;
import IRGlancePackage.SpeedEvent;

/**
 * This is an example of how to print the speed results by implementing the IRGlancListener interface.
 * Implementaci�n de ejemplo de un IRGlanceListener que imprimie por pantalla
 * @author Santiago
 *
 */
public class ListenerTest implements IRGlanceListener {

	public void speedUpdated(SpeedEvent se) {
		System.out.println("\nSpeed = " +se.speed);

	}

}
