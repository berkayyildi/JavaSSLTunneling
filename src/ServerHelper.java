import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;


/* YAPILANLAR:
 * bReader dan okudugunu listenOut'a yaz
 * listenIn den geleni bWriter a yaz
 * 
 */


public class ServerHelper extends Thread {

		private Socket secure_socket;

		public ServerHelper(Socket incoming) {
			this.secure_socket = incoming;
		}

		public void run() {
			try {
				
				
				

				DataInputStream bReader = new DataInputStream(new BufferedInputStream(secure_socket.getInputStream()));
				DataOutputStream bWriter = new DataOutputStream(secure_socket.getOutputStream());
				
				
				@SuppressWarnings("resource")
				Socket listen_socket=new Socket("localhost",4444);	//PROXY PORTU
				DataInputStream listenIn = new DataInputStream(new BufferedInputStream(listen_socket.getInputStream()));
				DataOutputStream listenOut=new DataOutputStream(listen_socket.getOutputStream());	//PROXY INPUT STREAM
				

	
	            
	            Runnable run2 = new Runnable() {
	                public void run() {
	                    try {
	                    	
	            			//bReader dan okur listenOut a yazar
	        	            int IN=0; 
	        	            byte[] receivedData = new byte[999999999];
	        	            while ((IN = bReader.read(receivedData)) != -1){	//Read until return -1
	        	            	listenOut.write(receivedData,0,IN);
	        	            	listenOut.flush();
	        	            }
	                        

	                    } catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
	                }
	             };
	             new Thread(run2).start();

	           

	            Runnable run = new Runnable() {
	                public void run() {
	                    try {
	                    	
	                    	//listenIn den okur bWriter a yazar
	        	            int OUT=0; 
	        	            byte[] sendedData = new byte[999999999];
	        	            while ((OUT = listenIn.read(sendedData)) != -1){	//Read until return -1
	        	            	bWriter.write(sendedData,0,OUT);
	        	            	bWriter.flush();
	        	            }
	        	            
	                        

	                    } catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
	                }
	             };
	             new Thread(run).start();
	             
	            
	          

				
				
				//secure_socket.close();
				//ss.close();
				
				
			} catch (Exception exception) {
				exception.printStackTrace();
			}
		}
	}