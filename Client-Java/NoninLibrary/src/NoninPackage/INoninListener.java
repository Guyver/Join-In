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
package NoninPackage;

import java.util.EventListener;

/**
 * Must be implemented by all classes that want to get and deal with the Nonin events.
 * @author Santiago Hors Fraile
 */
public interface INoninListener extends EventListener {
	/**
	 * Must be implemented to manage the Nonin events.
	 * @param ne The new Nonin event.
	 */
	public void noninUpdate(NoninEvent ne);

	
}
