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

import launchers.KinectTotalSpaceTravelledForATimeLauncher;

import org.OpenNI.SkeletonJoint;

import KinectPackage.KinectManager;

import control.DeviceManager;
/**
 * This class implements the logic to calculate the space that a given user's joint has gone over throughout a given time. 
 * 
 * @autor Santiago Hors Fraile
 */
public class KinectTotalSpaceTravelledForATime implements Runnable {

	/**
	 * This attribute represents the user ID whose movement data we want to know
	 * about.
	 */
	int userId;
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
	KinectTotalSpaceTravelledForATimeLauncher launcher;

	/**
	 * This constructor assigns the user identification label, the joint we want
	 * to track, the time throughout we want to track.
	 * 
	 * @param userId
	 *            The user we refer to.
	 * @param joint
	 *            The joint we want to track.
	 * @param time
	 *            The amount of time (in milliseconds) that we want to track the
	 *            user's joint.
	 * @param launcher
	 */
	public KinectTotalSpaceTravelledForATime(int userId, SkeletonJoint joint,
			long time, KinectTotalSpaceTravelledForATimeLauncher launcher) {
		this.userId = userId;
		this.joint = joint;
		this.time = time;
		this.launcher = launcher;
	}

	@Override
	public void run() {
		KinectManager kinectManager = DeviceManager.getDeviceManager()
				.getKinectManager();
		// Take the position of the joint at this moment
		Vector3d lastPosition = kinectManager.getSkeletonManager().getJoint3D(
				userId, joint);

		Vector3d currentPosition;
		// Initialize the result
		Vector3d result = new Vector3d(0, 0, 0);
		// Start counting the time.
		long initialTime = System.currentTimeMillis();
		while (System.currentTimeMillis() < initialTime + time) {
			// We update the final position variable with the current position
			currentPosition = kinectManager.getSkeletonManager().getJoint3D(userId, joint);
		
			// To update the result, we must ensure that the current position is
			// a valid position (it is different from the past position and it
			// is different from 0.0,0.0,0.)

			if (currentPosition.x != 0.0
					&& currentPosition.y != 0.0
					&& currentPosition.z != 0.0
					&& (currentPosition.x - lastPosition.x != 0.0
							|| currentPosition.y - lastPosition.y != 0.0 || currentPosition.z
							- lastPosition.z != 0.0)) {
				// Update the resulting point with the difference of the
				// movement
				result.set(
						(result.getX() + Math.abs(currentPosition.x	- lastPosition.x)),
						(result.getY() + Math.abs(currentPosition.y - lastPosition.y)),
						(result.getZ() + Math.abs(currentPosition.z	- lastPosition.z)));
				lastPosition = currentPosition;
			}

		}
		
		KinectTotalSpaceTravelledForATimeEvent ke = new KinectTotalSpaceTravelledForATimeEvent(userId, joint, new Vector3d(result.getX(),result.getY(), result.getZ()));
		launcher.kinectTotalSpaceTravelledForATimeUpdate(ke);
		launcher.setRunning(false);
	}

}
