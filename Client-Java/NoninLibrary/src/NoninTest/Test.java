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

import NoninPackage.INoninListener;
import NoninPackage.NoninEvent;
import NoninPackage.NoninManager;


public class Test implements INoninListener{

	public static void main (String [] args) throws Exception {
		Test obj = new Test();
		
		obj.estaConectadoNonin();

	}

	public boolean estaConectadoNonin() throws Exception{
	 
		Test tes = new Test();
		
		try{
			NoninManager nm = new NoninManager();
			nm.setNoninMac("001c050028f0");
			ListenerTest lt = new ListenerTest();
			
			nm.connect();
			
nm.addListener(lt);
		    
			long inicio = System.currentTimeMillis();
			boolean noCumplido=true;
			while(noCumplido)
			if((System.currentTimeMillis()-inicio)>12000){
		
				nm.disconnect();
				noCumplido=false;
				//throw new NullPointerException();

			}
			
		
			//System.out.println("Conectado el n con ID = " + wiimote.getId());
			return true;
		}catch(Exception e){
			System.out.println("No esta conectado");
			throw new NullPointerException();
			//return false;
		}
	
		
	}



	@Override
	public void noninUpdate(NoninEvent ne) {
		
		System.out.println("Pulsohbkjhblb: "+ne.pulse);
		System.out.println("Oxigenacion: "+ne.oxy);

		
	}



}
