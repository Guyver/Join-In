package services;

import control.IEventCommModule;

public class KinectUserHugServiceEvent implements IEventCommModule  {
	
	private int userId;


	
	public KinectUserHugServiceEvent(int userId){

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


}
