package org.wiigee.event;
import java.util.EventListener;

/**
 * Must be implemented by all class which wants to deal with ExtensionEvents.
 * @author Santiago hors Fraile
 *
 */
public interface ExtensionListener extends EventListener{

	/**
	 * Must be implemented to deal with ExtensionEvents when an extension is connected.
	 * @param evt The new ExtensionEvent.
	 */
	public void extensionConnected(ExtensionEvent evt);
	/**
	 * Must be implemented to deal with ExtensionEvents when an extension is disconnected.
	 * @param evt The new ExtensionEvent.
	 */
	public void extensionDisconnected(ExtensionEvent evt);
	
	/**
	 * Gets the listeners for a specific kind of extension.
	 * @param class1 The type ExtensionListener.
	 * @return ExtensionListener The array of ExtensionListeners of the type of class given as parameter.
	 */
	public ExtensionListener[] getListeners(Class<ExtensionListener> class1);

	/**
	 * Adds the listener given as a parameter to the ExtionListeners array.
	 * @param class1 The kind of Extension.
	 * @param listener The listener to be added.
	 */
	public void add(Class<DataListener> class1, DataListener listener);
	/**
	 * Removes the listener given as a parameter from the ExtionListeners array.
	 * @param class1 The kind of Extension.
	 * @param listener The listener to be removed.
	 */
	public void remove(Class<ExtensionListener> class1,	ExtensionListener listener);
	
}
