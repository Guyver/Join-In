package test;

import org.wiigee.event.InfraredEvent;
import org.wiigee.event.InfraredListener;

public class MiListenerDeIR implements InfraredListener{

	@Override
	public void infraredReceived(InfraredEvent event) {
		System.out.println("Longitud ="+event.getCoordinates().length);
		for(int i = 0; i< event.getCoordinates().length; i++){
			if(event.isValid(i))
			System.out.println(event.getCoordinates()[i][0]);
			System.out.println(event.getCoordinates()[i][1]);

		}
		
	}

}
