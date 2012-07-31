/*
 * wiigee - accelerometerbased gesture recognition
 * Copyright (C) 2007, 2008, 2009 Benjamin Poppinga
 * 
 * Developed at University of Oldenburg
 * Contact: wiigee@benjaminpoppinga.de
 *
 * This file is part of wiigee.
 *
 * wiigee is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.wiigee.device;

import java.io.IOException;
import java.util.Random;
import java.util.Vector;
import javax.bluetooth.L2CAPConnection;
import javax.microedition.io.Connector;
import javax.swing.event.EventListenerList;

import org.wiigee.control.WiimoteStreamer;
import org.wiigee.device.Device;


import org.wiigee.event.ExtensionEvent;
import org.wiigee.event.ExtensionListener;
import org.wiigee.event.RotationListener;



import org.wiigee.event.*;
import org.wiigee.filter.Filter;
import org.wiigee.util.Log;


/**
 * This class represents the basic functions of the wiimote.
 * If you want your wiimote to e.g. vibrate you'll do this here.
 * 
 * @author Benjamin Poppinga, upgraded by Santiago Hors Fraile
 */
public class Wiimote extends Device {

    // Fixed number values.
    public static final int BUTTON_2 = 0x0001;
    public static final int BUTTON_1 = 0x0002;
    public static final int BUTTON_B = 0x0004;
    public static final int BUTTON_A = 0x0008;
    public static final int BUTTON_MINUS = 0x0010;
    public static final int BUTTON_HOME = 0x0080;
    public static final int BUTTON_LEFT = 0x0100;
    public static final int BUTTON_RIGHT = 0x0200;
    public static final int BUTTON_DOWN = 0x0400;
    public static final int BUTTON_UP = 0x0800;
    public static final int BUTTON_PLUS = 0x1000;

    // Reports
    public static final byte CMD_SET_REPORT = 0x52;

    // IR Modes
    public static final byte IR_MODE_STANDARD = 0x01;
    public static final byte IR_MODE_EXTENDED = 0x03;

    // Modes / Channels
    public static final byte MODE_BUTTONS = 0x30;
    public static final byte MODE_BUTTONS_ACCELERATION = 0x31;
    public static final byte MODE_BUTTONS_ACCELERATION_INFRARED = 0x33;
    
    // For connections and disconnections
    public static final int TYPE_NUNCHUK= 1;
    public static final int TYPE_WIIMOTIONPLUS = 2;

    /**
     *  Represents the BlueTooth address as string representation
     */
    private String btaddress;

    /**
     *  Represents the LED encoded as byte.
     */
    byte ledencoding;

    /**
     *  Represents the filters that can filter the data stream.
     */
    protected Vector<Filter> rotfilters = new Vector<Filter>();

    /**
     *  Represents the control connection that send commands to the wiimote.
     */
    private L2CAPConnection controlCon;

    /**
     *  Represents the reception connection that receive answers from the wiimote.
     */
    private L2CAPConnection receiveCon;

    /**
     *  Stores the IR listeners that receive generated events.
     */
    protected Vector<InfraredListener> infraredlistener = new Vector<InfraredListener>();
   
    // Functional
    /**
     * Represents the vibration state of the WiiMote. True if vibrating, false otherwise.
     */
    private boolean vibrating;
    /**
     * Represents the calibration state of the WiiMote. True if calibrated, false otherwise.
     */
    private boolean calibrated;
    /**
     * Represents the IR state of the WiimMote. True if enabled, false otherwise.
     */
    private boolean infraredEnabled;
    /**
     * Represents the streamer that controls the flow of bytes arriving from the WiiMote.
     */
    private WiimoteStreamer wms;
    /**
     * Represents the WiiMotionPlus connected event. True if connected, false otherwise.
     */
    private boolean wiiMotionPlusEnabled = false;
    
