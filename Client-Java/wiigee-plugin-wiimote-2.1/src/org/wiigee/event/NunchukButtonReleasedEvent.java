package org.wiigee.event;

import org.wiigee.device.Nunchuk;

/**
 * Defines the Nunchuk button released event.
 * @author Santiago Hors Fraile
 */
public class NunchukButtonReleasedEvent  {
	
	// Fixed number values.
	public static final int NO_BUTTON = 1;
	
	public static final int BUTTON_C = 0x02;
	
	public static final int BUTTON_Z = 0x01;;

	/**
	 * The Nunchuk which has been pressed a button.
	 */
	private Nunchuk nunchuk;
	/**
	 * The button pressed.
	 */
	int button;
	/**
	 * Creates a NunchukButtonReleasedEvent with the Wiimote source whose
	 * Button has been pressed and the integer representation of the button.
	 * 
	 * @param nun The Nunchuk.
	 * @param button The button.
	 */
	public NunchukButtonReleasedEvent(Nunchuk nun, int button) {
		
	    this.button = button;
	    this.nunchuk=nun;
	}
	/**
	 * Gets the field button.
	 * @return int The current button.
	 */
	public int getButton() {
		return this.button;
	}

	
	/**
	 * Gets the field Nunchuk.
	 * @return Nunchuk The current Nunchuk.
	 */
	public Nunchuk getNunchuk() {
		return nunchuk;
	}

}
