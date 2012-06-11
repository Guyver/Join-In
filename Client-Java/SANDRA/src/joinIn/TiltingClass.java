package joinIn;

import java.util.Scanner;

import control.DeviceManager;

public class TiltingClass implements Runnable{

	DeviceManager dm;
	
	public TiltingClass (DeviceManager dm){
		this.dm= dm;
	}
	
	public void run() {
	while (true){
		  Scanner input=new Scanner(System.in);
		  String key=input.next(); // Get what the user types.
		  if(key.compareTo("d")==0){
			  dm.getKinectManager().getMotorCommunicator().setAngle(dm.getKinectManager().getMotorCommunicator().getAngle()-3);
		  }else if(key.compareTo("u")==0){
			  dm.getKinectManager().getMotorCommunicator().setAngle(dm.getKinectManager().getMotorCommunicator().getAngle()+3);
		  }
		  
	}
	}
}