    //For Nunchuk and WiiMotionPlus control 
	private Nunchuk nunchuk;
	private WiiMotionPlus wiimotionplus;
	
	private EventListenerList listenerList = new EventListenerList();
	private boolean wiiNunchukEnabled= false;
	private boolean wiiMotionPlusCalibrated =false;
	private int nunchukbuttonstate =0;

	
	
    /**
     * Creates a new wiimote-device with a specific bluetooth mac-adress.
     *
     * @param btaddress
     * 			String representation of the mac-adress e.g. 00191D68B57C.
     * @param autofiltering
     *          If set the wiimote would automatically add the IdleStateFilter.
     * @param autoconnect
     *          If set the wiimote would automatically be connected.
     */
    public Wiimote(String btaddress, boolean autofiltering, boolean autoconnect) throws IOException {
        super(autofiltering);
        this.btaddress = this.removeChar(btaddress, ':');
        this.vibrating = false;
        /*
        this.setCloseGestureButton(Wiimote.BUTTON_HOME);
        this.setRecognitionButton(Wiimote.BUTTON_B);
        this.setTrainButton(Wiimote.BUTTON_A);
         */
        // automatic connect enabled
        if (autoconnect) {
            this.connect();
            this.calibrateAccelerometer();
            this.streamData(true);
            this.setLED(1);
        
        }
       
    }

   

	/**
     * Creates the two needed connections to send and receive commands
     * to and from the wiimote-device.
     *
     */
    public void connect() throws IOException {
        this.controlCon = (L2CAPConnection) Connector.open("btl2cap://" +
                this.btaddress + ":11;authenticate=false;encrypt=false;master=false",
                Connector.WRITE); // 11
        this.receiveCon = (L2CAPConnection) Connector.open("btl2cap://" +
                this.btaddress + ":13;authenticate=false;encrypt=false;master=false",
                Connector.READ); // 13
    }

    /**
     * Disconnects the wiimote and closes the two connections.
     */
    public void disconnect() {
        this.vibrating = false;
        try {
            this.controlCon.close();
            this.receiveCon.close();
            Log.write("Disconnected wiimote.");
        } catch (Exception e) {
            Log.write("Failure during disconnect of wiimote.");
        }
    }

    /**
     * @return
     * 		Receiving data connection
     */
    public L2CAPConnection getReceiveConnection() {
        return this.receiveCon;
    }

    /**
     * This method makes the Wiimote-Class reacting to incoming data.
     * For just controlling and sending commands to the wiimote
     * (vibration, LEDs, ...) it's not necessary to call this method.
     *
     * @param value
     * 		true, if the class should react to incoming data.
     * 		false, if you only want to send commands to wiimote and
     * 		only the control-connection is used.
     */
    public void streamData(boolean value) {
        if (value == true) {
            if (this.wms == null) {
                this.wms = new WiimoteStreamer(this);
            }
            wms.start();
        } else if (this.wms != null) {
            wms.stopThread();
        }
    }

    /**
     * The added Listener will be notified about detected infrated
     * events.
     *
     * @param listener The Listener to be added.
     */
    public void addInfraredListener(InfraredListener listener) {
        this.infraredlistener.add(listener);
    }



    /**
     * Write data to a register inside of the wiimote.
     *
     * @param offset The memory offset, 3 bytes.
     * @param data The data to be written, max. 16 bytes.
     * @throws IOException
     */
    public void writeRegister(byte[] offset, byte[] data) throws IOException {
        byte[] raw = new byte[23];
        raw[0] = CMD_SET_REPORT;
        raw[1] = 0x16; // Write channel
        raw[2] = 0x04; // Register
        for (int i = 0; i < offset.length; i++) {
            raw[3 + i] = offset[i];
        }
        raw[6] = (byte) data.length;
        for (int i = 0; i < data.length; i++) {
            raw[7 + i] = data[i];
        }
        this.sendRaw(raw);
    }

