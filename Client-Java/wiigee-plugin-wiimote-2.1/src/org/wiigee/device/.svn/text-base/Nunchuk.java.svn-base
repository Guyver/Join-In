package org.wiigee.device;

import java.awt.Point;
import java.io.IOException;


import org.wiigee.control.NunchukCalibrationData;
import org.wiigee.event.AccelerometerEvent;
import org.wiigee.event.AccelerometerListener;
import org.wiigee.event.AnalogStickEvent;
import org.wiigee.event.AnalogStickListener;
import org.wiigee.event.DataEvent;
import org.wiigee.event.DataListener;
import org.wiigee.event.NunchukButtonListener;
import org.wiigee.event.NunchukButtonPressedEvent;
import org.wiigee.event.NunchukButtonReleasedEvent;
import org.wiigee.control.Extension;
/**
 * Defines all logic related with the Nunchuk
 * Partially taken form WiiUseJ library.
 * @author Santiago Hors Fraile
 */
public class Nunchuk implements DataListener,Extension {

	/**
	 * Represents the WiiMote to whom the Nunchuk is attached.
	 */
	private Wiimote wiimote;

	/**
	 * Stores the Nunchuk calibration data.
	 */
	private NunchukCalibrationData calibrationData;

	
	
	/**
	 * Sets the Nunchuk calibration data from a Nunchuk data event.
	 * This event must be one with the analog joystick in the center position.
	 * @param evt The new event from Nunchuk from which data is going to be taken.
	 */
	public void dataRead(DataEvent evt) {
		if (calibrationData == null && evt.getError() == 0
				&& evt.getAddress()[0] == 0x00
				&& (evt.getAddress()[1] & 0xff) == 0x30
				&& evt.getPayload().length == 0x0f) {

		System.out.println("Calibration Data received.");

			byte[] payload = evt.getPayload();

			calibrationData = new NunchukCalibrationData();
			calibrationData.setZeroForceX(payload[0] & 0xff);
			calibrationData.setZeroForceY(payload[1] & 0xff);
			calibrationData.setZeroForceZ(payload[2] & 0xff);
			calibrationData.setGravityForceX(payload[4] & 0xff);
			calibrationData.setGravityForceY(payload[5] & 0xff);
			calibrationData.setGravityForceZ(payload[6] & 0xff);
			calibrationData.setMinimumAnalogPoint(new Point(payload[9] & 0xff,
					payload[12] & 0xff));
			calibrationData.setMaximumAnalogPoint(new Point(payload[8] & 0xff,
					payload[11] & 0xff));
			calibrationData.setCenterAnalogPoint(new Point(payload[10] & 0xff,
					payload[13] & 0xff));
		}
	}

