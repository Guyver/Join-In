package test;

import java.io.IOException;


import org.wiigee.control.WiimoteWiigee;
import org.wiigee.device.Wiimote;



public class Test 
{
	private WiimoteWiigee wiigee;
	private Wiimote wiimote;
	String eventstring=""; 
	


	public static void main (String args []) throws IOException
	{
		//System.setProperty("bluecove.stack.first", "widcomm");
		System.setProperty("bluecove.jsr82.psm_minimum_off", "true");
		try{
			new Test();
		}catch(Exception e){
			//e.printStackTrace();
		}
	}
	

    public Test() throws IOException
    {
    	
    
    	wiigee = new WiimoteWiigee();
    	
    	try{
        	MiListenerDeWiimote mldw = new MiListenerDeWiimote();
        	wiimote = wiigee.getDeviceByMac("001B7AE9657D");

        	//wiimote = wiigee.getDevice();

			// Para el nunchuk
	        MiExtensionListenerDeNunchuk elistnun = new MiExtensionListenerDeNunchuk();
			MiListenerDeWiimote elistmot = new MiListenerDeWiimote();
			// MiListenerDeIR elistir = new MiListenerDeIR();
			wiimote.addAccelerationListener(elistmot);
			wiimote.addButtonListener(elistmot);

			// wiimote.addInfraredListener(elistir);

			 wiimote.addExtensionListener(elistnun);
			// wiimote.addAnalogStickListener(elistnun);
			// wiimote.addNunchukAccelerometerListener(elistnun);
			// wiimote.addNunchukButtonListener(elistnun);

			// Para el motionplus

			wiimote.connectWiiMotionPlus();
			
			MiExtensionListenerDeWiiMotionPlus elistplus = new MiExtensionListenerDeWiiMotionPlus();
			
			//wiimote.addWiiMotionPlusRotationListener(elistplus);
			wiimote.getWiiMotionPlus().addRotationListener(elistplus);

          // wiimote.connectNunchuk();            
          // wiimote.setInfraredCameraEnabled(true);
			
			
        	}catch(Exception e){
        		e.printStackTrace();
        	}
        	
        	
        	
        	
    	

        	
        	
     
    }


	


	
}