    /**
     * Makes the Wiimote respond the data of an register. The wiimotestreamer
     * doesn't react to the reponse yet.
     *
     * @param offset The memory offset.
     * @param size The size which has to be read out.
     * @throws IOException
     */
    public void readRegister(byte[] offset, byte[] size) throws IOException {
        byte[] raw = new byte[8];
        raw[0] = CMD_SET_REPORT;
        raw[1] = 0x17; // Read channel
        raw[2] = 0x04; // Register
        for (int i = 0; i < offset.length; i++) {
            raw[3 + i] = offset[i];
        }
        for (int i = 0; i < size.length; i++) {
            raw[6 + i] = size[i];
        }
        this.sendRaw(raw);
    }

    /**
     * Reads data out of the EEPROM of the wiimote.
     * At the moment this method is only used to read out the
     * calibration data, so the wiimotestreamer doesn't react for
     * every answer on this request.
     *
     * @param offset The memory offset.
     * @param size The size.
     * @throws IOException
     */
    public void readEEPROM(byte[] offset, byte[] size) throws IOException {
        byte[] raw = new byte[8];
        raw[0] = CMD_SET_REPORT;
        raw[1] = 0x17; // Read channel
        raw[2] = 0x00; // EEPROM
        for (int i = 0; i < offset.length; i++) {
            raw[3 + i] = offset[i];
        }
        for (int i = 0; i < size.length; i++) {
            raw[6 + i] = size[i];
        }
        this.sendRaw(raw);
    }

    /**
     * Sends pure hexdata to the wiimote. If you want your wiimote
     * to vibrate use sendRaw(new byte[] {0x52, 0x13, 0x01}). For other raw-commands use
     * the specific wiki-sites around the web (wiili.org, wiibrew.org, ...)
     * @param raw
     * 		byte representation of an command
     */
    public void sendRaw(byte[] raw) throws IOException {
        if (this.controlCon != null) {
            this.controlCon.send(raw);
            try {
                Thread.sleep(100l);
            } catch (InterruptedException e) {
                System.out.println("sendRaw() interrupted");
            }
        }
    }

    /**
     * Enables one or more LEDs, where the value could be between 0 and 8.
     * If value=1 only the left LED would light up, for value=2 the second
     * led would light up, for value=3 the first and second led would light up,
     * and so on...
     *
     * @param value Between 0 and 8, indicating which LEDs should light up
     * @throws IOException
     */
    public void setLED(int value) throws IOException {
        if (value < 16 && value > 0) {
            byte tmp = (byte) value;
            this.ledencoding = (byte) (tmp << 4);
            this.sendRaw(new byte[]{CMD_SET_REPORT, 0x11, this.ledencoding});
        } else {
            // Random LED change :)
            this.setLED(new Random().nextInt(16));
        }
    }

    /**
     * Updates the report channel according to the choosen
     * functions that are enabled (acceleration, irda, ...).
     *
     */
    private void updateReportChannel() throws IOException {
        if(!accelerationEnabled
        && !anyExtensionEnabled()
        && !infraredEnabled) {
            this.sendRaw(new byte[]{CMD_SET_REPORT, 0x12, 0x00, 0x30});
        }
        else if(accelerationEnabled
             && !anyExtensionEnabled()
             && !infraredEnabled) {
            this.sendRaw(new byte[]{CMD_SET_REPORT, 0x12, 0x04, 0x31});
        }
        else if(!accelerationEnabled
             && anyExtensionEnabled()
             && !infraredEnabled) {
            this.sendRaw(new byte[]{CMD_SET_REPORT, 0x12, 0x00, 0x32});
        }
        else if(accelerationEnabled
                && !anyExtensionEnabled()
                && infraredEnabled) {
               this.sendRaw(new byte[]{CMD_SET_REPORT, 0x12, 0x04, 0x33});
        }
        else if(!accelerationEnabled
                && anyExtensionEnabled()
                && !infraredEnabled) {
               this.sendRaw(new byte[]{CMD_SET_REPORT, 0x12, 0x04, 0x34});
        }
        else if(accelerationEnabled
             && anyExtensionEnabled()
             && !infraredEnabled) {
            this.sendRaw(new byte[]{CMD_SET_REPORT, 0x12, 0x04, 0x35});
        }
        else if(!accelerationEnabled
                && anyExtensionEnabled()
                && infraredEnabled) {
               this.sendRaw(new byte[]{CMD_SET_REPORT, 0x12, 0x04, 0x36});
        }
        else if(accelerationEnabled
             && anyExtensionEnabled()
             && infraredEnabled) {
            this.sendRaw(new byte[]{CMD_SET_REPORT, 0x12, 0x04, 0x37});
        }
        else {
            // default channel - fallback to button only.
            Log.write("Invalid Value Configuration: Fallback to Buttons only.");
            this.sendRaw(new byte[]{CMD_SET_REPORT, 0x12, 0x00, 0x30});
        }
    }