	/**
	 * Throws an Nunchuk acceleration event to each listener in the listnerList.
	 * @param data The data bytes that has been sent via BlueTooth from the Nunchuk.
	 */
	@SuppressWarnings("unchecked")
	protected void fireAccelerometerEvent(byte[] data) {
		AccelerometerListener<Nunchuk>[] listeners = wiimote.getListenerList().getListeners(AccelerometerListener.class);
		if (listeners.length == 0) {
			return;
		}

		int ax = (((data[2] & 0xff)) << 2) | ((data[5] & 0x0c) >> 2);
		int ay = (((data[3] & 0xff)) << 2) | ((data[5] & 0x30) >> 2);
		int az = (((data[4] & 0xff)) << 2) | ((data[5] & 0xc0) >> 2);
		AccelerometerEvent<Nunchuk> evt = new AccelerometerEvent<Nunchuk>(this,	ax, ay, az);
		for (AccelerometerListener<Nunchuk> l : listeners) {
			l.accelerometerChanged(evt);
		}
	}
	/**
	 * Throws an Nunchuk acceleration event to each listener in the listnerList.
	 * This function must only be called when the WiiMotionPlus is also attached to the WiiMote.
	 * @param data The data bytes that has been sent via BlueTooth from the Nunchuk.
	 */
	@SuppressWarnings("unchecked")
	private void fireAccelerometerEventInterleave(byte[] data) {
		AccelerometerListener<Nunchuk>[] listeners = wiimote.getListenerList().getListeners(AccelerometerListener.class);
		if (listeners.length == 0) {
			return;
		}

		int ax = (((data[2] & 0xff)) << 2) | ((data[5] & 0x10) >> 3);//dejamos el bit menos significativo a 0, de ahí que lo mueva 3 y no 4
		int ay = (((data[3] & 0xff)) << 2) | ((data[5] & 0x20) >> 4);
		int az = (((data[4] & 0xfe)) << 2) | ((data[5] & 0xc0) >> 5);
		AccelerometerEvent<Nunchuk> evt = new AccelerometerEvent<Nunchuk>(this,	ax, ay, az);
		for (AccelerometerListener<Nunchuk> l : listeners) {
			l.accelerometerChanged(evt);
		}
	}
	/**
	 * Throws an Nunchuk analog stick event to each listener in the listnerList.
	 * @param data The data bytes that has been sent via BlueTooth from the Nunchuk.
	 */
	protected void fireAnalogStickEvent(byte[] data) {
		AnalogStickListener[] listeners = wiimote.getListenerList().getListeners(AnalogStickListener.class);
		if (listeners.length == 0) {
			return;
		}

		int sx = data[0] & 0xff;
		int sy = data[1] & 0xff;
		AnalogStickEvent evt = new AnalogStickEvent(this, new Point(sx & 0xff,sy & 0xff));
		
		if(evt.getPoint().x>10 || evt.getPoint().x<-10 || evt.getPoint().y>10 || evt.getPoint().y<-10){
			for (AnalogStickListener l : listeners) {
				
				l.analogStickChanged(evt);
			}
		}
	}
	/**
	 * Throws an Nunchuk button event to each listener in the listnerList.
	 * @param data The data bytes that has been sent via BlueTooth from the Nunchuk.
	 */
	protected void fireButtonEvent(byte[] data) {
		NunchukButtonListener[] listeners = wiimote.getListenerList().getListeners(NunchukButtonListener.class);
		if (listeners.length == 0) {
			return;
		}

		// we invert the original data as the wiimote returns
		// button pressed as nil and thats not that useable.
		int modifiers = (data[5] & 0x03) ^ 0x03;
		
		int delta = wiimote.getNunchukbuttonstate() ^ modifiers;

		int shift = 0x01;
		while (shift<=0x100){
			if((delta&shift)==shift){//change detected
				if((modifiers&shift)==shift){//press detected
					NunchukButtonPressedEvent evt = new NunchukButtonPressedEvent(this, shift);		
					for (NunchukButtonListener l : listeners) {		
						l.buttonPressedReceived(evt);
					}
				
				}else{//release detected
					NunchukButtonReleasedEvent evt = new NunchukButtonReleasedEvent(this, shift);				
					for (NunchukButtonListener l : listeners) {					
						l.buttonReleasedReceived(evt);					
					}		
				}
			}
			shift<<=1;
		}
		wiimote.setNunchukbuttonstate(modifiers);
		
	
	}
	/**
	 * Throws an Nunchuk acceleration event to each listener in the listnerList.
	 * This function must only be called when the WiiMotionPlus is also attached to the WiiMote.
	 * @param data The data bytes that has been sent via BlueTooth from the Nunchuk.
	 */
	protected void fireButtonEventInterLeave(byte[] data) {
		NunchukButtonListener[] listeners = wiimote.getListenerList().getListeners(NunchukButtonListener.class);
		if (listeners.length == 0) {
			return;
		}
		// we invert the original data as the wiimote returns
		// button pressed as nil and thats not that useable.
		int modifiers = ((data[5] & 0x0C) ^ 0x0C)>>2;

		int delta = wiimote.getNunchukbuttonstate() ^ modifiers;
		
		
		int shift = 0x01;
		while (shift<=0x10){
			
			if((delta&shift)==shift){//change detected
				if((modifiers&shift)==shift){//press detected
					NunchukButtonPressedEvent evt = new NunchukButtonPressedEvent(this, shift);		
					for (NunchukButtonListener l : listeners) {
						l.buttonPressedReceived(evt);
					}
				}else{//release detected
					NunchukButtonReleasedEvent evt = new NunchukButtonReleasedEvent(this, shift);
					for (NunchukButtonListener l : listeners) {
						l.buttonReleasedReceived(evt);
					}				
				}
			}
			shift<<=1;
		}
		wiimote.setNunchukbuttonstate(modifiers);
	}

	/**
	 * Gets the calibrationData field.
	 * @return NunchukCalibrationData The calibrationData field.
	 */
	public NunchukCalibrationData getCalibrationData() {
		return calibrationData;
	}

	/**
	 * Gets the WiiMote to whom the Nunchuk is attached.
	 * @return Wiimote the WiiMote to whom the Nunchuk is attached.
	 */
	public Wiimote getMote() {
		return wiimote;
	}

	/**
	 * Writes in the WiiMote registers the information needed so that it recognizes that a Nunchuk has been connected.
	 */
	public void initialize() {
		
		// initialize
		try {
			wiimote.writeRegister(new byte[] { (byte) 0xa4, 0x00, 0x40}, new byte[] { 0x00 });
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// request calibration data
		try {
			wiimote.readRegister(new byte[] { (byte) 0xa4, 0x00, 0x30 }, new byte[] {0x00, 0x0f });
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Handles the bytes given as parameter. They will have a different handling whether a WiiMote is connected or not.
	 * @param extensionData The string of bytes sent by the WiiMote containing the Nunchuk state information.
	 */
	public void parseExtensionData(byte[] extensionData) {
		
		if(!wiimote.isWiiMotionPlusEnabled()){//Modo normal
		    fireAnalogStickEvent(extensionData); 
			fireAccelerometerEvent(extensionData);
			fireButtonEvent(extensionData);
		}else{//Modo interleave con pérdida de precisión
			fireAnalogStickEvent(extensionData); 
			fireAccelerometerEventInterleave(extensionData);
			fireButtonEventInterLeave(extensionData);

		}
	}

	
	/**
	 * Set the wiimote field with the wiimote given as parameter.
	 * @param wiimote The new wiimote.
	 */
	public void setMote(Wiimote wiimote) {
		this.wiimote = wiimote;
	}

	/**
	 * Overwrites the toString function.
	 * @return String The string "Nunchuk".
	 */
	@Override
	public String toString() {
		return "Nunchuk";
	}
	
	/**
	 * Initializes the class and sets the wiimote field to the one given as parameter.
	 * @param wiimote The WiiMote to whom the Nunchuk is attached.
	 */
	public Nunchuk (Wiimote wiimote){
		this.wiimote = wiimote;
		this.initialize();
	}

}
