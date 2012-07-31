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
 * Defines the equivalences between buttons.
 * @author Santiago Hors Fraile
 *
 */
public class WiiMoteServiceButton implements IEventCommModule{
	public static final int BUTTON_2 = 1;
	public static final int BUTTON_1 = 2;
	public static final int BUTTON_B = 4;
	public static final int BUTTON_A = 8;
	public static final int BUTTON_MINUS = 16;
	public static final int BUTTON_HOME =  128;
	public static final int BUTTON_LEFT = 256;
	public static final int BUTTON_RIGHT = 512;
	public static final int BUTTON_DOWN = 1024;
	public static final int BUTTON_UP = 2048;
	public static final int BUTTON_PLUS = 4096;
	
	public static String returnButtonName(int button) {
		
		if(button==1){
			return "BUTTON_2";
		}else if(button==2){
			return "BUTTON_1";
		}else if(button==4){
			return "BUTTON_B";
		}else if(button==8){
			return "BUTTON_A";
		}else if(button==16){
			return "BUTTON_MINUS";
		}else if(button==128){
			return "BUTTON_HOME";
		}else if(button==256){
			return "BUTTON_LEFT";
		}else if(button==512){
			return "BUTTON_RIGHT";
		}else if(button==1024){
			return "BUTTON_DOWN";
		}else if(button==2048){
			return "BUTTON_UP";
		}else if(button==4096){
			return "BUTTON_PLUS";
		}
		
		return null;
	}
	
	
	
	
}
