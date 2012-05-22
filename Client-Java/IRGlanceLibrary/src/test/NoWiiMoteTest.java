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

import java.util.Random;

import IRGlancePackage.IRGlance;

public class NoWiiMoteTest {

	/**
	 * 
	 * This test simulates that the IR points come in and out the wiimote camera range of sight randomly. 
	 */
	public static void main (String [] args) throws InterruptedException{
		
		IRGlance irglance = new IRGlance(1000);
		
		ListenerTest li = new ListenerTest ();
		
		irglance.addListener(li);
		
		int waitingTime; 
		int irRandom;
		
		Random generator = new Random(System.currentTimeMillis());

		
		for(int i=0; i<100; i++){
			
			waitingTime = generator.nextInt(500);
			irRandom = generator.nextInt(4);
			
			irglance.IRSpotGlanced(irRandom);
			
			Thread.sleep(waitingTime);
			
		}
		
		System.out.println("Bye-bye");
	}
	
	
}
