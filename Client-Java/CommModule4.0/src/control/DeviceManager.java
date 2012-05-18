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
package control;


import java.io.IOException;
import java.util.*;
import java.util.concurrent.Semaphore;

import javax.vecmath.Vector3d;

import kinectThreads.KinectPoseEnum;
import kinectThreads.KinectPoseManager;

import org.OpenNI.Point3D;
import org.OpenNI.SkeletonJoint;
import org.OpenNI.StatusException;
import org.wiigee.control.WiimoteWiigee;
import org.wiigee.device.Wiimote;


import launchers.*;

import IRGlancePackage.IRGlance;
import KinectPackage.KinectManager;
import KinectPackage.MotorStatus;

import NoninPackage.NoninData;
import NoninPackage.NoninManager;

import edu.unsw.cse.wiiboard.WiiBoard;
import edu.unsw.cse.wiiboard.WiiBoardDiscoverer;
import edu.unsw.cse.wiiboard.WiiBoardDiscoveryListener;

/**
 * A singleton class which has all necessary logic to offer the different kind
 * of gadget services.
 * 
 * @author Santiago Hors Fraile
 */
public class DeviceManager implements WiiBoardDiscoveryListener {

	/**
	 * This object is the only object that can exist of this class. This object
	 * has to be the one used to get the service launchers.
	 */
	static private DeviceManager dm = null;
	/**
	 * This object is the manager of the Kinect. It is the one which will do the
	 * Kinect connections and disconnections.
	 */
	KinectManager kinectManager = null;
	/**
	 * This object is the manager of the Nonin. It is the one which will do the
	 * Nonin connections and disconnections.
	 */
	NoninManager noninManager = null;
	/**
	 * This field counts the number of Nonins that have been connected. Due to
	 * the way in which this library has been developed, its value can only be 0
	 * or 1.
	 */
	int noninCounter = 0;
	/**
	 * This field counts the number of Kinects that have been connected. Due to
	 * the way in which this library has been developed, its value can only be 0
	 * or 1.
	 */
	int kinectCounter = 0;
	/**
	 * This field represents the discoverer of the WiiBoard.
	 */
	WiiBoardDiscoverer wbd = null;
	/**
	 * This field represents the WiiBoard itself.
	 */
	WiiBoard wiiBoard = null;
	/**
	 * This field counts the number of connected WiiBoards. Due to the way in
	 * which this library has been developed, its value can only be 0 or 1.
	 */
	int wiiBoardCounter = 0;
	/**
	 * This field maps the connected WiiMote objects. The X value represents the
	 * label of the WiiMote and the Y value the reference to the WiiMote object.
	 */
	Map<Integer, Wiimote> wiiMoteCreated = new HashMap<Integer, Wiimote>();
	/**
	 * This field maps the number of connected WiiMote. The X value represents
	 * the label of the WiiMote and the Y value the number of connected
	 * WiiMotes.
	 */
	Map<Integer, Integer> wiiMoteCounter = new HashMap<Integer, Integer>();
	/**
	 * This field represents the ipAddress of the socket that we are going to
	 * create to communicate with the server.
	 */
	private String ipAddress;
	/**
	 * This field represents the port of the server we are going to connect.
	 */
	private int port;
	
	/**
	 * This semamphore is used to ensure that we do not try to start up the Kinect in two places at the same time.
	 */
	private Semaphore sem ; 
	
	/**
	 * This field represents the maximum number of users that the Kinect can track.
	 */
	private int maximumNumberOfKinectUsers; 
	
	/**
	 * This static field allows handling with the Kinect pose detection manager.
	 */
	private static KinectPoseManager kinectPoseManager;
	
	
	/**
	 * 
	 * Returns a new instance of DeviceManager if it did not exist or the
	 * instance that was created if there was a created instance already.
	 * 
	 * @return DeviceManager
	 */
	static public DeviceManager getDeviceManager() {
		System.setProperty("bluecove.jsr82.psm_minimum_off", "true");

		if (dm == null) {
			dm = new DeviceManager();
		}
	
		return dm;
	}
	/**
	 * 
	 * Returns a new instance of DeviceManager if it did not exist or the
	 * instance that was created if there was a created instance already.
	 * 
	 * @param ipAddressParam
	 *            The string containing the ipAddress of the socket we want to
	 *            open. For example: "193.156.105.166"
	 * @param portParam
	 *            The port number of the IP address that we want to open the
	 *            socket. For example: 7540
	 * @param maximumNumberOfKinectusers The maximum number of users that the Kinect can track.
	 * @return DeviceManager
	 */
	static public DeviceManager getDeviceManager(String ipAddressParam,
			int portParam, int maximumNumberOfKinectUsers) {
		System.setProperty("bluecove.jsr82.psm_minimum_off", "true");
		if (dm == null) {
			dm = new DeviceManager();
		}
		dm.setIpAddress(ipAddressParam);
		dm.setPort(portParam);
		kinectPoseManager = new KinectPoseManager();
		SocketManager.getSocket(ipAddressParam, portParam);
		dm.setMaximumNumberOfKinectUsers(maximumNumberOfKinectUsers);
		return dm;
	}
	/**
	 * 
	 * Returns a new instance of DeviceManager if it did not exist or the
	 * instance that was created if there was a created instance already.
	 * It sets the maximum number of users that the Kinect can track to 1.
	 * 
	 * @param ipAddressParam
	 *            The string containing the ipAddress of the socket we want to
	 *            open. For example: "193.156.105.166"
	 * @param portParam
	 *            The port number of the IP address that we want to open the
	 *            socket. For example: 7540
	 * @return DeviceManager
	 */
	static public DeviceManager getDeviceManager(String ipAddressParam,
			int portParam) {
		System.setProperty("bluecove.jsr82.psm_minimum_off", "true");
		if (dm == null) {
			dm = new DeviceManager();
		}
		dm.setIpAddress(ipAddressParam);
		dm.setPort(portParam);
		kinectPoseManager = new KinectPoseManager();
		SocketManager.getSocket(ipAddressParam, portParam);
		dm.setMaximumNumberOfKinectUsers(1);
		return dm;
	}

