package services;

import kinectThreads.KinectPoseEnum;
import control.IEventCommModule;

public class KinectPoseServiceEvent implements IEventCommModule  {
	
	private int userId;
	private KinectPoseEnum kinectPose;
	private boolean posing;
	
	public KinectPoseServiceEvent(int userId, KinectPoseEnum kinectPose, boolean posing){
		this.setKinectPose(kinectPose);
		this.setUserId(userId);
		this.setPosing(posing);
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
	 * @return the kinectPose
	 */
	public KinectPoseEnum getKinectPose() {
		return kinectPose;
	}
	/**
	 * @param kinectPose the kinectPose to set
	 */
	public void setKinectPose(KinectPoseEnum kinectPose) {
		this.kinectPose = kinectPose;
	}

	/**
	 * @return the posing
	 */
	public boolean isPosing() {
		return posing;
	}

	/**
	 * @param posing the posing to set
	 */
	public void setPosing(boolean posing) {
		this.posing = posing;
	}
	
	

}
