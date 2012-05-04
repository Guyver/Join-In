package norut;

import control.IListenerCommModule;

public interface IGameRotationService extends IListenerCommModule{
	
	void gameRotationReceived(GameServiceRotationEvent se);

}
