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

import NoninPackage.NoninData;


/**
 * Defines the Nonin events.
 * @author Santiago Hors Fraile
 *
 */
public class NoninServiceEvent implements IEventCommModule
{
	/**
	 * Represents the pulse of the person who is using the Nonin. 
	 */
	public int pulse;
	/**
	 * Represents the oxygen value of the person who is using the Nonin.
	 */
	public int oxy;
	/**
	 * Represents the Nonin state data.
	 */
	public NoninData data;

	
	/**
	 * Sets the field of this class with the given parameters.
	 * @param pulse The new pulse.
	 * @param oxy The new oxygen value.
	 * @param data The new Nonin data.
	 */
	public NoninServiceEvent (int pulse, int oxy, NoninData data){
		this.pulse= pulse;
		this.oxy= oxy;
		this.data = data;

	}
	
	
}
