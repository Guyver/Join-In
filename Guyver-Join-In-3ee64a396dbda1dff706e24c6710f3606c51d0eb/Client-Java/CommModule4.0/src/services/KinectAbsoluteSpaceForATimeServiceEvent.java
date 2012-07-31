/**
 * Copyright 2012 Santiago Hors Fraile

  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
 */
package services;

import javax.vecmath.Vector3d;

import org.OpenNI.SkeletonJoint;

import control.IEventCommModule;
/**
 * Defines the KinectAbsoluteSpaceForATime events.
 * @author Santiago Hors Fraile
 *
 */
public class KinectAbsoluteSpaceForATimeServiceEvent  implements IEventCommModule{

	/**
	 * Represents the user we are going to track to see whether her joint reaches the specified point.
	 */
	private int userId;
	/**
	 * Represents the user's joint which we are going to track to see whether it reaches the specified point.
	 */
	private SkeletonJoint joint;
	/**
	 * Represents the point which the user's joint must reach. 
	 */
	private Vector3d space; 
	/**
	 * Represents time throughout which the space was calculated.
	 */
	private long time;
	
	/**
	 * Sets the field of this class with the given parameters.
	 * @param userId The identifier which determines which user the values refer to. 
	 * @param joint The joint of the user we are tracking to see whether it reaches the point.
	 * @param pont The point to reach by the user's joint.
	 * @param time The time throughout which the space was calculated.
	 */	
	public KinectAbsoluteSpaceForATimeServiceEvent(int userId, SkeletonJoint joint, Vector3d space, long time){
		this.setUserId(userId);
		this.setJoint(joint);
		this.setSpace (space);
		this.setTime(time);
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
	 * @return the joint
	 */
	public SkeletonJoint getJoint() {
		return joint;
	}

	/**
	 * @param joint the joint to set
	 */
	public void setJoint(SkeletonJoint joint) {
		this.joint = joint;
	}

	/**
	 * @return the space
	 */
	public Vector3d getSpace() {
		return space;
	}

	/**
	 * @param space the space to set
	 */
	public void setSpace(Vector3d space) {
		this.space = space;
	}

	/**
	 * @return the time
	 */
	public long getTime() {
		return time;
	}

	/**
	 * @param time the time to set
	 */
	public void setTime(long time) {
		this.time = time;
	}
}
