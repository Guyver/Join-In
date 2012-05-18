package norut;

import iservices.IWiiMoteButtonsService;
import services.WiiMoteServiceButtonPressReceivedEvent;
import services.WiiMoteServiceButtonReleaseReceivedEvent;
import control.DeviceManager;

public class WiimoteButtonsEventHandler implements IWiiMoteButtonsService{

	@SuppressWarnings("static-access")
	public void buttonPressedEventReceived(WiiMoteServiceButtonPressReceivedEvent se) {
		if(se.button==se.BUTTON_1){
			//System.out.println("Se ha pulsado el botón 1");

		}else if(se.button==se.BUTTON_2){
		//	System.out.println("Se ha pulsado el botón 2");

		}else if(se.button==se.BUTTON_A){
		//	System.out.println("Se ha pulsado el botón A");
			
			DeviceManager dm = DeviceManager.getDeviceManager();
			dm.resetWiimoteCalibration(1);
			dm.resetWiimoteCalibration(2);
			synchronized(this){
				notifyAll();
			}
		}else if(se.button==se.BUTTON_B){
		//	System.out.println("Se ha pulsado el botón B");

		}else if(se.button==se.BUTTON_DOWN){
		//	System.out.println("Se ha pulsado el botón abajo");

		}else if(se.button==se.BUTTON_HOME){
		//	System.out.println("Se ha pulsado el botón casa");

		}else if(se.button==se.BUTTON_LEFT){
		//	System.out.println("Se ha pulsado el botón izquierda");

		}else if(se.button==se.BUTTON_MINUS){
		//	System.out.println("Se ha pulsado el botón menos");

		}else if(se.button==se.BUTTON_PLUS){
		//	System.out.println("Se ha pulsado el botón más");

		}else if(se.button==se.BUTTON_RIGHT){
		//	System.out.println("Se ha pulsado el botón derecha");

		}else if(se.button==se.BUTTON_UP){
		//	System.out.println("Se ha pulsado el botón arriba");

		}
		
	}

	@Override
	public void buttonReleasedEventReceived(WiiMoteServiceButtonReleaseReceivedEvent se) {
		/*
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

		}*/
		
	}
}
