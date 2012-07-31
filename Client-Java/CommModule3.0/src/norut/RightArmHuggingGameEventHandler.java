package norut;

import java.util.EventListener;

import org.wiigee.device.Wiimote;

import control.DeviceManager;

import services.WiiMoteServiceRotationEvent;
import services.WiiMoteServiceRotationSpeedEvent;
import iservices.ICalibrationFinishedService;
import iservices.IWiiMoteRotationService;
import iservices.IWiiMoteRotationSpeedService;

public class RightArmHuggingGameEventHandler implements IWiiMoteRotationService, IWiiMoteRotationSpeedService, ICalibrationFinishedService, EventListener{

	
	double currentYaw;
	double pastYaw;
	boolean reachedOpenHug;
	boolean reachedClosedHug;
	boolean doingOpenHug;
	int counter;
	HugManager hugManager;

	
	public RightArmHuggingGameEventHandler(){
		currentYaw=0.0;
		pastYaw=0.0;
		reachedOpenHug=false;
		reachedClosedHug=false;
		doingOpenHug=true;
		counter=0;
	}
	
	@Override
	public void rotationReceived(WiiMoteServiceRotationEvent se) {	
		
		
		Wiimote wm= se.getWiimote();
		 
		DeviceManager dm = DeviceManager.getDeviceManager();
		Wiimote rightWiiMote;	
		hugManager = HugManager.getHugManager(20);
		
		if(dm.getWiiMoteCreated().containsKey(new Integer(1))){		
			rightWiiMote= dm.getWiiMoteCreated().get(new Integer(1));	
			if(wm.getBtAddress()==rightWiiMote.getBtAddress()){
				
				counter++;
				if (se.getYaw()>pastYaw){
					doingOpenHug=true;
				}else if(se.getYaw()<pastYaw){
					doingOpenHug=false;
				}else{//don't change anything
				}
				if(doingOpenHug && se.getYaw()>40.0){
					reachedOpenHug=true;
					hugManager.rightArmExtended();
					
				}
				if(reachedOpenHug && !doingOpenHug && se.getYaw()<-40.0){
					reachedClosedHug=true;
					hugManager.rightArmClosed();
				}
				//Prepare for the next repetition
				if(reachedClosedHug && reachedOpenHug){
					reachedOpenHug=false;
					reachedClosedHug=false;
					//System.out.println("Correct movement with your RIGHT arm. You rock!");
					counter=0;
			
				
				}else{
					if(counter>800){
						//System.out.println("Are you doing the hugging movement properly? Try again! :)");
						//System.out.println("Yaw: "+se.getYaw()+" Yaw : "+se.getYaw()+" Yaw: "+se.getYaw());
						counter=0;
						reachedOpenHug=false;
						reachedClosedHug=false;
						hugManager.resetRightArm();
				;
					}
				}
	
			
			}else{
				System.out.println("La BT del derecho no coincide");
			}
		}else{
			System.out.println("No registrado el wiimote derecho");
		}
	}

	@Override
	public void rotationSpeedReceived(WiiMoteServiceRotationSpeedEvent se) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void calibrationFinished() {
		// TODO Auto-generated method stub
		
	}


	
	



}
