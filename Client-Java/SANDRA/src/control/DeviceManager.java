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
	
	
	/*
	 * 
	 * The createdXXXXLaunchers represent the launchers for each type of information that can be reported. 
	 * We store their reference here so that we can use DeviceManager to add and remove functionalities in run-time. 
	 */
	
	private Map<Integer, WiiMoteAccelerationLauncher> createdWiiMoteAccelerationLauncher;
	private Map<Integer, WiiMoteButtonsLauncher> createdWiiMoteButtonsLauncher ;
	private Map<Integer, WiiMoteRotationLauncher> createdWiiMoteRotationLauncher ;
	private Map<Integer, WiiMoteIRLauncher> createdWiiMoteIRLauncher;
	private Map<Integer, WiiMoteIRGlanceLauncher> createdWiiMoteIRGlanceLauncher ;
	private Map<Integer, NunchukAccelerationLauncher> createdNunchukAccelerationLauncher;
	private Map<Integer, NunchukButtonsLauncher> createdNunchukButtonsLauncher ;
	private Map<Integer, NunchukAnalogStickLauncher> createdNunchukAnalogStickLauncher ;
	private WiiBoardLauncher createdWiiBoardLauncher;
	private NoninLauncher createdNoninLauncher;
	private KinectSkeletonLauncher createdKinectSkeletonLauncher;
	private KinectMotorLauncher createdKinectMotorLauncher;
	private KinectPoseLauncher createdKinectPoseLauncher;
	private KinectUserMovementLauncher createdKinectUserMovementLauncher;
	private KinectUserHugLauncher createdKinectUserHugLauncher;
	private KinectUserGameControlLauncher createdKinectUserGameControlLauncher;
	private KinectUserPickedUpFromSidesLauncher createdKinectUserPickedUpFromSidesLauncher;
	private KinectUserOutOfScopeLauncher createdKinectUserOutOfScopeLauncher;
	private KinectUserReachWithBothHandsLauncher createdKinectUserReachWithBothHandsLauncher;
	
	
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
	 * @param noninMac The MAC address of the Nonin we want to connect to. If "", it connects to the first Nonin it sees available.
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
		
		if (noninCounter == 0) {
			noninManager = new NoninManager(noninMac);
			noninManager.connect();

		}
		noninCounter++;
		createdNoninLauncher = new NoninLauncher();

		noninManager.addListener(createdNoninLauncher);
		
		return createdNoninLauncher;
	}
	/**
	 * Returns a new WiiBoard-service launcher if there was not any
	 * WiiBoardLauncher created yet or a reference to the created object if
	 * there was one created already.
	 * 
	 * @param macBoard The MAC address of the wiiboard we want to connect to. If "", it connects to the first wiiboard it sees available.
	 * @return The reference to the WiiBoardLauncher object.
	 */
	public WiiBoardLauncher getWiiBoardLauncher(String macBoard) {// We allow to
																	// exist
																	// only one
																	// WiiBoard
																	// gadget
		
		
		if (wiiBoardCounter == 0) {
			wbd = WiiBoardDiscoverer.getWiiBoardDiscoverer(macBoard);
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
		createdWiiBoardLauncher = new WiiBoardLauncher();
		
		wiiBoardCounter++;
		return createdWiiBoardLauncher;
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
	 *            connecting to any unwanted WiiMote nearby. If "", it connects to the 
	 *            first WiiMote it sees.
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
				if(wiiMac==""){
					wm= wg.getDevice();
				}else{
					wm = wg.getDeviceByMac(wiiMac);
				}
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
		createdWiiMoteAccelerationLauncher.put(label, launcher);
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
	 *            connecting to any unwanted WiiMote nearby. If "", it connects to the 
	 *            first WiiMote it sees.
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
				if(wiiMac==""){
					wm= wg.getDevice();
				}else{
					wm = wg.getDeviceByMac(wiiMac);
				}
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
		createdWiiMoteButtonsLauncher.put(label, launcher);
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
	 *            connecting to any unwanted WiiMote nearby. If "", it connects to the 
	 *            first WiiMote it sees.
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
				if(wiiMac==""){
					wm= wg.getDevice();
				}else{
					wm = wg.getDeviceByMac(wiiMac);
				}
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
	 *            connecting to any unwanted WiiMote nearby. If "", it connects to the 
	 *            first WiiMote it sees.
	 * @param label
	 *            The label that identifies one WiiMote from another.
	 * @return The reference to the WiiMoteButtonsLauncher object.
	 */
	public WiiMoteIRGlanceLauncher getWiiMoteIRGlanceLauncher(String wiiMac,int label) {

		WiimoteWiigee wg = null;
		Wiimote wm = null;
		IRGlance irglance;
		if (wiiMoteCreated.containsKey(new Integer(label))) {
			wm = wiiMoteCreated.get(new Integer(label));
		} else {
			wg = new WiimoteWiigee();
			try {
				if(wiiMac==""){
					wm= wg.getDevice();
				}else{
					wm = wg.getDeviceByMac(wiiMac);
				}
				wiiMoteCreated.put(new Integer(label), wm);
				wm.setLED(label);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		wiiMoteCounter.put(new Integer(label),	new Integer((wiiMoteCounter.get(new Integer(label))).intValue() + 1));

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
		createdWiiMoteIRGlanceLauncher.put(label,launcher);
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
	 *            connecting to any unwanted WiiMote nearby. If "", it connects to the 
	 *            first WiiMote it sees.
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
				if(wiiMac==""){
					wm= wg.getDevice();
				}else{
					wm = wg.getDeviceByMac(wiiMac);
				}
				wiiMoteCreated.put(new Integer(label), wm);
				wm.setLED((label % 4 == 0) ? 4 : label % 4);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		wiiMoteCounter.put(new Integer(label),new Integer((wiiMoteCounter.get(new Integer(label))).intValue() + 1));
		WiiMoteIRLauncher launcher = new WiiMoteIRLauncher();
		wm.addInfraredListener(launcher);
		createdWiiMoteIRLauncher.put(label,launcher);
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
			createdNoninLauncher=null;
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
		createdWiiBoardLauncher=null;
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
		if(launcher instanceof WiiMoteAccelerationLauncher){
			createdWiiMoteAccelerationLauncher.remove(label);
		}else if(launcher instanceof WiiMoteButtonsLauncher){
			createdWiiMoteButtonsLauncher.remove(label);
		}else if(launcher instanceof WiiMoteRotationLauncher){
			createdWiiMoteRotationLauncher.remove(label);
		}else if(launcher instanceof WiiMoteIRLauncher){
			createdWiiMoteIRLauncher.remove(label);
		}else if(launcher instanceof WiiMoteIRGlanceLauncher){
			createdWiiMoteIRGlanceLauncher.remove(label);
		}
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
	 * You have to call this function when you do not need one service of the
	 * Nunchuk any longer. 
	 * 
	 * @param label
	 *            The label that identifies one WiiMote from another.
	 * @param launcher The AccelerationLauncher, NunchukButtonsLauncher or NunchukAnalogStickLauncher that you no longer need.
	 */
	public void dropNunchuk(int label, LauncherWrapper launcher) {

		if(launcher instanceof NunchukAccelerationLauncher){
			createdNunchukAccelerationLauncher.remove(label);
		}else if(launcher instanceof NunchukButtonsLauncher){
			createdNunchukButtonsLauncher.remove(label);
		}else if(launcher instanceof NunchukAnalogStickLauncher){
			createdNunchukAnalogStickLauncher.remove(label);
		}
		if(createdNunchukAccelerationLauncher.get(label)==null&&createdNunchukButtonsLauncher.get(label)==null&&createdNunchukAnalogStickLauncher.get(label)==null){
			wiiMoteCreated.get(label).disconnectNunchuk();
		}
	}
	/**
	 * Notifies all waiting threads of this class when a WiiBoard is discovered.
	 * 
	 * @param wb
	 *            The discovered WiiBoard.
	 */
	@Override
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
		createdWiiMoteRotationLauncher.put(label, launcher);
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
	/**
	 * Default constructor
	 */
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
	public KinectSkeletonLauncher getKinectSkeletonLauncher(int userId) {
		kinectStartUpKinectIfNeeded();
		dm.waitForUserIsCalibrated(userId);
		kinectCounter++;
		createdKinectSkeletonLauncher = new KinectSkeletonLauncher(userId, true);
		kinectManager.addKinectDataListener(createdKinectSkeletonLauncher);
		return createdKinectSkeletonLauncher;
	}
	/**
	 * Returns a new KinectUserOutOfScopeLauncher for the given user. The
	 * KinectUserOutOfScopeLauncher will enable us to register listeners which will be
	 * notified about outOfScope-related events. This function connects the Kinect
	 * device if it was not connected already.
	 * 
	 * @param userId
	 *            The user we want to track. It is only useful if the Kinect wasn't running yet.
	 * @return The reference to the KinectUserOutOfScopeLauncher object.
	 * @throws Exception
	 */
	public KinectUserOutOfScopeLauncher getKinectUserOutOfScopeLauncher(int userId) { 	
		kinectStartUpKinectIfNeeded();
		dm.waitForUserIsCalibrated(userId);
		kinectCounter++;
		createdKinectUserOutOfScopeLauncher = new KinectUserOutOfScopeLauncher();
		kinectManager.addKinectUserOutOfScopeListener(createdKinectUserOutOfScopeLauncher);
		return createdKinectUserOutOfScopeLauncher;
	}
	/**
	 * Returns a new KinectMotorLauncher. The KinectMotorLauncher will enable us
	 * to register listeners which will be notified about motor-related events.
	 * This function connects the Kinect device if it was not connected already.
	 * 
	 * @return The reference to the KinectMotorLauncher object.
	 * @throws Exception
	 */
	public KinectMotorLauncher getKinectMotorLauncher() { 
	
		kinectStartUpKinectIfNeeded();
		kinectCounter++;
		createdKinectMotorLauncher = new KinectMotorLauncher();

		kinectManager.addKinectDataListener(createdKinectMotorLauncher);

		return createdKinectMotorLauncher;
	}
	/**
	 * You have to call this function when you do not need one service of the
	 * Kinect any longer. This function subtracts one from the kinectCounter. If
	 * kinectCounter becomes 0, the kinectCounter will be disconnected.
	 * 
	 * @param kinectLauncher
	 */
	public void dropKinect(LauncherWrapper kinectLauncher) {
		if(kinectLauncher!=null){
			if(kinectLauncher instanceof KinectPoseLauncher){
				kinectManager.removeKinectDataListener(((KinectPoseLauncher)kinectLauncher).getPrivateKinectSkeletonLauncher());
				((KinectPoseLauncher)kinectLauncher).getPrivateKinectSkeletonLauncher().dropService();
				createdKinectPoseLauncher=null;
			}else 	if(kinectLauncher instanceof KinectUserMovementLauncher){
				kinectManager.removeKinectDataListener(((KinectUserMovementLauncher)kinectLauncher).getKinectPoseLauncherWalkLeftLegUp().getPrivateKinectSkeletonLauncher());
				kinectManager.removeKinectDataListener(((KinectUserMovementLauncher)kinectLauncher).getKinectPoseLauncherWalkRightLegUp().getPrivateKinectSkeletonLauncher());
				kinectManager.removeKinectDataListener(((KinectUserMovementLauncher)kinectLauncher).getKinectPoseLauncherStand().getPrivateKinectSkeletonLauncher());	
				((KinectUserMovementLauncher)kinectLauncher).getKinectPoseLauncherWalkLeftLegUp().dropService();
				((KinectUserMovementLauncher)kinectLauncher).getKinectPoseLauncherWalkRightLegUp().dropService();
				((KinectUserMovementLauncher)kinectLauncher).getKinectPoseLauncherStand().dropService();
				createdKinectUserMovementLauncher=null;
			} else if(kinectLauncher instanceof KinectUserHugLauncher){
				kinectManager.removeKinectDataListener(((KinectUserHugLauncher)kinectLauncher).getKinectPoseLauncherOpenedHug().getPrivateKinectSkeletonLauncher());
				kinectManager.removeKinectDataListener(((KinectUserHugLauncher)kinectLauncher).getKinectPoseLauncherClosedHug().getPrivateKinectSkeletonLauncher());
				((KinectUserHugLauncher)kinectLauncher).getKinectPoseLauncherOpenedHug().dropService();
				((KinectUserHugLauncher)kinectLauncher).getKinectPoseLauncherClosedHug().dropService();
				createdKinectUserHugLauncher=null;
			} else if(kinectLauncher instanceof KinectUserGameControlLauncher){
				kinectManager.removeKinectDataListener(((KinectUserGameControlLauncher)kinectLauncher).getKinectPoseLauncherCrossedHands().getPrivateKinectSkeletonLauncher());
				kinectManager.removeKinectDataListener(((KinectUserGameControlLauncher)kinectLauncher).getKinectPoseLauncherLeftHandBeneathLeftElbowSeparated30CmFromLeftHip().getPrivateKinectSkeletonLauncher());
				kinectManager.removeKinectDataListener(((KinectUserGameControlLauncher)kinectLauncher).getKinectPoseLauncherRightHandBeneathRightElbowSeparated30CmFromRightHip().getPrivateKinectSkeletonLauncher());
				kinectManager.removeKinectDataListener(((KinectUserGameControlLauncher)kinectLauncher).getKinectPoseLauncherLeftHandAboveLeftShoulder().getPrivateKinectSkeletonLauncher());
				kinectManager.removeKinectDataListener(((KinectUserGameControlLauncher)kinectLauncher).getKinectPoseLauncherRightHandAboveRightShoulder().getPrivateKinectSkeletonLauncher());
				kinectManager.removeKinectDataListener(((KinectUserGameControlLauncher)kinectLauncher).getKinectPoseLauncherHandsAboveShouldersPsiPose().getPrivateKinectSkeletonLauncher());
				
				((KinectUserGameControlLauncher)kinectLauncher).getKinectPoseLauncherCrossedHands().dropService();
				((KinectUserGameControlLauncher)kinectLauncher).getKinectPoseLauncherLeftHandBeneathLeftElbowSeparated30CmFromLeftHip().dropService();
				((KinectUserGameControlLauncher)kinectLauncher).getKinectPoseLauncherRightHandBeneathRightElbowSeparated30CmFromRightHip().dropService();
				((KinectUserGameControlLauncher)kinectLauncher).getKinectPoseLauncherLeftHandAboveLeftShoulder().dropService();
				((KinectUserGameControlLauncher)kinectLauncher).getKinectPoseLauncherRightHandAboveRightShoulder().dropService();
				((KinectUserGameControlLauncher)kinectLauncher).getKinectPoseLauncherHandsAboveShouldersPsiPose().dropService();
				
				createdKinectUserGameControlLauncher=null;
	
			} else if(kinectLauncher instanceof KinectUserPickedUpFromSidesLauncher){
				kinectManager.removeKinectDataListener(((KinectUserPickedUpFromSidesLauncher)kinectLauncher).getKinectPoseLauncherLeftShoulderLowerAndCloser().getPrivateKinectSkeletonLauncher());
				kinectManager.removeKinectDataListener(((KinectUserPickedUpFromSidesLauncher)kinectLauncher).getKinectPoseLauncherRightShoulderLowerAndCloser().getPrivateKinectSkeletonLauncher());
				
				((KinectUserPickedUpFromSidesLauncher)kinectLauncher).getKinectPoseLauncherLeftShoulderLowerAndCloser().dropService();
				((KinectUserPickedUpFromSidesLauncher)kinectLauncher).getKinectPoseLauncherRightShoulderLowerAndCloser().dropService();
				createdKinectUserPickedUpFromSidesLauncher=null;
			}else if(kinectLauncher instanceof KinectUserOutOfScopeLauncher){
				kinectManager.removeKinectUserOutOfScopeListener(((KinectUserOutOfScopeLauncher)kinectLauncher));
				((KinectUserOutOfScopeLauncher)kinectLauncher).dropService();
				createdKinectUserOutOfScopeLauncher=null;
			} else if(kinectLauncher instanceof KinectUserReachWithBothHandsLauncher){
				kinectManager.removeKinectDataListener(((KinectUserReachWithBothHandsLauncher)kinectLauncher).getKinectSkeletonLauncher());
				((KinectUserReachWithBothHandsLauncher)kinectLauncher).getKinectSkeletonLauncher().dropService();
			}
			if (kinectCounter == 0) {
				kinectManager.disconnect();
			}
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
		kinectManager.addKinectDataListener(kujrpl);
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
	 *            before continuing.getKinectPoseLauncherTouchingEar
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

			Point3D massCenter = kinectManager.getDepthGenerator().convertRealWorldToProjective(kinectManager.getUserGenerator().getUserCoM(userId));

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
			e1.printStackTrace();
		}
		
	}
	/**
	 * 
	 * @param userId The ID label of the user we want to detect the pose to.
	 * @param kinectPose The KinectPose that we want to be detected.
	 * @return A KinectPoseLauncher which will notify its listeners when the user with ID label userId performs the pose kinectPose. 
	 */
	public KinectPoseLauncher getKinectPoseLauncher(int userId, KinectPoseEnum kinectPose) { 
			
		kinectStartUpKinectIfNeeded();	
		dm.waitForUserIsCalibrated(userId);
		kinectCounter++;
		createdKinectPoseLauncher = new KinectPoseLauncher(userId, kinectPose);
	
		try {
			
			createdKinectPoseLauncher.setPrivateKinectSkeletonLauncher(new KinectSkeletonLauncher(userId, false));
			kinectManager.addKinectDataListener(createdKinectPoseLauncher.getPrivateKinectSkeletonLauncher());
			createdKinectPoseLauncher.getPrivateKinectSkeletonLauncher().addListener(kinectPoseManager);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		kinectPoseManager.addListener(createdKinectPoseLauncher);
		return createdKinectPoseLauncher;
	}
	/**
	 * 
	 * @param userId The ID label of the user we want to detect the pose to.
	 * @return A KinectUserMovementLauncher which will notify its listeners when the user with ID label userId performs any pose related with the movement of the user. 
	 */
	public KinectUserMovementLauncher getKinectUserMovementLauncher(int userId) { 
	
		kinectStartUpKinectIfNeeded();
		dm.waitForUserIsCalibrated(userId);
		createdKinectUserMovementLauncher = new KinectUserMovementLauncher(userId);			
		return createdKinectUserMovementLauncher;
	}
	/**
	 * 
	 * @param userId The ID label of the user we want to detect the pose to.
	 * @return A KinectUserReachWithBothHandsLauncher which will notify its listeners when the user with ID label userId performs any pose related with the movement of the user. 
	 */
	public KinectUserReachWithBothHandsLauncher getKinectUserReachWithBothHandsLauncher(int userId){
				
		kinectStartUpKinectIfNeeded();
		dm.waitForUserIsCalibrated(userId);
		createdKinectUserReachWithBothHandsLauncher = new KinectUserReachWithBothHandsLauncher(userId);
		return createdKinectUserReachWithBothHandsLauncher;		
		
	}
	/**
	 * 
	 * @param userId The ID label of the user we want to detect the pose to.
	 * @return A KinectUserMovementLauncher which will notify its listeners when the user with ID label userId performs a hug. 
	 */
	public KinectUserHugLauncher getKinectUserHugLauncher(int userId) { 
		// We allow to exist only one Kinect device
	
		kinectStartUpKinectIfNeeded();
		dm.waitForUserIsCalibrated(userId);
		createdKinectUserHugLauncher = new KinectUserHugLauncher(userId);
		return createdKinectUserHugLauncher;
	}
	/**
	 * 
	 * @param userId The ID label of the user we want to detect the pose to.
	 * @return A KinectUserGameControlLauncher which will notify its listeners when the user with ID label userId performs any pose related with the control of the menus of the game. 
	 */
	public KinectUserGameControlLauncher getKinectUserGameControlLauncher(int userId){	
		kinectStartUpKinectIfNeeded();
		dm.waitForUserIsCalibrated(userId);
		createdKinectUserGameControlLauncher = new KinectUserGameControlLauncher(userId);	
		return createdKinectUserGameControlLauncher;		
	}

	/**
	 * 
	 * @param userId The ID label of the user we want to detect the pose to.
	 * @return A KinectUserPickedUpFromSidesLauncher which will notify its listeners when the user with ID label userId performs either a pose of picking up from the left side or a pose of picking up from the right side. 
	 */	
	public KinectUserPickedUpFromSidesLauncher getKinectUserPickedUpFromSidesLauncher(int userId){			
		kinectStartUpKinectIfNeeded();
		dm.waitForUserIsCalibrated(userId);
		createdKinectUserPickedUpFromSidesLauncher = new KinectUserPickedUpFromSidesLauncher(userId);
		return createdKinectUserPickedUpFromSidesLauncher;
	}		
	/**
	 * @return the maximumNumberOfKinectUsers
	 */
	public int getMaximumNumberOfKinectUsers() {
		return maximumNumberOfKinectUsers;
	}

	/**
	 * @param the maximumNumberOfKinectUsers to set
	 */
	public void setMaximumNumberOfKinectUsers(int maximumNumberOfKinectUsers) {
		this.maximumNumberOfKinectUsers = maximumNumberOfKinectUsers;
	}
	/**
	 * 
	 * @return the map of launchers of WiiMote accelerations events
	 */
	public Map<Integer, WiiMoteAccelerationLauncher> getCreatedWiiMoteAccelerationLauncher(){
		return createdWiiMoteAccelerationLauncher;
	}
	/**
	 * 
	 * @return the map of launchers of WiiMote button events 
	 */
	public Map<Integer, WiiMoteButtonsLauncher> getCreatedWiiMoteButtonsLauncher(){
		return createdWiiMoteButtonsLauncher;
	}
	/**
	 * 
	 * @return the map of launchers of WiiMote rotation events 
	 */
	public Map<Integer, WiiMoteRotationLauncher> getCreatedWiiMoteRotationLauncher(){
		return createdWiiMoteRotationLauncher;
	}
	/**
	 * 
	 * @return the map of launchers of WiiMote IR events 
	 */
	public Map<Integer, WiiMoteIRLauncher> getCreatedWiiMoteIRLauncher(){
		return 	createdWiiMoteIRLauncher;	
	}
	/**
	 * 
	 * @return the map of launchers of WiiMote IR glance events 
	 */
	public Map<Integer, WiiMoteIRGlanceLauncher> getCreatedWiiMoteIRGlanceLauncher(){
		return createdWiiMoteIRGlanceLauncher;
	}
	/**
	 * 
	 * @return the map of launchers of Nunchuk acceleration events 
	 */
	public Map<Integer, NunchukAccelerationLauncher> getCreatedNunchukAccelerationLauncher() {
		return createdNunchukAccelerationLauncher;
	}
	/**
	 * 
	 * @return the map of launchers of Nunchuk button events 
	 */
	public Map<Integer, NunchukButtonsLauncher> getCreatedNunchukButtonsLauncher() {
		return createdNunchukButtonsLauncher;
	}
	/**
	 * 
	 * @return the map of launchers of Nunchuk analog stick events 
	 */
	public Map<Integer, NunchukAnalogStickLauncher> getCreatedNunchukAnalogStickLauncher() {
		return createdNunchukAnalogStickLauncher;
	}
	/**
	 * 
	 * @return the launcher of WiiBoard events
	 */
	public WiiBoardLauncher getCreatedWiiBoardLauncher() {
		return createdWiiBoardLauncher;
	}
	/**
	 * 
	 * @return the launcher of NoninLauncher events
	 */
	public NoninLauncher getCreatedNoninLauncher() {
		return createdNoninLauncher;
	}
	/**
	 * 
	 * @return the launcher of Kinect skeleton events
	 */
	public KinectSkeletonLauncher getCreatedKinectSkeletonLauncher() {
		return createdKinectSkeletonLauncher;
	}
	/**
	 * 
	 * @return the launcher of Kinect motor events
	 */
	public KinectMotorLauncher getCreatedKinectMotorLauncher() {
		return createdKinectMotorLauncher;
	}
	/**
	 * 
	 * @return the launcher of Kinect pose events
	 */
	public KinectPoseLauncher getCreatedKinectPoseLauncher() {
		return createdKinectPoseLauncher;
	}
	/**
	 * 
	 * @return the launcher of Kinect movement events
	 */
	public KinectUserMovementLauncher getCreatedKinectUserMovementLauncher() {
		return createdKinectUserMovementLauncher;
	}
	/**
	 * 
	 * @return the launcher of Kinect hug events
	 */
	public KinectUserHugLauncher getCreatedKinectUserHugLauncher() {
		return createdKinectUserHugLauncher;
	}
	/**
	 * 
	 * @return the launcher of Kinect game control events 
	 */
	public KinectUserGameControlLauncher getCreatedKinectUserGameControlLauncher() {
		return createdKinectUserGameControlLauncher;
	}

	/**
	 * 
	 * @return the launcher of Kinect picking up from sides events
	 */
	public KinectUserPickedUpFromSidesLauncher getCreatedKinectUserPickedUpFromSidesLauncher() {
		return createdKinectUserPickedUpFromSidesLauncher;
	}

	/**
	 * 
	 * @return the launcher of Kinect user out of scope events
	 */
	public KinectUserOutOfScopeLauncher getCreatedKinectUserOutOfScopeLauncher() {
		return createdKinectUserOutOfScopeLauncher;
	}
	/**
	 * 
	 * @return the launcher of Kinect user reach with both hands event
	 */
	public KinectUserReachWithBothHandsLauncher getCreatedKinectUserReachWithBothHandsLauncher() {
		return createdKinectUserReachWithBothHandsLauncher;
	}

	
}
