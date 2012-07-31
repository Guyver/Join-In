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

import iservices.IWiiBoardService;

import services.WiiBoardServiceButtonEvent;
import services.WiiBoardServiceDisconnectionEvent;
import services.WiiBoardServiceMassEvent;
import services.WiiBoardServiceStatusEvent;

public class ClaseQueImplementaBoard implements IWiiBoardService{


	@Override
	public void wiiBoardButtonEvent(WiiBoardServiceButtonEvent se) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void wiiBoardStatusEvent(WiiBoardServiceStatusEvent se) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void wiiBoardUpdate(WiiBoardServiceMassEvent se) {
		System.out.println("Peso: "+se.totalWeight);		
	}

	
	@Override
	public void wiiBoardMassEvent(WiiBoardServiceMassEvent se) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void wiiBoardDisconnectionEvent(WiiBoardServiceDisconnectionEvent se) {
		// TODO Auto-generated method stub
		
	}

}
