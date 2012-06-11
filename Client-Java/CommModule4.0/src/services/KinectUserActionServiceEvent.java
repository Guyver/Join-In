package services;

import control.IEventCommModule;

public class KinectUserActionServiceEvent implements IEventCommModule  {
	
	
	private String userAction;
	private int value;

	
	public KinectUserActionServiceEvent(String userAction){
		this.setUserAction(userAction);
		this.setValue(-1);
	}
	
	public KinectUserActionServiceEvent(String userAction, int value){
		this.setUserAction(userAction);
		this.setValue(value);
	}
	
	/**
	 * @return the userAction
	 */
	public String getUserAction() {
		return userAction;
	}
	/**
	 * @param userAction the userAction to set
	 */
	public void setUserAction(String userAction) {
		this.userAction = userAction;
	}


	/**
	 * @return the value
	 */
	public int getValue() {
		return value;
	}


	/**
	 * @param value the value to set
	 */
	public void setValue(int value) {
		this.value = value;
	}


}
