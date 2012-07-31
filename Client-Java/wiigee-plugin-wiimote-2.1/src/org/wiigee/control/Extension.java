package org.wiigee.control;

import org.wiigee.device.Wiimote;

/**
 * Implemented by all WiiMote extensions (Nunchuk and WiiMotionPlus)
 * @author Santiago Hors Fraile
 */
public interface Extension {
	/**
	 * Must initialize all fields of the extension.
	 */
	public void initialize();
	/**
	 * Must deal with the data given as parameter.
	 * @param extensionData The received bytes from the device.
	 */
	public void parseExtensionData(byte[] extensionData);
	/**
	 * Must set the field Wiimote with the reference WiiMote object given as a parameter..
	 * @param mote The reference WiiMote object.
	 */
	public void setMote(Wiimote mote);
}
