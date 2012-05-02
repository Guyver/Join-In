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

package control;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Semaphore;

import org.OpenNI.SkeletonJoint;

import services.KinectUserActionServiceEvent;

import services.KinectSkeletonServiceEvent;

import com.google.gson.Gson;



/**
 * This class maps the data of the device events and codifies it under the GSON/JSON protocol before sending it to the 
 * specified IP address (and port) through a socket.
 * @author Santiago Hors Fraile
 *
 */
public class SharedOutput {
	/**
	 * This object is the only object that can exist of this class. This object
	 * has to be the one used to send the events through the socket to the remote server.
	 */
	static private SharedOutput rc = null;
	/**
	 * This field represents the SocketManager of the socket we have stablished the communication through.
	 */
	private SocketManager sm = null;
	
	private static Semaphore  sem=null;
	
	/**
	 * 
	 * Returns a new instance of SharedOutupu if it did not exist or the
	 * instance that was created if there was a created instance already.
	 * 
	 * @return SharedOutput
	 */
	static public SharedOutput getSharedOutput() {
		
	        if (rc == null) {
	            rc = new SharedOutput();
	            sem= new Semaphore(1, true);
	        }
	        return rc;
     }

	/**
	 * This function sends any type of data device data event through the socket. It is mapped and codified under the GSON/JSON protocol before it is sent.
	 * It is mandatory to get any message from the other side of the socket each time that we are going to send data.
	 * @param se The device data event (of any type) that we want to send information about.
	 */
	public void performTransference(IEventCommModule se){
	
		
		while (sm == null) {
			try {
				
				sm = SocketManager.getSocket(DeviceManager.getDeviceManager().getIpAddress(), DeviceManager.getDeviceManager().getPort());
			} catch (Exception e) {
				sm = null;
			}
		}
		try {
	
			boolean validEvent=false;
			
			Gson gson = new Gson();
			String s ="";
			Map<Object, Object> map = new HashMap<Object, Object>();
			
			if(se instanceof KinectSkeletonServiceEvent){
				validEvent=true;
				
				
				map.put("device", "kinect");
				map.put("userID", ((KinectSkeletonServiceEvent) se).getUserId());
				map.put("type", "jointPosition");
				map.put(SkeletonJoint.HEAD, ((KinectSkeletonServiceEvent) se).getHead());
				map.put(SkeletonJoint.LEFT_ELBOW, ((KinectSkeletonServiceEvent) se).getLeftElbow());
				map.put(SkeletonJoint.LEFT_FOOT, ((KinectSkeletonServiceEvent) se).getLeftFoot());
				map.put(SkeletonJoint.LEFT_HAND, ((KinectSkeletonServiceEvent) se).getLeftHand());
				map.put(SkeletonJoint.LEFT_HIP, ((KinectSkeletonServiceEvent) se).getLeftHip());
				map.put(SkeletonJoint.LEFT_KNEE, ((KinectSkeletonServiceEvent) se).getLeftKnee());
				map.put(SkeletonJoint.LEFT_SHOULDER, ((KinectSkeletonServiceEvent) se).getLeftShoulder());
				map.put(SkeletonJoint.NECK, ((KinectSkeletonServiceEvent) se).getNeck());
				map.put(SkeletonJoint.RIGHT_ELBOW, ((KinectSkeletonServiceEvent) se).getRightElbow());
				map.put(SkeletonJoint.RIGHT_FOOT, ((KinectSkeletonServiceEvent) se).getRightFoot());
				map.put(SkeletonJoint.RIGHT_HAND, ((KinectSkeletonServiceEvent) se).getRightHand());
				map.put(SkeletonJoint.RIGHT_HIP, ((KinectSkeletonServiceEvent) se).getRightHip());
				map.put(SkeletonJoint.RIGHT_KNEE, ((KinectSkeletonServiceEvent) se).getRightKnee());
				map.put(SkeletonJoint.RIGHT_SHOULDER, ((KinectSkeletonServiceEvent) se).getRightShoulder());
				map.put(SkeletonJoint.TORSO, ((KinectSkeletonServiceEvent) se).getTorso());
				
			}
			
			if(se instanceof KinectUserActionServiceEvent){
				validEvent=true;
				
				map.put("device", "kinect");
				map.put("userID", ((KinectUserActionServiceEvent) se).getUserId());
				map.put("type", "action");
				map.put("action", ((KinectUserActionServiceEvent)se).getUserAction());
				
			}
		
			
			if(validEvent){	
				s+= gson.toJson(map);
				s+='\n';
			
				try {
					sem.acquire();
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				String dummyReceivedMessage= sm.readMessage();
				sm.sendMessage(s);
				sem.release();
				if(se instanceof KinectUserActionServiceEvent){
				System.out.println("Sent: "+s.toString());
				}
			}
			
		} catch (Exception e) {
	
			e.printStackTrace();
		}
	

		
		
	}
}