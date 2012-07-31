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
package control;


import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.wiigee.event.DataEvent;
import org.wiigee.event.DataListener;



/**
 * Abstract class which gives basic functionalities to all other launchers.
 * @author Santiago Hors Fraile
 */
public abstract class LauncherWrapper extends Thread implements DataListener{

	/**
	 * Contains all listeners that are listening the service that this class wraps
	 */
	public List<IListenerCommModule> listenersList = new CopyOnWriteArrayList<IListenerCommModule>();
	

	/**
	 * Activated when there has been as many calls from  service as dropServices()
	 */
	protected boolean deviceNotNecessaryAnyLonger = false; 
	
	/**
	 * Adds a listener to the list of listeners
	 * @param l This is the listener to be added
	 * @throws Exception 
	 */
	public void addListener(IListenerCommModule l) throws Exception {
		
		 if(!deviceNotNecessaryAnyLonger){
			
			listenersList.add(l);
		
		}else{
			throw new Exception();
		}
		

	}
	
	/**
	 * Removes a listener to the list of listeners
	 * @param l This is the listener to be removed
	 */
	public void removeListener(IListenerCommModule l){		
		if(!deviceNotNecessaryAnyLonger){
			listenersList.remove(l);
		}
	}
	
	/**
	 * Called when any service is no longer needed.
	 */
	public void dropService(){
		//CommModule 2.0
		listenersList= new CopyOnWriteArrayList<IListenerCommModule>();
		deviceNotNecessaryAnyLonger = true;
		/* CommModule 1.0
		  if(listenersList.size()>0){
				listenersList.remove(listenersList.size()-1);
				if(listenersList.size()==0){
					deviceNotNecessaryAnyLonger = true; 
				}
			}
		*/

	}
		
	
	@Override
	public void dataRead(DataEvent arg0) {
		
	}

	
	
	
}
