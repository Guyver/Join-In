package gameLaunchers;

import gameIServices.IGameRotationService;
import gameServices.GameServiceRotationEvent;

import java.util.Iterator;



import org.wiigee.event.RotationEvent;





import control.IListenerCommModule;
import control.LauncherWrapper;

public class GameRotationLauncher extends LauncherWrapper{
	
	
	/**
	 * Adds a listener to the list of listeners of the superclass LauncherWrapper.
	 * @param l The listener that have to be added.
	 * @throws Exception 
	 */
	public void addListener (IGameRotationService l) throws Exception{	

			super.addListener(l);

	}
	
	//Specific functions
	/**
	 * Throws the given RotationEvent to all listeners in the listenerList as a RotationServiceEvent.
	 * @param event The RotationEvent.
	 */
	public void GameRotationReceived(RotationEvent arg0, int label) {
		GameServiceRotationEvent se = new GameServiceRotationEvent(arg0, label);	
		Iterator<IListenerCommModule> it = listenersList.iterator();
			while(it.hasNext()){
				IGameRotationService l = (IGameRotationService)it.next();
				l.gameRotationReceived(se);		
			}			
		
	}
	
		


}
