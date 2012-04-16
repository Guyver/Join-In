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

import iservices.IWiiMoteIRService;

import services.WiiMoteServiceIREvent;

public class ClaseQueImplementaIR implements IWiiMoteIRService {

	@Override
	public void iRReceived(WiiMoteServiceIREvent event) {
		System.out.println("Longitud ="+event.getCoordinates().length);
		for(int i = 0; i< event.getCoordinates().length; i++){
			if(event.isValid(i))
			System.out.println(event.getCoordinates()[i][0]);
			System.out.println(event.getCoordinates()[i][1]);

		}	}

}
