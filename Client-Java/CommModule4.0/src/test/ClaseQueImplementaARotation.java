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

import iservices.ICalibrationFinishedService;
import iservices.IWiiMoteRotationService;
import iservices.IWiiMoteRotationSpeedService;

import services.WiiMoteServiceRotationEvent;
import services.WiiMoteServiceRotationSpeedEvent;

public class ClaseQueImplementaARotation implements IWiiMoteRotationService, IWiiMoteRotationSpeedService, ICalibrationFinishedService{
	
	
	@Override
	public void rotationReceived(WiiMoteServiceRotationEvent se) {
	
			System.out.println("Se ha lanzado un evento de rotación con Roll="+se.getRoll()+" Pitch="+se.getPitch()+" Yaw= "+se.getYaw());
		
	}

	@Override
	public void rotationSpeedReceived(WiiMoteServiceRotationSpeedEvent se) {
		System.out.println("Se ha lanzado un evento de velocidad de rotación con Phi="+se.getPhi()+" Psi="+se.getPsi()+" Theta="+se.getTheta());
	}

	@Override
	public void calibrationFinished() {
		System.out.println("Se ha lanzado un evento de final de calibración");
	}

}
