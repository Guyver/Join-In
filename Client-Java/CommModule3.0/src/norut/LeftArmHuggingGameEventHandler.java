package norut;



import org.wiigee.device.Wiimote;

import control.DeviceManager;
import services.WiiMoteServiceRotationEvent;
import services.WiiMoteServiceRotationSpeedEvent;
import iservices.ICalibrationFinishedService;
import iservices.IWiiMoteRotationService;
import iservices.IWiiMoteRotationSpeedService;

public class LeftArmHuggingGameEventHandler implements IWiiMoteRotationService, IWiiMoteRotationSpeedService, ICalibrationFinishedService{

	
	double currentYaw;
	double pastYaw;
	boolean reachedOpenHug;
	boolean reachedClosedHug;
	boolean doingOpenHug;
	int counter;
	HugManager hugManager;


	
	public LeftArmHuggingGameEventHandler(){
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
		Wiimote leftWiiMote;
		hugManager = HugManager.getHugManager(20);
		
		if(dm.getWiiMoteCreated().containsKey(new Integer(2))){	
			leftWiiMote= dm.getWiiMoteCreated().get(new Integer(2));
			
			if(wm.getBtAddress()==leftWiiMote.getBtAddress()){
				
				counter++;
				
				if (se.getYaw()<pastYaw){
					doingOpenHug=true;
				}else if(se.getYaw()>pastYaw){
					doingOpenHug=false;
		
				}else{
					//don't change anything
				}
				if(doingOpenHug && se.getYaw()<-40.0){
					reachedOpenHug=true;
					hugManager.rightArmClosed();
				}
				if(reachedOpenHug && !doingOpenHug && se.getYaw()>40.0){
					reachedClosedHug=true;
					hugManager.leftArmClosed();
				}	
				//Prepare for the next repetition
				if(reachedClosedHug && reachedOpenHug){
					
					reachedOpenHug=false;
					reachedClosedHug=false;
					//System.out.println("Correct movement with your LEFT arm. You rock!");
					counter=0;
								
				}else{
					if(counter>800){
						//System.out.println("Are you doing the hugging movement properly WITH YOUR LEFT ARM? Try again! :)");
						//System.out.println("Yaw: "+se.getYaw()+" Yaw : "+se.getYaw()+" Yaw: "+se.getYaw());
						counter=0;
						reachedOpenHug=false;
						reachedClosedHug=false;
						hugManager.resetLeftArm();
						
					;
					}
					
				}
			}else{
				System.out.println("La BT address del izquierdo no coincide");
				
			}
		}else{
			
			System.out.println("No registrado el wiimote izquierdo");
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
