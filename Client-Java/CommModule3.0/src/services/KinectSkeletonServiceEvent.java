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

import KinectPackage.KinectData;

import control.IEventCommModule;

/**
 * Defines the KinectSkeleton events.
 * 
 * @author Santiago Hors Fraile
 * 
 */
public class KinectSkeletonServiceEvent implements IEventCommModule {
	/**
	 * Identifies the specific person who is using the Kinect among the
	 * different possible other people.
	 */
	private int userId;

	/**
	 * Represents the x,y,z position of the left hand of the user who is using
	 * the Kinect.
	 */
	private Vector3d leftHand;
	/**
	 * Represents the x,y,z position of the left elbow of the user who is using
	 * the Kinect.
	 */
	private Vector3d leftElbow;
	/**
	 * Represents the x,y,z position of the left shoulder of the user who is
	 * using the Kinect.
	 */
	private Vector3d leftShoulder;
	/**
	 * Represents the x,y,z position of the left hip of the user who is using
	 * the Kinect.
	 */
	private Vector3d leftHip;
	/**
	 * Represents the x,y,z position of the left knee of the user who is using
	 * the Kinect.
	 */
	private Vector3d leftKnee;
	/**
	 * Represents the x,y,z position of the foot hand of the user who is using
	 * the Kinect.
	 */
	private Vector3d leftFoot;
	/**
	 * Represents the x,y,z position of the head of the user who is using the
	 * Kinect.
	 */
	private Vector3d head;
	/**
	 * Represents the x,y,z position of the neck of the user who is using the
	 * Kinect.
	 */
	private Vector3d neck;
	/**
	 * Represents the x,y,z position of the torso of the user who is using the
	 * Kinect.
	 */
	private Vector3d torso;
	/**
	 * Represents the x,y,z position of the right hand of the user who is using
	 * the Kinect.
	 */
	private Vector3d rightHand;
	/**
	 * Represents the x,y,z position of the right elbow of the user who is using
	 * the Kinect.
	 */
	private Vector3d rightElbow;
	/**
	 * Represents the x,y,z position of the right shoulder of the user who is
	 * using the Kinect.
	 */
	private Vector3d rightShoulder;
	/**
	 * Represents the x,y,z position of the right hip of the user who is using
	 * the Kinect.
	 */
	private Vector3d rightHip;
	/**
	 * Represents the x,y,z position of the right knee of the user who is using
	 * the Kinect.
	 */
	private Vector3d rightKnee;
	/**
	 * Represents the x,y,z position of the right foot of the user who is using
	 * the Kinect.
	 */
	private Vector3d rightFoot;


	/**
	 * Sets the field of this class with the given parameters.
	 * 
	 * @param userId
	 *            The identifier which determines which user the values refer
	 *            to.
	 * @param kinectData
	 *            The data of the Kinect which contains the user's joint
	 *            poisitions.
	 */
	public KinectSkeletonServiceEvent(int userIdParam, KinectData kinectData) {
	
		this.setUserId(userIdParam + 1);
		setLeftHand(new Vector3d(kinectData.getSkeletonManager().getJoint3D(
				userId, SkeletonJoint.LEFT_HAND)));
		setLeftElbow(new Vector3d(kinectData.getSkeletonManager().getJoint3D(
				userId, SkeletonJoint.LEFT_ELBOW)));
		setLeftShoulder(new Vector3d(kinectData.getSkeletonManager()
				.getJoint3D(userId, SkeletonJoint.LEFT_SHOULDER)));
		setLeftHip(new Vector3d(kinectData.getSkeletonManager().getJoint3D(
				userId, SkeletonJoint.LEFT_HIP)));
		setLeftKnee(new Vector3d(kinectData.getSkeletonManager().getJoint3D(
				userId, SkeletonJoint.LEFT_KNEE)));
		setLeftFoot(new Vector3d(kinectData.getSkeletonManager().getJoint3D(
				userId, SkeletonJoint.LEFT_FOOT)));
		setHead(new Vector3d(kinectData.getSkeletonManager().getJoint3D(userId,
				SkeletonJoint.HEAD)));
		setNeck(new Vector3d(kinectData.getSkeletonManager().getJoint3D(userId,
				SkeletonJoint.NECK)));
		setTorso(new Vector3d(kinectData.getSkeletonManager().getJoint3D(
				userId, SkeletonJoint.TORSO)));
		setRightHand(new Vector3d(kinectData.getSkeletonManager().getJoint3D(
				userId, SkeletonJoint.RIGHT_HAND)));
		setRightElbow(new Vector3d(kinectData.getSkeletonManager().getJoint3D(
				userId, SkeletonJoint.RIGHT_ELBOW)));
		setRightShoulder(new Vector3d(kinectData.getSkeletonManager()
				.getJoint3D(userId, SkeletonJoint.RIGHT_SHOULDER)));
		setRightHip(new Vector3d(kinectData.getSkeletonManager().getJoint3D(
				userId, SkeletonJoint.RIGHT_HIP)));
		setRightKnee(new Vector3d(kinectData.getSkeletonManager().getJoint3D(
				userId, SkeletonJoint.RIGHT_KNEE)));
		setRightFoot(new Vector3d(kinectData.getSkeletonManager().getJoint3D(
				userId, SkeletonJoint.RIGHT_FOOT)));

	}

