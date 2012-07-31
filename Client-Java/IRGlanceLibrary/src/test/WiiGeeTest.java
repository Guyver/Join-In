
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
package test;

import java.io.IOException;

import org.wiigee.control.WiimoteWiigee;
import org.wiigee.device.Wiimote;
import org.wiigee.event.ButtonListener;
import org.wiigee.event.ButtonPressedEvent;
import org.wiigee.event.ButtonReleasedEvent;
import org.wiigee.event.InfraredEvent;
import org.wiigee.event.InfraredListener;


import IRGlancePackage.IRGlance;
import IRGlancePackage.IRGlanceListener;

/**
 * This is an example of how to use the IRGlanceLibrary using the supplied ListenerTest and WiiGee.
 * @author Santiago
 *
 */
public class WiiGeeTest implements ButtonListener, InfraredListener
{
	private WiimoteWiigee wiigee;
	private Wiimote wiimote;
	String eventstring=""; 


	public static void main (String args []) throws IOException
	{
		new WiiGeeTest();
	}
	
	/**
	 * This function settles all the necessary actions to run the test
	 */
	//System.setProperty("bluecove.jsr82.psm_minimum_off", "true");
    public WiiGeeTest() throws IOException
    {
    	wiigee = new WiimoteWiigee();
    	
    	wiimote = wiigee.getDevice();	
    	
    	IRGlance irglance = new IRGlance(3000);
    	irglance.setEventFilter(1); //We can set the filter to a value of 3 for the WiiGee.
    	
    	IRGlanceListener irgl = new ListenerTest();
    	irglance.addListener(irgl);
    	
    	wiimote.setInfraredCameraEnabled(true,Wiimote.IR_MODE_EXTENDED);
    	wiimote.addInfraredListener(irglance);
    	
    	wiimote.addButtonListener(this);
    	wiimote.addInfraredListener(this);	   	
    }

	/**
	 * This function recognizes if we have pushed the minus button to stop the test.
	 */
	public void buttonReleaseReceived(ButtonReleasedEvent event)
	{
		if (event.getButton() == Wiimote.BUTTON_MINUS)
			{wiimote.disconnect();System.out.println("Bye-Bye");
			}
	}

	/**
	 * This function prints the number the IR spots for each event arrives. It prints a new line if the number is different from the number that was before. 
	 */
	public void infraredReceived(InfraredEvent event)
	{

		int acum=0;
		
		for(int i=0; i<event.getValids().length; i++){
			if(event.getValids()[i]){
				acum++;
			}
			
		}
		
		
		if(!eventstring.equals(acum+"")){
			eventstring = acum + "";
			System.out.print("\n    Event = "+acum);
		}else{
			System.out.print(acum);

		}
		
	}

	public void buttonPressReceived(ButtonPressedEvent event) {
		// TODO Auto-generated method stub
		
	}
	
}
