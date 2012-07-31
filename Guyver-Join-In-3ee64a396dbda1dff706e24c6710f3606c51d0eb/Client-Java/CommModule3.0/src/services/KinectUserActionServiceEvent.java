package services;

import control.IEventCommModule;

public class KinectUserActionServiceEvent implements IEventCommModule  {
	
	private int userId;
	private String userAction;

	
	public KinectUserActionServiceEvent(int userId, String userAction){
		this.setUserAction(userAction);
		this.setUserId(userId);
	}
	
	/**
	 * @return the userId
	 */
	public int getUserId() {
		return userId;
	}
	/**
	 * @param userId the userId to set
	 */
	public void setUserId(int userId) {
		this.userId = userId;
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


}
