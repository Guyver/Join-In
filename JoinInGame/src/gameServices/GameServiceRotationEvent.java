package gameServices;

import org.wiigee.event.RotationEvent;

import control.IEventCommModule;

public class GameServiceRotationEvent implements IEventCommModule{
	
	/**
	 * Represents the pitch of the WiiMotionPlus.
	 */
    protected double pitch;

	/**
	 * Represents the yaw of the WiiMotionPlus.
	 */
	protected double yaw;

	/**
	 * Represents the roll of the WiiMotionPlus.
	 */
    protected double roll;
    
    /**
     * Rrepresents the label of the wiimote which has throwed the event
     */    
    protected int label; 
    /**
     * Sets the fields of this class with the information contained in the given parameter.
     * @param event The new RotationEvent.
     */
	public GameServiceRotationEvent(RotationEvent event, int label) {
        this.pitch = event.getPitch();
        this.roll = event.getRoll();
        this.yaw = event.getYaw();
        this.label = label; 
	}
	/**
	 * Gets the current field pitch.
	 * @return double The current pitch.
	 */
	public double getPitch() {
        return this.pitch;
    }
	/**
	 * Gets the current field yaw.
	 * @return double The current yaw.
	 */
    public double getYaw() {
        return this.yaw;
    }
	/**
	 * Gets the current field roll.
	 * @return double The current roll.
	 */
    public double getRoll() {
        return this.roll;
    }

    /**
	 * Gets the label of the wiimote.
	 * @return int The label of the wiimote.
	 */
    public int getLabel(){
    	return this.label;    	
    }
}
