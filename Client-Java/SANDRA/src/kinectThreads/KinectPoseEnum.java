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

/**
 * This enum lists all the different poses that can be recognised by the Kinect. The 'Phi' pose to be tracked is not included. 
 * @author Santiago Hors Fraile
 *
 */
public enum KinectPoseEnum {
	STAND(0),
	WALK_LEFT_LEG_UP(1),
	WALK_RIGHT_LEG_UP(2),
	LEFT_SHOULDER_LOWER_AND_CLOSER(3),
	RIGHT_SHOULDER_LOWER_AND_CLOSER(4),
	OPENED_HUG(5),
	CLOSED_HUG(6),
	CROSSED_HANDS_ABOVE_SHOULDERS(7),
	RIGHT_HAND_BENEATH_RIGHT_ELBOW_SEPARATED_FROM_RIGHT_HIP(8),
	LEFT_HAND_BENEATH_LEFT_ELBOW_SEPARATED_FROM_LEFT_HIP(9),
	RIGHT_HAND_ABOVE_RIGHT_SHOULDER(10),
	LEFT_HAND_ABOVE_LEFT_SHOULDER(11),
	HANDS_ABOVE_SHOULDERS_PSI_POSE(12);

	private int code;

	private KinectPoseEnum(int code) 
	{  this.code = code; }

	/**
	 * @return The numeric code assigned to the pose.
	 */
	public short getCode() 
	{ return (short)code;  }
}