    /**
     * Initializes the calibration of the accerlerometer. This is done once
     * per each controller in program lifetime.
     *
     * @throws IOException
     */
    private void calibrateAccelerometer() throws IOException {
        this.readEEPROM(new byte[]{0x00, 0x00, 0x20}, new byte[]{0x00, 0x07});
        this.calibrated = true;
    }

    /**
     * Activates the acceleration sensor. You have to call the
     * streamData(true) method to react to this acceleration data.
     * Otherwise the wiimote would send data the whole time and
     * nothing else would happen.
     *
     */
    @Override
    public void setAccelerationEnabled(boolean enabled) throws IOException {
        super.setAccelerationEnabled(enabled);
        if(enabled) {
            Log.write("Enabling ACCELEROMETER...");
            this.accelerationEnabled = true;
            if (!this.calibrated) {
                this.calibrateAccelerometer();
            }
        } else {
            Log.write("Disabling ACCELEROMETER...");
            this.accelerationEnabled = false;
        }
       
       // change channel dynamically
       this.updateReportChannel();
    }

    /**
     * Enables or disables the infrared camera of the wiimote with
     * the default values.
     *
     * @param enabled Should the Infrared Camera be enabled.
     * @throws IOException In case of a connection error.
     */
    public void setInfraredCameraEnabled(boolean enabled) throws IOException {
        this.setInfraredCameraEnabled(enabled, Wiimote.IR_MODE_STANDARD);
        System.out.println("IRCamera activated");
    }

    /**
     * Enables the infrared camera in front of the wiimote to track
     * IR sources in the field of view of the camera. This could be used
     * to a lot of amazing stuff. Using this Mode could slow down the
     * recognition of acceleration gestures during the increased data
     * size transmitted.
     *
     * @param enabled Should the Infrared Camera be enabled.
     * @param infraredMode The choosen Infrared Camera Mode.
     * @throws IOException In case of a connection error.
     *
     */
    public void setInfraredCameraEnabled(boolean enabled, byte infraredMode) throws IOException {
        if(enabled) {
            Log.write("Enabling INFRARED CAMERA...");
            this.infraredEnabled = true;

            //write 0x04 to output 0x13
            this.sendRaw(new byte[]{CMD_SET_REPORT, 0x13, 0x04});

            // write 0x04 to output 0x1a
            this.sendRaw(new byte[]{CMD_SET_REPORT, 0x1a, 0x04});

            // write 0x08 to reguster 0xb00030
            this.writeRegister(new byte[]{(byte) 0xb0, 0x00, 0x30}, new byte[]{0x08});

            // write sensivity block 1 to register 0xb00000
            this.writeRegister(new byte[]{(byte) 0xb0, 0x00, 0x00}, new byte[]{0x00, 0x00, 0x00, 0x00, 0x00, 0x00, (byte) 0x90, 0x00, (byte) 0x41});

            // write sensivity block 2 to register 0xb0001a
            this.writeRegister(new byte[]{(byte) 0xb0, 0x00, (byte) 0x1a}, new byte[]{0x40, 0x00});

            // write ir-mode to register 0xb00033
            this.writeRegister(new byte[]{(byte) 0xb0, 0x00, 0x33}, new byte[]{infraredMode});
        } else {
            Log.write("Disabling INFRARED CAMERA...");
            this.infraredEnabled = false;
        }

        // change channel dynamically
        this.updateReportChannel();
    }

  

