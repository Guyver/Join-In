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

import org.OpenNI.Point3D;
import org.OpenNI.SkeletonJoint;

import control.IEventCommModule;
/**
 * Defines the KinectUserJointReachPoint events.
 * @author Santiago Hors Fraile
 *
 */
public class KinectUserJointReachPointServiceEvent  implements IEventCommModule{

	/**
	 * Represents the user we are going to track to see whether her joint reaches the specified point.
	 */
	private int userId;
	/**
	 * Represents the user's joint which we are going to track to see whether it reaches the specified point.
	 */
	private SkeletonJoint joint;
	/**
	 * Represents the center of the virtual sphere which the user's joint must reach. 
	 */
	private Point3D sphereCenter; 
	/**
	 * Represents the radius of the virtual sphere. 
	 */
	private Point3D radius;
	/**
	 * Represents the point which has been reached and that has triggered the event.
	 */
	private Point3D triggeringPoint;
	/**
	 * Sets the field of this class with the given parameters.
	 * @param userId The identifier which determines which user the values refer to. 
	 * @param joint The joint of the user we are tracking to see whether it reaches the point.
	 * @param sphereCenter The center of the virtual sphere which the user's joint must reach. 
	 * @param radius The radius of the virtual sphere. 
	 */
	public KinectUserJointReachPointServiceEvent(int userId, SkeletonJoint joint, Point3D sphereCenter, Point3D radius, Point3D triggeringPoint){
		this.setUserId(userId);
		this.setJoint(joint);
		this.setSphereCenter(sphereCenter);
		this.setRadius(radius);
		this.setTriggeringPoint(triggeringPoint);
		
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
	 * @return the sphereCenter
	 */
	public Point3D getSphereCenter() {
		return sphereCenter;
	}

	/**
	 * @param sphereCenter the sphereCenter to set
	 */
	public void setSphereCenter(Point3D sphereCenter) {
		this.sphereCenter = sphereCenter;
	}

	/**
	 * @return the radius
	 */
	public Point3D getRadius() {
		return radius;
	}

	/**
	 * @param radius the radius to set
	 */
	public void setRadius(Point3D radius) {
		this.radius = radius;
	}

	/**
	 * @return the triggeringPoint
	 */
	public Point3D getTriggeringPoint() {
		return triggeringPoint;
	}

	/**
	 * @param triggeringPoint the triggeringPoint to set
	 */
	public void setTriggeringPoint(Point3D triggeringPoint) {
		this.triggeringPoint = triggeringPoint;
	}
}
