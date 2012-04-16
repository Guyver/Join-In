package test;


import org.wiigee.device.Wiimote;
import org.wiigee.event.AccelerationEvent;
import org.wiigee.event.AccelerationListener;
import org.wiigee.event.ButtonListener;
import org.wiigee.event.ButtonPressedEvent;
import org.wiigee.event.ButtonReleasedEvent;
import org.wiigee.event.MotionStartEvent;
import org.wiigee.event.MotionStopEvent;
import org.wiigee.filter.RotationResetFilter;

public class MiListenerDeWiimote implements ButtonListener, AccelerationListener  {

	@Override
	public void buttonPressReceived(ButtonPressedEvent arg0) {
		System.out.println("lanzado el boton");
		if(arg0.getButton()==ButtonPressedEvent.BUTTON_1){
			System.out.println("Pulsado el botón 1");		

		}
		else if(arg0.getButton()==ButtonPressedEvent.BUTTON_2){
			System.out.println("Pulsado el botón 2");
		}
		else if(arg0.getButton()==ButtonPressedEvent.BUTTON_A){
			System.out.println("Pulsado el botón A");
			
		((Wiimote)arg0.getSource()).getWiiMotionPlus().resetRotationFilters();
			
			
			
			
			
		}
		else if(arg0.getButton()==ButtonPressedEvent.BUTTON_B){
			System.out.println("Pulsado el botón B");
		}else if(arg0.getButton()==ButtonPressedEvent.BUTTON_DOWN){
			System.out.println("Pulsado el botón DOWN");
		}else if(arg0.getButton()==ButtonPressedEvent.BUTTON_LEFT){
			System.out.println("Pulsado el botón LEFT");
		}
		else if(arg0.getButton()==ButtonPressedEvent.BUTTON_MINUS){
			System.out.println("Pulsado el botón MINUS");
		}else if(arg0.getButton()==ButtonPressedEvent.BUTTON_PLUS){
			System.out.println("Pulsado el botón PLUS");
		}else if(arg0.getButton()==ButtonPressedEvent.BUTTON_RIGHT){
			System.out.println("Pulsado el botón RIGHT");
		}else if(arg0.getButton()==ButtonPressedEvent.BUTTON_UP){
			System.out.println("Pulsado el botón UP");
		}else{
			System.out.println("Pulsado el botón HOME");
		}
		
	}

	@Override
	public void buttonReleaseReceived(ButtonReleasedEvent arg0) {
		if(arg0.getButton()==ButtonPressedEvent.BUTTON_1){
			System.out.println("Despulsado el botón 1");		

		}
		else if(arg0.getButton()==ButtonPressedEvent.BUTTON_2){
			System.out.println("Despulsado el botón 2");
		}
		else if(arg0.getButton()==ButtonPressedEvent.BUTTON_A){
			System.out.println("Despulsado el botón A");
		}
		else if(arg0.getButton()==ButtonPressedEvent.BUTTON_B){
			System.out.println("Despulsado el botón B");
		}else if(arg0.getButton()==ButtonPressedEvent.BUTTON_DOWN){
			System.out.println("Despulsado el botón DOWN");
		}else if(arg0.getButton()==ButtonPressedEvent.BUTTON_LEFT){
			System.out.println("Despulsado el botón LEFT");
		}
		else if(arg0.getButton()==ButtonPressedEvent.BUTTON_MINUS){
			System.out.println("Despulsado el botón MINUS");
		}else if(arg0.getButton()==ButtonPressedEvent.BUTTON_PLUS){
			System.out.println("Despulsado el botón PLUS");
		}else if(arg0.getButton()==ButtonPressedEvent.BUTTON_RIGHT){
			System.out.println("Despulsado el botón RIGHT");
		}else if(arg0.getButton()==ButtonPressedEvent.BUTTON_UP){
			System.out.println("Despulsado el botón UP");
		}else{
			System.out.println("Despulsado el botón HOME");
		}		
	}

	@Override
	public void accelerationReceived(AccelerationEvent arg0) {

		System.out.println("Recibida aceleración x: "+arg0.getX()+ "; y: "+arg0.getY()+"; z: "+arg0.getZ());
	}

	@Override
	public void motionStartReceived(MotionStartEvent arg0) {
		if(arg0.isTrainInitEvent()){
			
		}
	}

	@Override
	public void motionStopReceived(MotionStopEvent arg0) {
		// TODO Auto-generated method stub
		
	}

}
