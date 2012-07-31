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
package kinectThreads;

/**
 * This enum lists all the different actions that the games need about the user. 
 * @author Santiago Hors Fraile
 *
 */
public enum KinectUserActionEnum {
	STAND(0),
	WALK(1),
	PAUSE(2),
	RESUME(3),
	HUG(4),
	REACHED(5),
	ACCEPT(6),
	CANCEL(7),
	PICKED_UP_FROM_LEFT(8),
	PICKED_UP_FROM_RIGHT(9);

	private int code;

	private KinectUserActionEnum(int code) 
	{  this.code = code; }

	/**
	 * @return The numeric code assigned to the action.
	 */
	public short getCode() 
	{ return (short)code;  }
}
