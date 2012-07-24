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

package NoninTest;
import java.io.IOException;
import java.io.InputStream;
import java.util.Vector;

import javax.bluetooth.DeviceClass;
import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.DiscoveryListener;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.RemoteDevice;
import javax.bluetooth.ServiceRecord;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;


public class TestMain {

	//Creamos un vector donde almacenaremos los RemoteDevice descubiertos
    public static final Vector<RemoteDevice> devicesDiscovered = new Vector<RemoteDevice>();

	
     public static void main(String[] args) throws IOException, InterruptedException {
          
    	 final Object inquiryCompletedEvent = new Object();
    	     	 
          DiscoveryListener listener = new DiscoveryListener() {
               /**
               * Called when a device is found during an inquiry.
               */
      

			public void deviceDiscovered(RemoteDevice btDevice, DeviceClass cod) {
                    System.out.println("El dispositivo " + btDevice.getBluetoothAddress() + " ha sido econtrado");
                    devicesDiscovered.addElement(btDevice);
                   
                    try {
                         System.out.println("     nombre " + btDevice.getFriendlyName(false));
                    } catch (IOException cantGetDeviceName) {
                         System.out.println("Error: "+cantGetDeviceName);
                    }
                    try{
                    	
                    	

                    	 StreamConnection cc = (StreamConnection) Connector.open("btspp://"+btDevice.getBluetoothAddress()+":1");
                    	 InputStream input = cc.openInputStream();
                    	 
                    	
                    	//El nonin manda 4 bytes: el primero con todos sus bits a 1, el segundo con el puslo, el tercer con la oxigenaci�n y el cuarto a valor 36 (00100100)
                    	//input.read(); //Solo leemos para que se "pasen" los 8 primeros bits de todo a 1
                    	//System.out.println("Pulso: "+input.read());
                    	//System.out.println("Oxigenaci�n: "+input.read());
                    	long t = System.currentTimeMillis();
                    	long actual; 
                    	long anterior = System.currentTimeMillis();
                    	long dif;
                    	while(System.currentTimeMillis()<t+250000){
                    		byte[] b= new byte[4];
                    		input.read(b);
                    		actual = System.currentTimeMillis();
                    		dif= actual-anterior;
                    		anterior = actual;
                    		System.out.println("Tiempo: "+dif);
                    		System.out.println("Primero: "+b[0]);
                    		System.out.println("Pulso: "+b[1]);
                    		System.out.println("Oxigenaci�n: "+b[2]);
                    		System.out.println("�ltimo: "+b[3]+"\n\n");
                    	
                    	} 

                    
                    }
                    catch(Exception e){
                    	e.printStackTrace();
					}
                    
                    
               }

               /**
               * Called when an inquiry is completed.
               */
               public void inquiryCompleted(int discType) {
                    System.out.println("�Completada la investigaci�n del dispositivo!");

                    synchronized(inquiryCompletedEvent) {
                         inquiryCompletedEvent.notifyAll();
                    }
               }

               /**
               * Called when a service search is completed or was terminated because of an error.
               */
               public void serviceSearchCompleted(int transID, int respCode) {
               }
         
               /**
               * Called when service(s) are found during a service search.
               */

               public void servicesDiscovered(int transID, ServiceRecord[] servRecord) {
               }

          };// End of DiscoveryListener

          synchronized(inquiryCompletedEvent) {
               /**
               * Get the DiscoveryAgent of the LocalDevice and start the
               * Discovery process
               */

               boolean started = LocalDevice.getLocalDevice().getDiscoveryAgent().startInquiry(DiscoveryAgent.GIAC, listener);
               if (started) {
                    System.out.println("Comenzando el proceso de descubrimiento de dispositivos ...");
                    /**
                    * Wait for Discovery Process end
                    */
                    inquiryCompletedEvent.wait();
                    System.out.println("Hubo " + devicesDiscovered.size() +  " dispositvo(s) encontrado(s)");
                    
               } else {
                    System.out.println("�Fall� el proceso de descubrimiento de dispositivos!");
               }
          }
          
          
          
     }//End of main
} // End of class
