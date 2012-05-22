package test;


import org.wiigee.event.AccelerometerEvent;
import org.wiigee.event.AccelerometerListener;
import org.wiigee.event.AnalogStickEvent;
import org.wiigee.event.AnalogStickListener;
import org.wiigee.event.DataListener;
import org.wiigee.event.ExtensionEvent;
import org.wiigee.event.ExtensionListener;
import org.wiigee.event.NunchukButtonListener;
import org.wiigee.event.NunchukButtonPressedEvent;
import org.wiigee.event.NunchukButtonReleasedEvent;
import org.wiigee.device.Nunchuk;
import org.wiigee.device.WiiMotionPlus;


public class MiExtensionListenerDeNunchuk implements ExtensionListener, AnalogStickListener,NunchukButtonListener, AccelerometerListener<Nunchuk> {

	@Override
	public void add(Class<DataListener> class1, DataListener listener) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void extensionConnected(ExtensionEvent evt) {
		//if(evt.getExtension() instanceof Nunchuk){
		if(evt.getType() == 1){
			System.out.println("Se ha lanzado el evento de que se ha conectado un nunchuk");
		}else{
			//System.out.println("Se ha lanzado el evento de que se ha conectado un motionplus");
		}
	}

	@Override
	public void extensionDisconnected(ExtensionEvent evt) {
		//if(evt.getExtension() instanceof Nunchuk){
		if(evt.getType()==1){
			System.out.println("Se ha lanzado el evento de que se ha desconectado un nunchuk");
		}else if(evt.getExtension() instanceof WiiMotionPlus){
			//System.out.println("Se ha lanzado el evento de que se ha desconectado un motionplus");
		}else{
			//System.out.println("problema");
		}
	}

	@Override
	public ExtensionListener[] getListeners(Class<ExtensionListener> class1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void remove(Class<ExtensionListener> class1,
			ExtensionListener listener) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void analogStickChanged(AnalogStickEvent evt) {
		
		System.out.println("Se ha lanzado el evento del joystick con valor "+evt.getPoint());

	}
	

	@Override
	public void accelerometerChanged(AccelerometerEvent<Nunchuk> evt) {
		//System.out.println("Se ha lanzado el evento del aceleración de nunchuk con valor: X="+evt.getX() +" Y="+evt.getY()+" Z="+evt.getZ());
	}

	@Override
	public void buttonPressedReceived(NunchukButtonPressedEvent event) {
		// TODO Auto-generated method stub
		System.out.println("Pulsado "+event.getButton() );
	}

	@Override
	public void buttonReleasedReceived(NunchukButtonReleasedEvent event) {
		// TODO Auto-generated method stub
		System.out.println("Despulsado "+event.getButton());
		
	}

	

	

}