    /**
     * With this method you gain access over the vibrate function of
     * the wiimote. You got to try which time in milliseconds would
     * fit your requirements.
     *
     * @param milliseconds Time the wiimote would approx. vibrate.
     */
    public void vibrateForTime(long milliseconds) throws IOException {
        try {
            if (!vibrating) {
                this.vibrating = true;
                byte tmp = (byte) (this.ledencoding | 0x01);
                this.sendRaw(new byte[]{CMD_SET_REPORT, 0x11, tmp});
                Thread.sleep(milliseconds);
                this.sendRaw(new byte[]{CMD_SET_REPORT, 0x11, this.ledencoding});
                this.vibrating = false;
            }
        } catch (InterruptedException e) {
            System.out.println("WiiMoteThread interrupted.");
        }
    }

    /**
     * Fires a infrared event, containig coordinate pairs (x,y) and a
     * size of the detected IR spot.
     *
     * @param coordinates X and Y display coordinates.
     * @param size The size of the spot.
     */
    public void fireInfraredEvent(int[][] coordinates, int[] size) {
        InfraredEvent w = new InfraredEvent(this, coordinates, size);
        for (int i = 0; i < this.infraredlistener.size(); i++) {
        	
            this.infraredlistener.get(i).infraredReceived(w);
        }
    }
	
