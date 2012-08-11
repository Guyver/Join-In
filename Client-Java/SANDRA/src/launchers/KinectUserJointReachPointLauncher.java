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
package launchers;

import java.util.Iterator;

import javax.vecmath.Vector3d;

import org.OpenNI.Point3D;
import org.OpenNI.SkeletonJoint;

import KinectPackage.IKinectDataListener;
import KinectPackage.KinectDataEvent;


import services.KinectUserJointReachPointServiceEvent;

import iservices.IKinectUserJointReachPointService;
import control.DeviceManager;
import control.IListenerCommModule;
import control.LauncherWrapper;

public class KinectUserJointReachPointLauncher extends LauncherWrapper
		implements IKinectDataListener {

	/**
	 * The user's joint we want to track.
	 */
	private SkeletonJoint joint;
	/**
	 * The center of the virtual sphere that the user's joint has to reach.
	 */
	private Point3D sphereCenter;
	/**
	 * The radius of the virtual sphere.
	 */
	private Point3D radius;

	/**
	 * Creates a new KinectUserJointReachPointLauncher and initializes it with
	 * the given parameters.
	 * 
	 * @param joint
	 *            The user's joint we want to track.
	 * @param sphereCenter
	 *            The center of the virtual sphere that the user's joint has to
	 *            reach.
	 * @param radius
	 *            The radius of the virtual sphere.
	 */
	public KinectUserJointReachPointLauncher( SkeletonJoint joint,
			Point3D sphereCenter, Point3D radius) {
	
		this.setJoint(joint);
		this.setSphereCenter(sphereCenter);
		this.setRadius(radius);
	}

	/**
	 * Adds a listener to the list of listeners of the superclass
	 * LauncherWrapper.
	 * 
	 * @param l
	 *            The listener that have to be added.
	 * @throws Exception
	 */
	public void addListener(IKinectUserJointReachPointService l)
			throws Exception {
		super.addListener(l);
	}

	/**
	 * Calls to the superclass LauncherWrapper function dropService and drops
	 * the Kinect from the DeviceMnager.
	 */
	@Override
	public void dropService() {
		if (!deviceNotNecessaryAnyLonger) {
			super.dropService();
			DeviceManager.getDeviceManager().dropKinect(this);
		}
	}

	@Override
	/**
	 * Throws the given KinectEvent to listeners in the listenerList as a KinectUserJointReachPointServiceEvent if the point retrieved for the user's joint we are tracking is inside the virtual sphere we have defined.
	 * @param ke The KinectEvent.
	 */
	public void kinectUpdate(KinectDataEvent ke) {

		/*Point3D currentPoint = new Point3D((float) ke.getKinectData()
				.getSkeletonManager().getJoint3D(userId, joint).getX(),
				(float) ke.getKinectData().getSkeletonManager()
						.getJoint3D(userId, joint).getY(), (float) ke
						.getKinectData().getSkeletonManager()
						.getJoint3D(userId, joint).getZ());*/

		Point3D currentPoint = new Point3D((float) ke.getKinectData()
				.getSkeletonManager().getJoint3D( joint).getX(),
				(float) ke.getKinectData().getSkeletonManager()
						.getJoint3D( joint).getY(), (float) ke
						.getKinectData().getSkeletonManager()
						.getJoint3D( joint).getZ());
		KinectUserJointReachPointServiceEvent se = new KinectUserJointReachPointServiceEvent( joint, sphereCenter, radius, currentPoint);
		Iterator<IListenerCommModule> it = super.listenersList.iterator();

		/*Vector3d myCurrentPoint = ke.getKinectData().getSkeletonManager()
				.getJoint3D(userId, joint);*/

		Vector3d myCurrentPoint = ke.getKinectData().getSkeletonManager()
				.getJoint3D( joint);

		if (myCurrentPoint.getX() >= sphereCenter.getX() - radius.getX()
				&& myCurrentPoint.getX() <= sphereCenter.getX() + radius.getX()
				&& myCurrentPoint.getY() >= sphereCenter.getY() - radius.getY()
				&& myCurrentPoint.getY() <= sphereCenter.getY() + radius.getY()
				&& myCurrentPoint.getZ() >= sphereCenter.getZ() - radius.getZ()
				&& myCurrentPoint.getZ() <= sphereCenter.getZ() + radius.getZ()) {

			while (it.hasNext()) {
				IKinectUserJointReachPointService l = (IKinectUserJointReachPointService) it
						.next();
				l.kinectUpdate(se);
			}

		}

	}

	

	/**
	 * @return the joint
	 */
	public SkeletonJoint getJoint() {
		return joint;
	}

	/**
	 * @param joint
	 *            the joint to set
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
	 * @param sphereCenter
	 *            the sphereCenter to set
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
	 * @param radius
	 *            the radius to set
	 */
	public void setRadius(Point3D radius) {
		this.radius = radius;
	}

}
