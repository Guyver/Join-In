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


import iservices.*;

import services.WiiMoteServiceButtonPressReceivedEvent;
import services.WiiMoteServiceButtonReleaseReceivedEvent;

public class ClaseQueImplementaBotones implements IWiiMoteButtonsService {

	
	@SuppressWarnings("static-access")
	@Override
	
	public void buttonPressedEventReceived(WiiMoteServiceButtonPressReceivedEvent se) {
		if(se.button==se.BUTTON_1){
			System.out.println("Se ha pulsado el botón 1");

		}else if(se.button==se.BUTTON_2){
			System.out.println("Se ha pulsado el botón 2");

		}else if(se.button==se.BUTTON_A){
			System.out.println("Se ha pulsado el botón A");
			synchronized(this){
				notifyAll();
			}
		}else if(se.button==se.BUTTON_B){
			System.out.println("Se ha pulsado el botón B");

		}else if(se.button==se.BUTTON_DOWN){
			System.out.println("Se ha pulsado el botón abajo");

		}else if(se.button==se.BUTTON_HOME){
			System.out.println("Se ha pulsado el botón casa");

		}else if(se.button==se.BUTTON_LEFT){
			System.out.println("Se ha pulsado el botón izquierda");

		}else if(se.button==se.BUTTON_MINUS){
			System.out.println("Se ha pulsado el botón menos");

		}else if(se.button==se.BUTTON_PLUS){
			System.out.println("Se ha pulsado el botón más");

		}else if(se.button==se.BUTTON_RIGHT){
			System.out.println("Se ha pulsado el botón derecha");

		}else if(se.button==se.BUTTON_UP){
			System.out.println("Se ha pulsado el botón arriba");

		}
		
	}

	@SuppressWarnings("static-access")
	@Override
	public void buttonReleasedEventReceived(WiiMoteServiceButtonReleaseReceivedEvent se) {
		
		if(se.button==se.BUTTON_1){
			System.out.println("Se ha despulsado el botón 1");

		}else if(se.button==se.BUTTON_2){
			System.out.println("Se ha despulsado el botón 2");

		}else if(se.button==se.BUTTON_A){
			System.out.println("Se ha despulsado el botón A");

		}else if(se.button==se.BUTTON_B){
			System.out.println("Se ha despulsado el botón B");

		}else if(se.button==se.BUTTON_DOWN){
			System.out.println("Se ha despulsado el botón abajo");

		}else if(se.button==se.BUTTON_HOME){
			System.out.println("Se ha despulsado el botón casa");

		}else if(se.button==se.BUTTON_LEFT){
			System.out.println("Se ha despulsado el botón izquierda");

		}else if(se.button==se.BUTTON_MINUS){
			System.out.println("Se ha despulsado el botón menos");

		}else if(se.button==se.BUTTON_PLUS){
			System.out.println("Se ha despulsado el botón más");

		}else if(se.button==se.BUTTON_RIGHT){
			System.out.println("Se ha despulsado el botón derecha");

		}else if(se.button==se.BUTTON_UP){
			System.out.println("Se ha despulsado el botón arriba");

		}
		
	}



	
}
