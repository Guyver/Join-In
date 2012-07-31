/**
 * Copyright 2012 Santiago Hors Fraile

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

/**
 * Defines the KinectUserOutOfScopeServiceEvent events.
 * 
 * @author Santiago Hors Fraile
 * 
 */
public class KinectUserOutOfScopeServiceEvent implements IEventCommModule {

	private int userId;
	
	public KinectUserOutOfScopeServiceEvent(int userId) {
		setUserId(userId);
	}

	/**
	 * @return the userId
	 */
	public int getUserId() {
		return userId;
	}

	/**
	 * @param userId the userId to set
	 */
	public void setUserId(int userId) {
		this.userId = userId;
	}
	
}