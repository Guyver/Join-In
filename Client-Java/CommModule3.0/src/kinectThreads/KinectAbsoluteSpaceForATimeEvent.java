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
package kinectThreads;

import javax.vecmath.Vector3d;

import org.OpenNI.SkeletonJoint;

public class KinectAbsoluteSpaceForATimeEvent {

	/**
	 * The user's ID label  
	 */
	private int userId;
	/**
	 * The user's joint
	 */
	private SkeletonJoint joint;
	/**
	 * The vector that contains the space the user's joint has moved.
	 */
	private Vector3d space;
	
	/**
	 * Creates a new KinectAbsoluteSpaceForATimeEvent object with the given parameters.
	 * @param userId The user's ID label.
	 * @param joint The user's joint.
	 * @param point The space that the user's joint has moved.
	 */
	public KinectAbsoluteSpaceForATimeEvent(int userId, SkeletonJoint joint,Vector3d space) {
		this.setUserId(userId);
		this.setJoint(joint);
		this.setSpace(space);
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
	 * @return the space
	 */
	public Vector3d getSpace() {
		return space;
	}

	/**
	 * @param point the space to set
	 */
	public void setSpace(Vector3d space) {
		this.space = space;
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

}