    //Helper methods
    /**
     * Removes all chars that are the same than the one given by parameter from a string given by parameter.
     */
    private String removeChar(String s, char c) {
        String r = "";
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) != c) {
                r += s.charAt(i);
            }
        }
        return r;
    }
    
    //For Nunchuk and WiiMotionPlus uses
    /**
     * Throws a disconnection event to all extensions listeners referring to the Nunchuk or WiiMote depending on the given parameter. 
     * @param type The type of extension connected: TYPE_NUNCHUK or TYPE_WIIMOTIONPLUS.
     */
	protected void fireExtensionConnectedEvent(int type) {
			
		ExtensionListener[] listeners = listenerList.getListeners(ExtensionListener.class);
		ExtensionEvent evt;
		if(type==TYPE_NUNCHUK){
			evt = new ExtensionEvent(this, nunchuk, TYPE_NUNCHUK);

		}else{
			evt = new ExtensionEvent(this, wiimotionplus, TYPE_WIIMOTIONPLUS);

		}
		for (ExtensionListener l : listeners) {
			l.extensionConnected(evt);
		}
	}
	 /**
     * Throws a connection event to all extensions listeners referring to the Nunchuk or WiiMote depending on the given parameter. 
     * @param type The type of extension disconnected: TYPE_NUNCHUK or TYPE_WIIMOTIONPLUS.
     */
	protected void fireExtensionDisconnectedEvent(int type) {
		
		ExtensionListener[] listeners = listenerList.getListeners(ExtensionListener.class);
		
		ExtensionEvent evt;
		if(type==TYPE_NUNCHUK){
			evt = new ExtensionEvent(this, nunchuk, TYPE_NUNCHUK);
		}else{
			evt = new ExtensionEvent(this, wiimotionplus, TYPE_WIIMOTIONPLUS);
		}
		
		for (ExtensionListener l : listeners) {
			l.extensionDisconnected(evt);
		}
	}
	/**
	 * Adds the DataListener given by parameter to the listenerList.
	 * @param listener The DataListener to be added.
	 */
	public void addDataListener(DataListener listener) {
		listenerList.add(DataListener.class, listener);
	}
	/**
	 * Adds the ExtensionListener given by parameter to the listenerList.
	 * Notifies connections and disconnections.
	 * @param listener The ExtensionListener to be added.
	 */
	public void addExtensionListener(ExtensionListener listener) {
		listenerList.add(ExtensionListener.class, listener);
	
	}
	
	/**
	 * Adds the AnalogStickListener given by parameter to the listenerList.
	 * @param listener The AnalogStickListener to be added.
	 */
	public void addAnalogStickListener(AnalogStickListener listener){
		listenerList.add(AnalogStickListener.class, listener);
	}
	
	/**
	 * Adds the NunchukAccelerometerListener given by parameter to the listenerList.
	 * @param listener The NunchukAccelerometerListener to be added.
	 */
	public void addNunchukAccelerometerListener(AccelerometerListener<Nunchuk> listener){
		listenerList.add(AccelerometerListener.class, listener);
	}
	
	/**
	 * Adds the NunchukButtonListener given by parameter to the listenerList.
	 * @param listener The NunchukButtonListener to be added.
	 */
	public void addNunchukButtonListener(NunchukButtonListener listener){
		listenerList.add(NunchukButtonListener.class, listener);
	}

	/**
	 * Adds the RotationListener given by parameter to the listenerList.
	 * @param listener The RotationListener to be added.
	 */
	public void addWiiMotionPlusRotationListener(RotationListener listener) {
		listenerList.add(RotationListener.class, listener);
	}

	/**
	 * Removes the DataListener given by parameter to the listenerList.
	 * @param listener The FataListener to be removed.
	 */
	public void removeDataListener(DataListener listener) {
		listenerList.remove(DataListener.class, listener);
	}
	/**
	 * Removes the ExtensionListener given by parameter to the listenerList.
	 * @param listener The ExtensionListener to be removed.
	 */
	public void removeExtensionListener(ExtensionListener listener) {
		listenerList.remove(ExtensionListener.class, listener);
	}
	/**
	 * Removes the AnalogStickListener given by parameter to the listenerList.
	 * @param listener The AnalogStickListener to be removed.
	 */
	public void removeAnalogStickListener(AnalogStickListener listener){
		listenerList.remove(AnalogStickListener.class, listener);
	}
	/**
	 * Removes the NunchukAccelerometerListener given by parameter to the listenerList.
	 * @param listener The NunchukAccelerometerListener to be removed.
	 */
	public void removeNunchukAccelerometerListener(AccelerometerListener<Nunchuk> listener){
		listenerList.remove(AccelerometerListener.class, listener);
	}
	
	/**
	 * Removes the NunchukButtonListener given by parameter to the listenerList.
	 * @param listener The NunchukButtonListener to be removed.
	 */
	public void removeNunchukButtonListener(NunchukButtonListener listener){
		listenerList.remove(NunchukButtonListener.class, listener);
	}
	
	/**
	 * Removes the WiiMotionPlusListener given by parameter to the listenerList.
	 * @param listener The WiiMotionPlusListener to be removed.
	 */
	public void removeWiiMotionPlusAccelerometerListener(AccelerometerListener<WiiMotionPlus> listener){
		listenerList.remove(AccelerometerListener.class, listener);
	}
     /**
     * Enables or disables the WiiMotionPlus extension. 
     * If it enables, the WiiMote will further get
     * every other information, like acceleration, infrared camera (loss of precision)
     * and button presses.
     * Anyway the channel is updated automatically.
     * 
     * @throws java.io.IOException
     * @param enabled The command to enable or disable the WiiMotionPlus.
     */
    public void setWiiMotionPlusEnabled(boolean enabled) throws IOException {
    	this.wiimotionplus= new WiiMotionPlus(this);
        if(enabled && !this.isWiiNunchukEnabled()) {
            Log.write("Enabling WII MOTION PLUS..");
            fireExtensionConnectedEvent(TYPE_WIIMOTIONPLUS);  
            this.wiiMotionPlusEnabled = true;
            this.writeRegister(new byte[]{(byte) 0xa6, 0x00, (byte) 0xfe}, new byte[]{0x04});

        }else if(enabled && this.isWiiNunchukEnabled()){
        	Log.write("Enabling WII MOTION PLUS in passthrough mode..");
            fireExtensionConnectedEvent(TYPE_WIIMOTIONPLUS);
            this.wiiMotionPlusEnabled = true;
            this.writeRegister(new byte[]{(byte) 0xa6, 0x00, (byte) 0xfe}, new byte[]{0x05});

        } else {
            Log.write("Disabling WII MOTION PLUS..");
            wiiMotionPlusCalibrated=false;
            fireExtensionDisconnectedEvent(TYPE_WIIMOTIONPLUS);
            this.wiiMotionPlusEnabled = false;
    		this.writeRegister(new byte[]{(byte) 0xa4,  0x00, (byte) 0xf0}, new byte[]{0x55});

        }
        // change channel dynamically
        this.updateReportChannel();
    }
    /**
     * Enables or disables the Nunchuk extension. 
     * If it enables, the WiiMote will further get
     * every other information, like acceleration, infrared camera (loss of precision)
     * and button presses.
     * Anyway the channel is updated automatically.
     * 
     * @throws java.io.IOException
     * @param enabled The command to enable or disable the Nunchuk.
     */
    public void setNunchukEnabled(boolean enabled) throws IOException {
    	nunchukbuttonstate=0;
    	
        if(enabled && !wiiMotionPlusEnabled) {
            Log.write("Enabling NUNCHUK...");
            fireExtensionConnectedEvent(TYPE_NUNCHUK);
            this.wiiNunchukEnabled = true;
            this.writeRegister(new byte[]{(byte) 0xa4,  0x00, (byte) 0xf0}, new byte[]{0x55});
            this.writeRegister(new byte[]{(byte) 0xa4,  0x00, (byte) 0xfb}, new byte[]{0x00});
            this.writeRegister(new byte[]{(byte) 0xa4, 0x00, (byte) 0xfe}, new byte[]{0x0000});
            this.nunchuk = new Nunchuk(this);
            
        }else if(enabled && wiiMotionPlusEnabled){
        	Log.write("Enabling NUNCHUK (with WiiMotionPlus)...");
        	fireExtensionConnectedEvent(TYPE_NUNCHUK);
        	this.wiiNunchukEnabled = true;
            this.writeRegister(new byte[]{(byte) 0xa4,  0x00, (byte) 0xf0}, new byte[]{0x55});
            this.writeRegister(new byte[]{(byte) 0xa4,  0x00, (byte) 0xfb}, new byte[]{0x00});
            this.writeRegister(new byte[]{(byte) 0xa6, 0x00, (byte) 0xfe}, new byte[]{0x05});
            this.nunchuk = new Nunchuk(this);

        } else {
            Log.write("Disabling NUNCHUK...");
            fireExtensionDisconnectedEvent(TYPE_NUNCHUK);
            this.wiiNunchukEnabled = false;
            this.nunchuk=null;
            if(isWiiMotionPlusEnabled()){
	        	this.writeRegister(new byte[]{(byte) 0xa4,  0x00, (byte) 0xf0}, new byte[]{0x55});
	           	this.writeRegister(new byte[]{(byte) 0xa6, 0x00, (byte) 0xfe}, new byte[]{0x04});
            }
        }
        // change channel dynamically
        this.updateReportChannel();
    }
    public void setBoardEnabled(boolean enabled) throws IOException {
  
    	
        if(enabled && !wiiMotionPlusEnabled) {
            Log.write("Enabling Board...");
            this.writeRegister(new byte[]{(byte) 0xa4,  0x00, (byte) 0xf0}, new byte[]{0x55});
            this.writeRegister(new byte[]{(byte) 0xa4,  0x00, (byte) 0xfb}, new byte[]{0x00});
            this.writeRegister(new byte[]{(byte) 0xa4, 0x00, (byte) 0xfe}, new byte[]{0x0000});
            this.nunchuk = new Nunchuk(this);
  
      
        }
        // change channel dynamically
        this.updateReportChannel();
    }
    /**
     * Reports if there is any extension (WiiMotionPlus or Nunchuk) enabled to the calling WiiMote.
     * @return boolean True if the WiiMotionPlus or Nunchuk is enabled, false otherwise.
     */
	public boolean anyExtensionEnabled(){
		return(wiiNunchukEnabled || wiiMotionPlusEnabled);
	}
	
    /**
     * Reports if there is any WiiMotionPlus enabled to the calling WiiMote.
     * @return boolean True if the WiiMotionPlus is enabled, false otherwise.
     */
	public boolean isWiiMotionPlusEnabled(){
		return this.wiiMotionPlusEnabled;
	}

    /**
     * Reports if there is any Nuncbuk enabled to the calling WiiMote.
     * @return boolean True if the Nunchuk is enabled, false otherwise.
     */
	public boolean isWiiNunchukEnabled() {
		return this.wiiNunchukEnabled;
	}


	/**
	 * Gets the field that stores the list of listeners.
	 * @return EventListenerList The field listenerList.
	 */
	public EventListenerList getListenerList() {
		return listenerList;
	}

	/**
	 * Gets the Nunchuk of the calling WiiMote.
	 * @return Nunchuk The object Nunchuk.
	 */
	public Nunchuk getNunchuk() {
		return nunchuk;
	}

	/**
	 * Gets the WiiMotionPlus of the calling WiiMote.
	 * @return WiiMotionPlus The object WiiMotionPlus.
	 */
	public WiiMotionPlus getWiiMotionPlus() {
		return wiimotionplus;
	}

	/**
	 * Sends the command to connect the Nunchuk.
	 */
	public void connectNunchuk(){
		try {
			setNunchukEnabled(true);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Sends the command to connect the WiiMotionPlus.
	 */
	public void connectWiiMotionPlus(){
		try {
			setWiiMotionPlusEnabled(true);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Sends the command to disconnect the Nunchuk.
	 */
	public void disconnectNunchuk(){
		try {
			setNunchukEnabled(false);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Sends the command to disconnect the WiiMotionPlus.
	 */
	public void disconnectWiiMotionPlus(){
		try {
			setWiiMotionPlusEnabled(false);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Sets the calibration state of the WiiMotionPlus with the given parameter.
	 * @param b The new calibration state of the WiiMotionPlus
	 */
	public void setWiiMotionPlusCalibrated(boolean b) {
		wiiMotionPlusCalibrated =b;
	}
	
	/**
	 * Gets the calibration state of the WiiMotionPlus.
	 * @return boolean The current calibration state of the WiiMotionPlus.
	 */
	public boolean isWiiMotionPlusCalibrated() {
		return wiiMotionPlusCalibrated;
	}


	/**
	 * Sets the button state of the Nunchuk with the given parameter.
	 * @param nunchukbuttonstate The new button state of the Nunchuk.
	 */
	public void setNunchukbuttonstate(int nunchukbuttonstate) {
		this.nunchukbuttonstate = nunchukbuttonstate;
	}
	
	/**
	 * Gets the button state of the Nunchuk.
	 * @return int The current button state of the Nunchuk.
	 */
	public int getNunchukbuttonstate() {
		return nunchukbuttonstate;
	}
	
	/**
	 * Gets the MAC (aka bt) address
	 * @return String The MAC (aka bt) address
	 */
	public String getBtAddress(){
		return this.btaddress;
		
	}



}
