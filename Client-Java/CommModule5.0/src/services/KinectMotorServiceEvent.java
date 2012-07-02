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

import KinectPackage.KinectData;

import control.IEventCommModule;

/**
 * Defines the KinectMotor events.
 * 
 * @author Santiago Hors Fraile
 * 
 */
public class KinectMotorServiceEvent implements IEventCommModule {

	/**
	 * Represents the x,y,z acceleartion of the Kinect.
	 */
	private int[] acceleration;
	/**
	 * Represents the x,y,z acceleartion of the Kinect as real G-Forces.
	 */
	private double[] accelerationGForce;
	/**
	 * Represents the tilt angle of the kinect.
	 */
	private double angle;
	/**
	 * Represents the speed of the motor of the Kinect.
	 */
	private int speed;

	/**
	 * Sets the field of this class with the given parameters.
	 * 
	 * @param kinectData
	 *            The kinectData which contains the acceleration,
	 *            acceleration-g-force, angle and speed of the Kinect.
	 */
	public KinectMotorServiceEvent(KinectData kinectData) {

		setAcceleration(kinectData.getMotorCommunicator().getAccel().clone());
		setAccelerationGForce(kinectData.getMotorCommunicator().getAccelG()
				.clone());
		setAngle(kinectData.getMotorCommunicator().getAngle());
		setSpeed(kinectData.getMotorCommunicator().getSpeed());

	}

	/**
	 * @return the acceleration
	 */
	public int[] getAcceleration() {
		return acceleration;
	}

	/**
	 * @param acceleration
	 *            the acceleration to set
	 */
	public void setAcceleration(int[] acceleration) {
		this.acceleration = acceleration;
	}

	/**
	 * @return the accelerationGForce
	 */
	public double[] getAccelerationGForce() {
		return accelerationGForce;
	}

	/**
	 * @param accelerationGForce
	 *            the accelerationGForce to set
	 */
	public void setAccelerationGForce(double[] accelerationGForce) {
		this.accelerationGForce = accelerationGForce;
	}

	/**
	 * @return the angle
	 */
	public double getAngle() {
		return angle;
	}

	/**
	 * @param angle
	 *            the angle to set
	 */
	public void setAngle(double angle) {
		this.angle = angle;
	}

	/**
	 * @return the speed
	 */
	public int getSpeed() {
		return speed;
	}

	/**
	 * @param speed
	 *            the speed to set
	 */
	public void setSpeed(int speed) {
		this.speed = speed;
	}
}
