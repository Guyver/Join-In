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

/**
 * Defines the static values of the buttons in the Nunchuk button events. This class will be extended by the pressed and released event.
 * @author Santiago Hors Fraile 
 */
public class NunchukServiceButton implements IEventCommModule{
	
	public static final int NO_BUTTON = 1;
	
	public static final int BUTTON_C = 0x02;

	public static final int BUTTON_Z = 0x01;;

	
	public static String getValue(int button){
		if(button==NO_BUTTON){
			return "NO_BUTTON";
		}else if(button==BUTTON_C){
			return "BUTTON_C";
		}else if(button==BUTTON_Z){
			return "BUTTON_Z";
		}else{
			return "";
		}
	}

}
