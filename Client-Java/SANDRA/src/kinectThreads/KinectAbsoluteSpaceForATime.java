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

import launchers.KinectAbsoluteSpaceForATimeLauncher;

import org.OpenNI.SkeletonJoint;

import KinectPackage.KinectManager;

import control.DeviceManager;

/**
 * This class implements the logic to calculate the space that exists between
 * the initial and final position of a given joint of a given user when a given
 * time expires.
 * 
 * @author Santiago Hors Fraile
 */
public class KinectAbsoluteSpaceForATime implements Runnable {

	
	/**
	 * This attribute represents the specific joint we want to track from the
	 * user.
	 */
	SkeletonJoint joint;
	/**
	 * This attribute represents the time throughout we are going to track the
	 * user's joint.
	 */
	long time;
	/**
	 * Contains all AbsoluteSpaceForATime listeners.
	 */

	KinectAbsoluteSpaceForATimeLauncher launcher;

	/**
	 * This constructor assigns the user identification label, the joint we want
	 * to track, the time throughout we want to track.
	 * 
	 * @param joint
	 *            The joint we want to track.
	 * @param time
	 *            The amount of time (in milliseconds) that we want to track the
	 *            user's joint.
	 * @param launcher
	 */
	public KinectAbsoluteSpaceForATime(SkeletonJoint joint,
			long time, KinectAbsoluteSpaceForATimeLauncher launcher) {

		this.joint = joint;
		this.time = time;
		this.launcher = launcher;

	}

	@Override
	public void run() {
		KinectManager kinectManager = DeviceManager.getDeviceManager()
				.getKinectManager();
		/*Vector3d initialPosition = kinectManager.getSkeletonManager()
				.getJoint3D(userId, joint);*/
		Vector3d initialPosition = kinectManager.getSkeletonManager()
				.getJoint3D( joint);
		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		/*Vector3d finalPosition = kinectManager.getSkeletonManager().getJoint3D(
				userId, joint);*/
		Vector3d finalPosition = kinectManager.getSkeletonManager().getJoint3D(
				 joint);
		Vector3d result = new Vector3d( Math.abs(finalPosition.getX()
				- initialPosition.getX()),  Math.abs(finalPosition
				.getY() - finalPosition.getY()),  Math.abs(finalPosition
				.getZ() - finalPosition.getZ()));

		KinectAbsoluteSpaceForATimeEvent ke = new KinectAbsoluteSpaceForATimeEvent(
				 joint, result);
		launcher.kinectAbsoluteSpaceForATimeUpdate(ke);
		launcher.setRunning(false);
	}

}