	/**
	 * Returns a new Nonin-service launcher if there was not any NoninLauncher
	 * created yet or a reference to the created object if there was one created
	 * already. It throws an exception if the Nonin was not found when the
	 * DiscoveryListener probed the BlueTooth area. See the
	 * javadocNoninDiscoveryListener and NoninManager (both of them belongs to
	 * the NoninLibrary library)
	 * 
	 * @return The reference to the NoninLauncher object.
	 * @throws Exception
	 */
	public NoninLauncher getNoninLauncher(String noninMac) throws Exception { // We
																				// allow
																				// to
																				// exist
																				// only
																				// one
																				// Nonin
																				// gadget
		NoninLauncher nl = null;
		if (noninCounter == 0) {
			noninManager = new NoninManager(noninMac);
			noninManager.connect();

		}
		noninCounter++;
		nl = new NoninLauncher();

		noninManager.addListener(nl);

		return nl;
	}

	/**
	 * Returns a new WiiBoard-service launcher if there was not any
	 * WiiBoardLauncher created yet or a reference to the created object if
	 * there was one created already.
	 * 
	 * @return The reference to the WiiBoardLauncher object.
	 */
	public WiiBoardLauncher getWiiBoardLauncher(String boardMac) {// We allow to
																	// exist
																	// only one
																	// WiiBoard
																	// gadget
		WiiBoardLauncher wbl;
		if (wiiBoardCounter == 0) {
			wbd = WiiBoardDiscoverer.getWiiBoardDiscoverer(boardMac);
			wbd.addWiiBoardDiscoveryListener(this);
			wbd.startWiiBoardSearch();
			try {
				synchronized (this) {
					this.wait();
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		wbl = new WiiBoardLauncher();

		wiiBoardCounter++;
		return wbl;
	}

	/**
	 * Returns a new WiiMoteAccelerationLauncher from the WiiMote whose label is
	 * the one given as parameter and whose MAC is the one given as parameter.
	 * If there is not any WiiMote with the given label yet, a new WiiMote
	 * object is created. The connected WiiMote will turn on one LED, the one
	 * corresponding to the label (starting from left to right). If the label
	 * value is higher than four, a module 4 operation will be applied so that
	 * one LED will be lighted up too.
	 * 
	 * @param wiiMac
	 *            The MAC address that the WiiMote must have so that we avoid
	 *            connecting to any unwanted WiiMote nearby.
	 * @param label
	 *            The label that identifies one WiiMote from another.
	 * @return The reference to the WiiMoteAccelerationLauncher object.
	 */
	public WiiMoteAccelerationLauncher getWiiMoteAccelerationLauncher(
			String wiiMac, int label) {
		WiimoteWiigee wg = null;
		Wiimote wm = null;
		if (wiiMoteCreated.containsKey(new Integer(label))) {
			wm = wiiMoteCreated.get(new Integer(label));
		} else {
			wg = new WiimoteWiigee();

			try {

				wm = wg.getDeviceByMac(wiiMac);
				wiiMoteCreated.put(new Integer(label), wm);
				wm.setLED((label % 4 == 0) ? 4 : label % 4);
				if (wiiMoteCreated.containsKey(1)) {
					System.out.println("El derecho se ha metido bien");
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try {
			wiiMoteCounter.put(new Integer(label), new Integer(wiiMoteCounter
					.get(new Integer(label)).intValue() + 1));
		} catch (Exception e) {
			wiiMoteCounter.put(new Integer(label), new Integer(1));
		}
		WiiMoteAccelerationLauncher launcher = new WiiMoteAccelerationLauncher();
		wm.addAccelerationListener(launcher);
		return launcher;
	}

	/**
	 * Returns a new WiiMoteButtonsLauncher from the WiiMote whose label is the
	 * one given as parameter and whose MAC is the one given as parameter. If
	 * there is not any WiiMote with the given label yet, a new WiiMote object
	 * is created. The connected WiiMote will turn on one LED, the one
	 * corresponding to the label (starting from left to right). If the label
	 * value is higher than four, a module 4 operation will be applied so that
	 * one LED will be lighted up too.
	 * 
	 * @param wiiMac
	 *            The MAC address that the WiiMote must have so that we avoid
	 *            connecting to any unwanted WiiMote nearby.
	 * @param label
	 *            The label that identifies one WiiMote from another.
	 * @return The reference to the WiiMoteButtonsLauncher object.
	 */
	public WiiMoteButtonsLauncher getWiiMoteButtonsLauncher(String wiiMac,
			int label) {
		WiimoteWiigee wg = null;
		Wiimote wm = null;

		if (wiiMoteCreated.containsKey(new Integer(label))) {
			wm = wiiMoteCreated.get(new Integer(label));
		} else {
			wg = new WiimoteWiigee();
			try {
				wm = wg.getDeviceByMac(wiiMac);
				wiiMoteCreated.put(new Integer(label), wm);
				wm.setLED((label % 4 == 0) ? 4 : label % 4);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try {
			wiiMoteCounter.put(new Integer(label), new Integer(wiiMoteCounter
					.get(new Integer(label)).intValue() + 1));
		} catch (Exception e) {
			wiiMoteCounter.put(new Integer(label), new Integer(1));
		}
		WiiMoteButtonsLauncher launcher = new WiiMoteButtonsLauncher();
		wm.addButtonListener(launcher);
		return launcher;
	}

	/**
	 * Returns a new WiiMoteGestureLauncher from the WiiMote whose label is the
	 * one given as parameter and whose MAC is the one given as parameter. If
	 * there is not any WiiMote with the given label yet, a new WiiMote object
	 * is created. The connected WiiMote will turn on one LED, the one
	 * corresponding to the label (starting from left to right). If the label
	 * value is higher than four, a module 4 operation will be applied so that
	 * one LED will be lighted up too.
	 * 
	 * @param wiiMac
	 *            The MAC address that the WiiMote must have so that we avoid
	 *            connecting to any unwanted WiiMote nearby.
	 * @param label
	 *            The label that identifies one WiiMote from another.
	 * @return The reference to the WiiMoteButtonsLauncher object.
	 */
	public WiiMoteGestureLauncher getWiiMoteGesturesLauncher(String wiiMac,
			int label) {
		WiimoteWiigee wg = null;
		Wiimote wm = null;
		if (wiiMoteCreated.containsKey(new Integer(label))) {
			wm = wiiMoteCreated.get(new Integer(label));
		} else {
			wg = new WiimoteWiigee();
			try {
				wm = wg.getDeviceByMac(wiiMac);
				wiiMoteCreated.put(new Integer(label), wm);
				wm.setLED((label % 4 == 0) ? 4 : label % 4);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		wiiMoteCounter
				.put(new Integer(label),
						new Integer((wiiMoteCounter.get(new Integer(label)))
								.intValue() + 1));

		WiiMoteGestureLauncher launcher = new WiiMoteGestureLauncher();
		wm.addGestureListener(launcher);
		return launcher;
	}

	/**
	 * Returns a new WiiMoteIRGlanceLauncher from the WiiMote whose label is the
	 * one given as parameter and whose MAC is the one given as parameter. If
	 * there is not any WiiMote with the given label yet, a new WiiMote object
	 * is created. The connected WiiMote will turn on one LED, the one
	 * corresponding to the label (starting from left to right). If the label
	 * value is higher than four, a module 4 operation will be applied so that
	 * one LED will be lighted up too.
	 * 
	 * @param wiiMac
	 *            The MAC address that the WiiMote must have so that we avoid
	 *            connecting to any unwanted WiiMote nearby.
	 * @param label
	 *            The label that identifies one WiiMote from another.
	 * @return The reference to the WiiMoteButtonsLauncher object.
	 */
	public WiiMoteIRGlanceLauncher getWiiMoteIRGlanceLauncher(String wiiMac,
			int label) {

		WiimoteWiigee wg = null;
		Wiimote wm = null;
		IRGlance irglance;
		if (wiiMoteCreated.containsKey(new Integer(label))) {
			wm = wiiMoteCreated.get(new Integer(label));
		} else {
			wg = new WiimoteWiigee();
			try {
				wm = wg.getDeviceByMac(wiiMac);
				wiiMoteCreated.put(new Integer(label), wm);
				wm.setLED(label);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		wiiMoteCounter
				.put(new Integer(label),
						new Integer((wiiMoteCounter.get(new Integer(label)))
								.intValue() + 1));

		WiiMoteIRGlanceLauncher launcher = new WiiMoteIRGlanceLauncher();

		try {
			irglance = new IRGlance(1000);
			irglance.setEventFilter(3);

			irglance.addListener(launcher);
			wm.setInfraredCameraEnabled(true, Wiimote.IR_MODE_EXTENDED);
			wm.addInfraredListener(irglance);

		} catch (IOException e) {
			e.printStackTrace();
		}

		return launcher;
	}

	/**
	 * Returns a new WiiMoteIRLauncher from the WiiMote whose label is the one
	 * given as parameter and whose MAC is the one given as parameter. If there
	 * is not any WiiMote with the given label yet, a new WiiMote object is
	 * created. The connected WiiMote will turn on one LED, the one
	 * corresponding to the label (starting from left to right). If the label
	 * value is higher than four, a module 4 operation will be applied so that
	 * one LED will be lighted up too.
	 * 
	 * @param wiiMac
	 *            The MAC address that the WiiMote must have so that we avoid
	 *            connecting to any unwanted WiiMote nearby.
	 * @param label
	 *            The label that identifies one WiiMote from another.
	 * @return The reference to the WiiMoteIRLauncher object.
	 */
	public WiiMoteIRLauncher getWiiMoteIRLauncher(String wiiMac, int label) {
		WiimoteWiigee wg = null;
		Wiimote wm = null;
		if (wiiMoteCreated.containsKey(new Integer(label))) {
			wm = wiiMoteCreated.get(new Integer(label));
		} else {
			wg = new WiimoteWiigee();
			try {
				wm = wg.getDeviceByMac(wiiMac);
				wiiMoteCreated.put(new Integer(label), wm);
				wm.setLED((label % 4 == 0) ? 4 : label % 4);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		wiiMoteCounter
				.put(new Integer(label),
						new Integer((wiiMoteCounter.get(new Integer(label)))
								.intValue() + 1));
		WiiMoteIRLauncher launcher = new WiiMoteIRLauncher();
		wm.addInfraredListener(launcher);
		return launcher;
	}

	/**
	 * You have to call this function when you do not need one service of the
	 * Nonin any longer. This function subtracts one from the noninCounter. If
	 * noninCounter becomes 0, the Nonin will be disconnected.
	 * 
	 * @param noninLauncher
	 */
	public void dropNonin(NoninLauncher noninLauncher) {
		noninCounter--;
		noninManager.removeListener(noninLauncher);
		if (noninCounter == 0) {
			noninManager.disconnect();
		}
	}

	/**
	 * You have to call this function when you do not need one service of the
	 * WiiBoard any longer. This function subtracts one from the
	 * wiiBoardCounter. If wiiBoardCounter becomes 0, the WiiBoard will be
	 * disconnected.
	 * 
	 * @param wiiBoardLauncher
	 */
	public void dropWiiBoard(WiiBoardLauncher wiiBoardLauncher) {
		wiiBoardCounter--;
		wiiBoard.removeListener(wiiBoardLauncher);
		if (wiiBoardCounter == 0) {
			wiiBoard.cleanup();
		}

	}

	/**
	 * You have to call this function when you do not need one service of the
	 * WiiMote any longer. This function subtracts one from the value mapped by
	 * the label given as parameter in wiiMoteCounter map. If that value becomes
	 * 0, the WiiMote mapped by label in the wiiMoteCreated map will be
	 * disconnected.
	 * 
	 * @param label
	 *            The label that identifies one WiiMote from another.
	 */
	public void dropWiiMote(int label, LauncherWrapper launcher) {

		wiiMoteCounter
				.put(new Integer(label),
						new Integer((wiiMoteCounter.get(new Integer(label)))
								.intValue() - 1));
		if (wiiMoteCounter.get(new Integer(label)).intValue() == 0) {
			Wiimote wm = wiiMoteCreated.get(new Integer(label));
			wm.removeDataListener(launcher);
			wm.disconnect();
			wiiMoteCreated.put(new Integer(label), null);

		}
	}

	/**
	 * Notifies all waiting threads of this class when a WiiBoard is discovered.
	 * 
	 * @param wb
	 *            The discovered WiiBoard.
	 */
	public void wiiBoardDiscovered(WiiBoard wb) {
		this.wiiBoard = wb;
		System.out.println("wb = " + wb);
		synchronized (this) {
			notifyAll();
		}
	}

	/**
	 * Connects the Nunchuk to the WiiMote whose label is the one given as a
	 * parameter.
	 * 
	 * @param label
	 *            The label that identifies one WiiMote from another.
	 */
	public void setNunchukEnabled(int label) {
		WiimoteWiigee wg = null;
		Wiimote wm = null;
		if (wiiMoteCreated.containsKey(new Integer(label))) {
			wm = wiiMoteCreated.get(new Integer(label));
		} else {
			wg = new WiimoteWiigee();
			try {
				wm = wg.getDevice();
				wiiMoteCreated.put(new Integer(label), wm);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		wm.connectNunchuk();

	}

	/**
	 * Disconnects the Nunchuk from the WiiMote whose label is the one given as
	 * a parameter.
	 * 
	 * @param label
	 *            The label that identifies one WiiMote from another.
	 */
	public void setNunchukDisabled(int label) {
		Wiimote wm = null;
		if (wiiMoteCreated.containsKey(new Integer(label))) {
			wm = wiiMoteCreated.get(new Integer(label));
			wm.disconnectNunchuk();
		} else {

		}

	}

	/**
	 * Connects the WiiMotionPlus to the WiiMote whose label is the one given as
	 * a parameter.
	 * 
	 * @param label
	 *            The label that identifies one WiiMote from another.
	 */
	public void setWiiMotionPlusEnabled(int label) {
		WiimoteWiigee wg = null;
		Wiimote wm = null;
		if (wiiMoteCreated.containsKey(new Integer(label))) {
			wm = wiiMoteCreated.get(new Integer(label));
		} else {
			wg = new WiimoteWiigee();
			try {
				wm = wg.getDevice();
				wiiMoteCreated.put(new Integer(label), wm);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		wm.connectWiiMotionPlus();

	}

	/**
	 * Disconnects the WiiMotionPlus from the WiiMote whose label is the one
	 * given as a parameter.
	 * 
	 * @param label
	 *            The label that identifies one WiiMote from another.
	 */
	public void setWiiMotionPlusDisabled(int label) {
		Wiimote wm = null;
		if (wiiMoteCreated.containsKey(new Integer(label))) {
			wm = wiiMoteCreated.get(new Integer(label));
			wm.disconnectWiiMotionPlus();
		} else {

		}

	}

	/**
	 * Returns a new NunchukAnalogStickLauncher from the WiiMote whose label is
	 * the one given as parameter. If there is not any WiiMote with the given
	 * label yet, a new WiiMote object is created.
	 * 
	 * @param label
	 *            The label that identifies one WiiMote from another.
	 * @return NunchukAnalogStickLaucher
	 */
	public NunchukAnalogStickLauncher getNunchukAnalogStickLauncher(int label) {
		WiimoteWiigee wg = null;
		Wiimote wm = null;
		if (wiiMoteCreated.containsKey(new Integer(label))) {
			wm = wiiMoteCreated.get(new Integer(label));
		} else {
			wg = new WiimoteWiigee();
			try {
				wm = wg.getDevice();
				wiiMoteCreated.put(new Integer(label), wm);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		wiiMoteCounter
				.put(new Integer(label),
						new Integer((wiiMoteCounter.get(new Integer(label)))
								.intValue() + 1));
		wm.connectNunchuk();
		NunchukAnalogStickLauncher launcher = new NunchukAnalogStickLauncher();
		wm.addAnalogStickListener(launcher);
		return launcher;
	}

	/**
	 * Returns a new NunchukAccelerationLauncher from the WiiMote whose label is
	 * the one given as parameter. If there is not any WiiMote with the given
	 * label yet, a new WiiMote object is created.
	 * 
	 * @param label
	 *            The label that identifies one WiiMote from another.
	 * @return NunchukAccelerationLauncher
	 */
	public NunchukAccelerationLauncher getNunchukAccelerationLauncher(int label) {
		WiimoteWiigee wg = null;
		Wiimote wm = null;
		if (wiiMoteCreated.containsKey(new Integer(label))) {
			wm = wiiMoteCreated.get(new Integer(label));
		} else {
			wg = new WiimoteWiigee();
			try {
				wm = wg.getDevice();
				wiiMoteCreated.put(new Integer(label), wm);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		wiiMoteCounter
				.put(new Integer(label),
						new Integer((wiiMoteCounter.get(new Integer(label)))
								.intValue() + 1));
		wm.connectNunchuk();

		NunchukAccelerationLauncher launcher = new NunchukAccelerationLauncher();
		wm.addNunchukAccelerometerListener(launcher);
		return launcher;
	}

	/**
	 * Returns a new NunchukButtonsLauncher from the WiiMote whose label is the
	 * one given as parameter. If there is not any WiiMote with the given label
	 * yet, a new WiiMote object is created.
	 * 
	 * @param label
	 *            The label that identifies one WiiMote from another.
	 * @return NunchukButtonsLauncher
	 */
	public NunchukButtonsLauncher getNunchukButtonsLauncher(int label) {
		WiimoteWiigee wg = null;
		Wiimote wm = null;
		if (wiiMoteCreated.containsKey(new Integer(label))) {
			wm = wiiMoteCreated.get(new Integer(label));
		} else {
			wg = new WiimoteWiigee();
			try {
				wm = wg.getDevice();
				wiiMoteCreated.put(new Integer(label), wm);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		wiiMoteCounter
				.put(new Integer(label),
						new Integer((wiiMoteCounter.get(new Integer(label)))
								.intValue() + 1));
		wm.connectNunchuk();
		NunchukButtonsLauncher launcher = new NunchukButtonsLauncher();
		wm.addNunchukButtonListener(launcher);
		return launcher;
	}

	/**
	 * Returns a new WiiMoteRotationLauncher from the WiiMote whose label is the
	 * one given as parameter. If there is not any WiiMote with the given label
	 * yet, a new WiiMote object is created.
	 * 
	 * @param label
	 *            The label that identifies one WiiMote from another.
	 * @return WiiMoteRotationLauncher
	 */
	public WiiMoteRotationLauncher getWiiMoteRotationLauncher(int label) {
		WiimoteWiigee wg = null;
		Wiimote wm = null;

		if (wiiMoteCreated.containsKey(new Integer(label))) {
			wm = wiiMoteCreated.get(new Integer(label));
		} else {
			wg = new WiimoteWiigee();
			try {
				wm = wg.getDevice();
				wiiMoteCreated.put(new Integer(label), wm);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		wm.connectWiiMotionPlus();

		wiiMoteCounter
				.put(new Integer(label),
						new Integer((wiiMoteCounter.get(new Integer(label)))
								.intValue() + 1));
		WiiMoteRotationLauncher launcher = new WiiMoteRotationLauncher();
		// wm.addWiiMotionPlusRotationListener(launcher);
		wm.getWiiMotionPlus().addRotationListener(launcher);
		return launcher;
	}

	/**
	 * Gets the current pulse.
	 * 
	 * @return int The current pulse.
	 */
	public int getPulse() {
		return noninManager.getPulse();
	}

	/**
	 * Gets the current oxygen value.
	 * 
	 * @return int The current oxygen value.
	 */
	public int getOxy() {
		return noninManager.getOxy();
	}

	/**
	 * Gets the current Nonin data.
	 * 
	 * @return NoninData The current Nonin data.
	 */
	public NoninData getData() {
		return noninManager.getData();
	}

	/**
	 * Resets the calibration of the WiiMote whose ID label is specified by the
	 * parameter.
	 * 
	 * @param label
	 *            The identification label of the Wiimote we want reset its
	 *            calibration.
	 */
	public void resetWiimoteCalibration(int label) {
		WiimoteWiigee wg = null;
		Wiimote wm = null;
		if (wiiMoteCreated.containsKey(new Integer(label))) {
			wm = wiiMoteCreated.get(new Integer(label));
		} else {
			wg = new WiimoteWiigee();
			try {
				wm = wg.getDevice();
				wiiMoteCreated.put(new Integer(label), wm);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		wm.getWiiMotionPlus().resetRotationFilters();
	}

	/**
	 * Gets the Wiimote object of the WiiMote whose ID label matches the
	 * parameter.
	 * 
	 * @param label
	 *            The ID label of the Wiimote we want to get.
	 * @return the Wiimote object of the WiiMote whose ID label matches the
	 *         parameter.
	 */
	public Wiimote getWiimoteByLabel(int label) {
		return wiiMoteCreated.get(label);

	}

	/**
	 * Gets a key-value map of the WiiMotes that have been created.
	 * 
	 * @return The key-value map of the WiiMotes that have been created.
	 */
	public Map<Integer, Wiimote> getWiiMoteCreated() {
		return this.wiiMoteCreated;

	}

	private DeviceManager() {
		super();
		sem = new Semaphore(1,true);
	}

	/**
	 * Returns a new KinectSkeletonLauncher for the given user. The
	 * KinectSkeletonLauncher will enable us to register listeners which will be
	 * notified about joint-related events. This function connects the Kinect
	 * device if it was not connected already.
	 * 
	 * @param userId
	 *            The user we want to track.
	 * @return The reference to the KinectSkeletonLauncher object.
	 * @throws Exception
	 */
	public KinectSkeletonLauncher getKinectSkeletonLauncher(int userId) { // We
																			// allow
																			// to
		// exist only
		// one Kinect
		// device

		KinectSkeletonLauncher kl = null;
		
		kinectStartUpKinectIfNeeded();
		
		dm.waitForUserIsCalibrated(userId);
		kinectCounter++;

		kl = new KinectSkeletonLauncher(userId, true);
	
		
		kinectManager.addListener(kl);
	
		return kl;
	}

	/**
	 * Returns a new KinectMotorLauncher. The KinectMotorLauncher will enable us
	 * to register listeners which will be notified about motor-related events.
	 * This function connects the Kinect device if it was not connected already.
	 * 
	 * @return The reference to the KinectMotorLauncher object.
	 * @throws Exception
	 */
	public KinectMotorLauncher getKinectMotorLauncher() { // We allow to exist
															// only one Kinect
															// device

		KinectMotorLauncher kl = null;
		kinectStartUpKinectIfNeeded();
		kinectCounter++;
		kl = new KinectMotorLauncher();

		kinectManager.addListener(kl);

		return kl;
	}

	/**
	 * You have to call this function when you do not need one service of the
	 * Kinect any longer. This function subtracts one from the kinectCounter. If
	 * kinectCounter becomes 0, the kinectCounter will be disconnected.
	 * 
	 * @param kinectLauncher
	 */
	public void dropKinect(LauncherWrapper kinectLauncher) {
		
		if(kinectLauncher instanceof KinectPoseLauncher){
			kinectManager.removeListener(((KinectPoseLauncher)kinectLauncher).getPrivateKinectSkeletonLauncher());
			((KinectPoseLauncher)kinectLauncher).getPrivateKinectSkeletonLauncher().dropService();
		}else 	if(kinectLauncher instanceof KinectUserMovementLauncher){
			kinectManager.removeListener(((KinectUserMovementLauncher)kinectLauncher).getKinectPoseLauncherWalkLeftLegUp().getPrivateKinectSkeletonLauncher());
			kinectManager.removeListener(((KinectUserMovementLauncher)kinectLauncher).getKinectPoseLauncherWalkRightLegUp().getPrivateKinectSkeletonLauncher());
			kinectManager.removeListener(((KinectUserMovementLauncher)kinectLauncher).getKinectPoseLauncherStand().getPrivateKinectSkeletonLauncher());
			kinectManager.removeListener(((KinectUserMovementLauncher)kinectLauncher).getKinectPoseLauncherHandsBack().getPrivateKinectSkeletonLauncher());
			kinectManager.removeListener(((KinectUserMovementLauncher)kinectLauncher).getKinectPoseLauncherRisedLeftArm().getPrivateKinectSkeletonLauncher());
			kinectManager.removeListener(((KinectUserMovementLauncher)kinectLauncher).getKinectPoseLauncherRisedRightArm().getPrivateKinectSkeletonLauncher());	
			((KinectUserMovementLauncher)kinectLauncher).getKinectPoseLauncherWalkLeftLegUp().dropService();
			((KinectUserMovementLauncher)kinectLauncher).getKinectPoseLauncherWalkRightLegUp().dropService();
			((KinectUserMovementLauncher)kinectLauncher).getKinectPoseLauncherStand().dropService();
			((KinectUserMovementLauncher)kinectLauncher).getKinectPoseLauncherHandsBack().dropService();
			((KinectUserMovementLauncher)kinectLauncher).getKinectPoseLauncherRisedLeftArm().dropService();
			((KinectUserMovementLauncher)kinectLauncher).getKinectPoseLauncherRisedRightArm().dropService();
		} else if(kinectLauncher instanceof KinectUserHugLauncher){
			kinectManager.removeListener(((KinectUserHugLauncher)kinectLauncher).getKinectPoseLauncherOpenedHug().getPrivateKinectSkeletonLauncher());
			kinectManager.removeListener(((KinectUserHugLauncher)kinectLauncher).getKinectPoseLauncherClosedHug().getPrivateKinectSkeletonLauncher());
			((KinectUserHugLauncher)kinectLauncher).getKinectPoseLauncherOpenedHug().dropService();
			((KinectUserHugLauncher)kinectLauncher).getKinectPoseLauncherClosedHug().dropService();
		} else {
			//Nothing
		}
		if (kinectCounter == 0) {
			kinectManager.disconnect();
		}
		
	}

	/**
	 * Gets the number of nonins which are currently being used.
	 * 
	 * @return The number of Nonins which are currently being used.
	 */
	public int getNoninCounter() {
		return noninCounter;
	}

	/**
	 * Gets the number of WiiBoards which are currently being used.
	 * 
	 * @return The number of WiiBoards which are currently being used.
	 */
	public int getWiiBoardCounter() {
		return wiiBoardCounter;
	}

	/**
	 * Gets the number of Kinects which are currently being used.
	 * 
	 * @return The number of Kinects which are currently being used.
	 */
	public int getKinectCounter() {
		return kinectCounter;
	}

	/**
	 * Gets the KinectManager object.
	 * 
	 * @return The object to manage the Kinect device.
	 */
	public KinectManager getKinectManager() {
		return kinectManager;
	}

	/**
	 * Returns a new KinectAbsoluteSpaceForATimeLauncher. The
	 * KinectAbsoluteSpaceForATimeLauncher will enable us to register listeners
	 * which will be notified about the space that exists between the initial
	 * and final position of the given joint of the given user when the given
	 * time expires. This function connects the Kinect device if it was not
	 * connected already.
	 * 
	 * @param userId
	 *            The user we want to track.
	 * @param joint
	 *            The joint of the user that we want to track.
	 * @param time
	 *            The time we want to leave between the initial and final
	 *            position of the user's joint.
	 * @return The KinectAbsoluteSpaceForATimeLauncher created to register
	 *         listeners.
	 */
	public KinectAbsoluteSpaceForATimeLauncher getKinectAbsoluteSpaceForATimeLauncher(
			int userId, SkeletonJoint joint, long time) {

		KinectAbsoluteSpaceForATimeLauncher kasfatl = null;
		kinectStartUpKinectIfNeeded();
		kinectCounter++;
		waitForUserIsCalibrated(userId);
		waitForJointReady(userId, joint);
		kasfatl = new KinectAbsoluteSpaceForATimeLauncher(userId, joint, time);
		return kasfatl;
	}

	/**
	 * Returns a new KinectTotalSpaceTravelledForATimeLauncher. The
	 * KinectTotalSpaceTravelledForATimeLauncher will enable us to register
	 * listeners which will be notified about the space that the given user's
	 * joint has gone over throughout the given time. This function connects the
	 * Kinect device if it was not connected already.
	 * 
	 * @param userId
	 *            The user we want to track.
	 * @param joint
	 *            The joint of the user that we want to track.
	 * @param time
	 *            The time throughout which we are going to count the distance
	 *            that the user's joint goes over.
	 * @return The KinectTotalSpaceTravelledForATimeLauncher created to register
	 *         listeners.
	 */
	public KinectTotalSpaceTravelledForATimeLauncher getKinectTotalSpaceTravelledForATimeLauncher(
			int userId, SkeletonJoint joint, long time) {
		KinectTotalSpaceTravelledForATimeLauncher ktstfat = null;
		kinectStartUpKinectIfNeeded();
		kinectCounter++;
		waitForUserIsCalibrated(userId);
		waitForJointReady(userId, joint);

		ktstfat = new KinectTotalSpaceTravelledForATimeLauncher(userId, joint,
				time);
		return ktstfat;

	}

	/**
	 * Returns a new KinectUserJointReachPointLauncher. The
	 * KinectUserJointReachPointLauncher will enable us to register listeners
	 * which will be notified when the given user's joint reaches the virtual
	 * sphere whose center and radius are given as parameters. This function
	 * connects the Kinect device if it was not connected already.
	 * 
	 * @param userId
	 *            The user we want to track.
	 * @param joint
	 *            The joint of the user that we want to track.
	 * @param sphereCenter
	 *            The center of the virtual sphere which the user has to reach
	 *            with his/her joint.
	 * @param radius
	 *            The radius of the sphere.
	 * @return The KinectUserJointReachPointLauncher created to register
	 *         listeners.
	 */

	public KinectUserJointReachPointLauncher getKinectUserJointReachPointLauncher(
			int userId, SkeletonJoint joint, Point3D sphereCenter,
			Point3D radius) {
		KinectUserJointReachPointLauncher kujrpl = null;
		kinectStartUpKinectIfNeeded();
		kinectCounter++;
		waitForUserIsCalibrated(userId);
		kujrpl = new KinectUserJointReachPointLauncher(userId, joint,
				sphereCenter, radius);
		kinectManager.addListener(kujrpl);
		return kujrpl;
	}

	/**
	 * Performs an active waiting (stops the execution of the thread) until the
	 * number of of users is higher than 0. If the parameter is a higher than 0
	 * value, then the function waits until the skeleton of that user is
	 * calibrated. This function connects the Kinect device if it was not
	 * connected already.
	 * 
	 * @param userId
	 *            The user we want to make sure his or her skeleton is
	 *            calibrated before continuing. If it is value is lower than
	 *            zero, the function will only waits until any user is detected
	 *            (but it will not wait for his or her skeleton calibration).
	 */
	public void waitForUserIsCalibrated(int userId) {
		
		kinectStartUpKinectIfNeeded();
		while (kinectManager.getUserGenerator().getNumberOfUsers() <= 0) {
			// Active wait for an user
		}
		if (userId != -1) {
			try {
				while (!kinectManager.getUserGenerator().getSkeletonCapability().isSkeletonCalibrated(userId)) {
					// Active wait for the calibration of the user's skeleton
				}
			} catch (StatusException e) {
				e.printStackTrace();
			}
		}
		
	}

	/**
	 * Performs an active waiting (stops the execution of the thread) until the
	 * number of of users is higher than 0.
	 * 
	 * @param userId
	 *            The user we want to make sure his or her skeleton is detected
	 *            before continuing.
	 */
	public void waitForUserIsDetected(int userId) {
		kinectStartUpKinectIfNeeded();
		while (kinectManager.getUserGenerator().getNumberOfUsers() <= 0) {
			// Active wait for an user
		}

	}

	/**
	 * Performs and active waiting (stops the execution of the thread) until the
	 * given joint of the given user is ready (its value will be different than
	 * zero).
	 * 
	 * @param userId
	 *            The user whose his or her joint we want to make sure that is
	 *            ready before continuing.
	 * @param joint
	 *            The joint of the user we want to make sure that is ready
	 *            before continuing.
	 */
	public void waitForJointReady(int userId, SkeletonJoint joint) {
		Vector3d currentPoint = kinectManager.getSkeletonManager().getJoint3D(
				userId, joint);
		while (currentPoint.getX() == 0.0 && currentPoint.getY() == 0.0
				&& currentPoint.getZ() == 0.0) {
			currentPoint = kinectManager.getSkeletonManager().getJoint3D(
					userId, joint);
		}
	}

	/**
	 * Tilts up or down the Kinect so that it can have the user's torso as
	 * closer to the center of the camera viewframe as possible.
	 * 
	 * @return Returns true if the Kinect has been tilted so that the player can
	 *         be seen correctly. False otherwise.
	 */
	public double adjustKinectForTheBestTilt(int userId) {
		boolean notAtLimit = true;
		double accuracyPercentage = 0.0;
		// For a Y resolution of 480, accuracy won't be higher than 240, so,
		// we initialise it to a high value that
		// we know we will never reach.

		double accuracy = 481;
		try {

			waitForUserIsCalibrated(userId);

			int angle = 0;

			while (kinectManager.getMotorCommunicator().getStatus()
					.compareTo(MotorStatus.MOVING) == 0) {

			}
			angle = kinectManager.getMotorCommunicator().getAngle();

			Point3D massCenter = kinectManager
					.getDepthGenerator()
					.convertRealWorldToProjective(
							kinectManager.getUserGenerator().getUserCoM(userId));

			double middlePointY = kinectManager.getYResolution() / 2;

			// 12 is the 5% of 240. Therefore, if we get a tilt with an accuracy
			// of 12 or less we will stop

			while (accuracy > 12 && notAtLimit) {

				if (kinectManager.getMotorCommunicator().getStatus()
						.compareTo(MotorStatus.MOVING) != 0) {

					if (massCenter.getY() - middlePointY > 0) {// tilt down
						angle -= 1;
						// The user is too far (or too low), the camera has
						// tilted down at maximum but it is still unable to
						// match the user's torso to its middle point properly.
						if (angle < -31) {
							notAtLimit = false;
							angle = -31;
						}

					} else if (massCenter.getY() - middlePointY < 0) {// tilt up
						angle += 1;
						// The user is too close, the camera has tilted up at
						// maximum but it is still unable to match the user's
						// torso to its middle point properly.
						if (angle > 31) {
							notAtLimit = false;
							angle = 31;
						}

					} else {
						// Nothing, it should never enter here
					}
					kinectManager.getMotorCommunicator().setAngle(angle);
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}
					try {
						// Update massCenter
						massCenter = kinectManager.getDepthGenerator()
								.convertRealWorldToProjective(
										kinectManager.getUserGenerator()
												.getUserCoM(userId));
					} catch (Exception e) {

					}
					// Update accuracy
					accuracy = Math.abs(massCenter.getY() - middlePointY);
					accuracyPercentage = 100 - accuracy * 5 / 12;
					// System.out.println("+++++++ Accuracy: "+accuracyPercentage+"% +++++++");
				}
			}

		} catch (StatusException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return accuracyPercentage;
	}

	/**
	 * @return the port
	 */
	public int getPort() {
		return port;
	}

	/**
	 * @param port
	 *            the port to set
	 */
	public void setPort(int port) {
		this.port = port;
	}

	/**
	 * @return the ipAddress
	 */
	public String getIpAddress() {
		return ipAddress;
	}

	/**
	 * @param ipAddress
	 *            the ipAddress to set
	 */
	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	
	/**
	 * Starts the Kinect if it has not been started yet.
	 */
	private void kinectStartUpKinectIfNeeded(){
		try {
			sem.acquire();
			if (kinectManager == null) {
				kinectManager = new KinectManager(maximumNumberOfKinectUsers);
				try {
					kinectManager.connect();
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
			sem.release();
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
	}
/**
 * 
 * @param userId The ID label of the user we want to detect the pose to.
 * @param kinectPose The KinectPose that we want to be detected.
 * @return A KinectPoseLauncher which will notify its listeners when the user with ID label userId performs the pose kinectPose. 
 */
	public KinectPoseLauncher getKinectPoseLauncher(int userId, KinectPoseEnum kinectPose) { // We
		// allow
		// to
		// exist only
		// one Kinect
		// device

		KinectPoseLauncher kpl = null;
		
		kinectStartUpKinectIfNeeded();

		dm.waitForUserIsCalibrated(userId);
		kinectCounter++;

		kpl = new KinectPoseLauncher(userId, kinectPose);
	
	
		
		
		try {
			
			kpl.setPrivateKinectSkeletonLauncher(new KinectSkeletonLauncher(userId, false));
			
			kinectManager.addListener(kpl.getPrivateKinectSkeletonLauncher());
			
			kpl.getPrivateKinectSkeletonLauncher().addListener(kinectPoseManager);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		kinectPoseManager.addListener(kpl);
		
		return kpl;
	}
	/**
	 * 
	 * @param userId The ID label of the user we want to detect the pose to.
	 * @return A KinectUserMovementLauncher which will notify its listeners when the user with ID label userId performs any pose related with the movement of the user. 
	 */
		public KinectUserMovementLauncher getKinectUserMovementLauncher(int userId) { // We
			// allow
			// to
			// exist only
			// one Kinect
			// device

			KinectUserMovementLauncher kuml = null;
			
			kinectStartUpKinectIfNeeded();

			dm.waitForUserIsCalibrated(userId);
			

			kuml = new KinectUserMovementLauncher(userId);
		
			
			return kuml;
		}
		
		
		/**
		 * 
		 * @param userId The ID label of the user we want to detect the pose to.
		 * @return A KinectUserMovementLauncher which will notify its listeners when the user with ID label userId performs a hug. 
		 */
			public KinectUserHugLauncher getKinectUserHugLauncher(int userId) { // We
				// allow
				// to
				// exist only
				// one Kinect
				// device

				KinectUserHugLauncher kuhl = null;
				
				kinectStartUpKinectIfNeeded();

				dm.waitForUserIsCalibrated(userId);
				

				kuhl = new KinectUserHugLauncher(userId);
			
				
				return kuhl;
			}
			
	/**
	 * @return the maximumNumberOfKinectUsers
	 */
	public int getMaximumNumberOfKinectUsers() {
		return maximumNumberOfKinectUsers;
	}

	/**
	 * @param maximumNumberOfKinectUsers the maximumNumberOfKinectUsers to set
	 */
	public void setMaximumNumberOfKinectUsers(int maximumNumberOfKinectUsers) {
		this.maximumNumberOfKinectUsers = maximumNumberOfKinectUsers;
	}

}
