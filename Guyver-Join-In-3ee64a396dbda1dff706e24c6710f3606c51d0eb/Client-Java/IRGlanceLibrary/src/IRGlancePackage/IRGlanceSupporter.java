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

package IRGlancePackage;

import org.wiigee.event.InfraredEvent;
import org.wiigee.event.InfraredListener;




/**
 * Allows to use the WiiUseJ and GeeWee IR event detection functions.
 * There are several functions that we don not need but we have to keep in the code because this class implements an interface which has those functions.
 * In this class, it is implemented both WiiGee and WiiUseJ listener interfaces that allow to use this class as a facade to the IRGlance class. If you want to use a new package of drivers to manage the IR system, you will have to
 * code the correct function body so that it calls to IRSpotGlanced with the number of IR points that the WiiMote has detected.
 * @author Santiago Hors Fraile
 */
public abstract class IRGlanceSupporter implements InfraredListener { 
	
	/**
	 * Must be called each time we know the number of IR spots on the WiiRemote camera. 
	 * @param num
	 */
	public abstract void IRSpotGlanced (int num);

	/**
	 * Implementation of the WiiGee function to detect the IR event. Each time we get an IREvent call IRSpotGlanced.
	 * @param event The new IREvent.
	 */
	
	public void infraredReceived(InfraredEvent event) {
		
		boolean[] vector = (event.getValids());
		 //Acum is the number of IR points that the WiiMote has detected. 
		int acum=0;
		for (int i=0; i<vector.length; i++){
			if (vector[i]){
				acum++;
			}
		}
		IRSpotGlanced(acum);
	}
	
	


}
