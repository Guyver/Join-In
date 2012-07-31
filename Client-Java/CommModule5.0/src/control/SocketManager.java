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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.UnknownHostException;
/**
 * This class manages the creation of the communication socket
 * @author Santiago Hors Fraile
 *
 */
public class SocketManager {
	
	static private SocketManager su = null;
	
	static Socket socket;
/**
 * This function creates a new SocketManager to the given ipAddress and port if it was not created already. If it was, it returns the previous Socket.
 * @param ipAddress The string IP address. For example "192.57.311.214".
 * @param port The port. For example: 2441.
 * @return The SocketManager to handle sending and receiving messages.
 */
	static public SocketManager getSocket(String ipAddress, int port) {
		if (su == null) {
	            su = new SocketManager();           
	        	try {      		
	    			socket = new Socket(ipAddress, port);		  
	    		} catch (UnknownHostException e) {
	    			e.printStackTrace();
	    		} catch (IOException e) {
	    			e.printStackTrace();
	    		}        
	    }
	    return su;
     }
		
/**
 * This function sends a String through the socket.
 * @param message The String message that is going to be sent.
 * @throws IOException
 */
	public void sendMessage(String message) throws IOException {
		
		socket.getOutputStream().write(message.getBytes("UTF-8"));
		socket.getOutputStream().flush();
	}
/**
 * This function waits until a message arrives to this part of the socket, reads it an returns it as String
 * @return The message that has been sent by the other part of the socket.
 * @throws IOException 
 */
	public String readMessage() throws IOException {
		InputStream is = socket.getInputStream();
		int curr = -1;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		while ((curr = is.read()) != -1) {
			if (curr == '\n') {
				break;
			}
			baos.write(curr);
			
		}
		
		return baos.toString("UTF-8");
	}

/**
 * This function returns the Socket object that the SocketManager has created.
 * @return The socket.
 */
	public Socket getSocket(){
		return socket;
	}
	
}