	/**
	 * @return the userId
	 */
	public int getUserId() {
		return userId;
	}

	/**
	 * @param userId
	 *            the userId to set
	 */
	public void setUserId(int userId) {
		this.userId = userId;
	}

	/**
	 * @return the leftHand
	 */
	public Vector3d getLeftHand() {
		return leftHand;
	}

	/**
	 * @param leftHand
	 *            the leftHand to set
	 */
	public void setLeftHand(Vector3d leftHand) {
		this.leftHand = leftHand;
	}

	/**
	 * @return the leftElbow
	 */
	public Vector3d getLeftElbow() {
		return leftElbow;
	}

	/**
	 * @param leftElbow
	 *            the leftElbow to set
	 */
	public void setLeftElbow(Vector3d leftElbow) {
		this.leftElbow = leftElbow;
	}

	/**
	 * @return the leftShoulder
	 */
	public Vector3d getLeftShoulder() {
		return leftShoulder;
	}

	/**
	 * @param leftShoulder
	 *            the leftShoulder to set
	 */
	public void setLeftShoulder(Vector3d leftShoulder) {
		this.leftShoulder = leftShoulder;
	}

	/**
	 * @return the leftHip
	 */
	public Vector3d getLeftHip() {
		return leftHip;
	}

	/**
	 * @param leftHip
	 *            the leftHip to set
	 */
	public void setLeftHip(Vector3d leftHip) {
		this.leftHip = leftHip;
	}

	/**
	 * @return the leftKnee
	 */
	public Vector3d getLeftKnee() {
		return leftKnee;
	}

	/**
	 * @param leftKnee
	 *            the leftKnee to set
	 */
	public void setLeftKnee(Vector3d leftKnee) {
		this.leftKnee = leftKnee;
	}

	/**
	 * @return the leftFoot
	 */
	public Vector3d getLeftFoot() {
		return leftFoot;
	}

	/**
	 * @param leftFoot
	 *            the leftFoot to set
	 */
	public void setLeftFoot(Vector3d leftFoot) {
		this.leftFoot = leftFoot;
	}

	/**
	 * @return the head
	 */
	public Vector3d getHead() {
		return head;
	}

	/**
	 * @param head
	 *            the head to set
	 */
	public void setHead(Vector3d head) {
		this.head = head;
	}

	/**
	 * @return the neck
	 */
	public Vector3d getNeck() {
		return neck;
	}

	/**
	 * @param neck
	 *            the neck to set
	 */
	public void setNeck(Vector3d neck) {
		this.neck = neck;
	}

	/**
	 * @return the torso
	 */
	public Vector3d getTorso() {
		return torso;
	}

	/**
	 * @param torso
	 *            the torso to set
	 */
	public void setTorso(Vector3d torso) {
		this.torso = torso;
	}

	/**
	 * @return the rightHand
	 */
	public Vector3d getRightHand() {
		return rightHand;
	}

	/**
	 * @param rightHand
	 *            the rightHand to set
	 */
	public void setRightHand(Vector3d rightHand) {
		this.rightHand = rightHand;
	}

	/**
	 * @return the rightElbow
	 */
	public Vector3d getRightElbow() {
		return rightElbow;
	}

	/**
	 * @param rightElbow
	 *            the rightElbow to set
	 */
	public void setRightElbow(Vector3d rightElbow) {
		this.rightElbow = rightElbow;
	}

	/**
	 * @return the rightShoulder
	 */
	public Vector3d getRightShoulder() {
		return rightShoulder;
	}

	/**
	 * @param rightShoulder
	 *            the rightShoulder to set
	 */
	public void setRightShoulder(Vector3d rightShoulder) {
		this.rightShoulder = rightShoulder;
	}

	/**
	 * @return the rightHip
	 */
	public Vector3d getRightHip() {
		return rightHip;
	}

	/**
	 * @param rightHip
	 *            the rightHip to set
	 */
	public void setRightHip(Vector3d rightHip) {
		this.rightHip = rightHip;
	}

	/**
	 * @return the rightKnee
	 */
	public Vector3d getRightKnee() {
		return rightKnee;
	}

	/**
	 * @param rightKnee
	 *            the rightKnee to set
	 */
	public void setRightKnee(Vector3d rightKnee) {
		this.rightKnee = rightKnee;
	}

	/**
	 * @return the rightFoot
	 */
	public Vector3d getRightFoot() {
		return rightFoot;
	}

	/**
	 * @param rightFoot
	 *            the rightFoot to set
	 */
	public void setRightFoot(Vector3d rightFoot) {
		this.rightFoot = rightFoot;
	}

}