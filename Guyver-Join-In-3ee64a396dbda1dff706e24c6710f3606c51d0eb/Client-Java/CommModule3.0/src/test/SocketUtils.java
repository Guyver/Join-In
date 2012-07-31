package test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.UnknownHostException;




public class SocketUtils {
	
	static private SocketUtils su = null;
	
	static Socket nodejs;

	static public SocketUtils getSocket(String ipAddress, int port) {
	

		if (su == null) {
	            su = new SocketUtils();
	            
	        	try {
	        		
	    			nodejs = new Socket(ipAddress, port);
	    		
	    			
	    			
	    		  
	    		} catch (UnknownHostException e) {
	    			// TODO Auto-generated catch block
	    			e.printStackTrace();
	    		} catch (IOException e) {
	    			// TODO Auto-generated catch block
	    			e.printStackTrace();
	    		}
	    }
	    return su;
     }
		
    public void sendMessage(String message) throws IOException {

        nodejs.getOutputStream().write(message.getBytes("UTF-8"));
        nodejs.getOutputStream().flush();
    }


    public String readMessage() throws IOException {
        InputStream is = nodejs.getInputStream();
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

}
