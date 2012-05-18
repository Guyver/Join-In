/* Copyright 2010 Santiago Hors Fraile 

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
package norut;


import iservices.*;

import services.WiiMoteServiceRotationEvent;
import services.WiiMoteServiceRotationSpeedEvent;

public class WiimoteRotationEventHandler implements ICalibrationFinishedService, IWiiMoteRotationSpeedService, IWiiMoteRotationService {

	

	@Override
	public void calibrationFinished() {
		System.out.println("You can start using the wiimotionplus.");
		
	}

	@Override
	public void rotationSpeedReceived(WiiMoteServiceRotationSpeedEvent se) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void rotationReceived(WiiMoteServiceRotationEvent se) {
	//	System.out.println("The roll: "+se.getRoll()+" The pitch: "+se.getPitch()+"The yaw: "+se.getYaw());
		se.getWiimote();
	}



	
}
