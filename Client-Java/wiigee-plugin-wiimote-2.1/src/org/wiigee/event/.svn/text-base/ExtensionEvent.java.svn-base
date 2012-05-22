package org.wiigee.event;



import org.wiigee.control.Extension;
import org.wiigee.device.Wiimote;
/**
 * Defines the extension event.
 * @author Santiago Hors Fraile
 */
public class ExtensionEvent {

	/**
	 * Represents the WiiMote to whom the extension is connected.
	 */
	private Wiimote source;
	
	/**
	 * Represents the extension itself (Nunchuk or WiiMote)
	 */
	private Extension extension;
	
	/**Represents the type of extension
	 * 
	 */
	private int type; 

	/**
	 * Calls the constructor with parameters with the default values.
	 * @param source
	 */
	public ExtensionEvent(Wiimote source) {
		this(source, null, 0);
	}
	
    // For connections and disconnections
    public static final int TYPE_NUNCHUK= 1;
    public static final int TYPE_WIIMOTIONPLUS = 2;

	/**
	 * Initializes the fields of this class with the given parameters.
	 * @param source The wiimote to whom the extension is connected.
	 * @param extension The extension itself.
	 * @param type The type of extension. 
	 */
	public ExtensionEvent(Wiimote source, Extension extension, int type) {
		this.source = source;
		this.extension = extension;
		this.type = type;
	}
	
	/**
	 * Gets the field source.
	 * @return Wiimote The current wiimote.
	 */
	public Wiimote getSource() {
		return source;
	}

	/**
	 * Gets the extension field
	 * @return Extension The current extension.
	 */ 
	public Extension getExtension() {
		return extension;
	}
	
	/**
	 * Reports if the extension is connected or not.
	 * @return boolean True if the extension is connected, false otherwise.
	 */
	public boolean isExtensionConnected() {
		return extension != null;
	}

	/**
	 * Sets the type of extension.
	 * @param type They type of extension.
	 */
	public void setType(int type) {
		this.type = type;
	}
	
	/**
	 * Gets the type of the extension.
	 * @return int They extension type.
	 */
	public int getType() {
		return type;
	}


	
	
	
	
	
}
