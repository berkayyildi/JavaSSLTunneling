import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.KeyStore;
import javax.net.*;
import javax.net.ssl.*;
import java.util.Properties;

/*
 * listenIn den oku (4445) -> secureOut a yaz
 * secureIn den oku (444) -> listenOut a yaz
 */

public class SSLSocketClient {
	
	static DataInputStream listenIn;
	static DataOutputStream listenOut;
	static DataInputStream secureIn;
	static DataOutputStream secureOut;

    public static void main(String[] args) throws Exception {


    Properties systemProps = System.getProperties();
    systemProps.put("javax.net.ssl.trustStore", "keystore.ImportKey");
        try {
        	
        	@SuppressWarnings("resource")
        	ServerSocket listenSocketPort = new ServerSocket(4445);
        	Socket listen_socket = listenSocketPort.accept();
        	
            
			listenIn = new DataInputStream(new BufferedInputStream(listen_socket.getInputStream()));
			listenOut=new DataOutputStream(listen_socket.getOutputStream());	// INPUT STREAM
        	
        	
        	
            SSLSocketFactory factory = getSSLSocketFactory("TLS");
            SSLSocket secure_socket = (SSLSocket)factory.createSocket("localhost", 444);
            
            secureIn = new DataInputStream(new BufferedInputStream(secure_socket.getInputStream()));
            secureOut=new DataOutputStream(secure_socket.getOutputStream());	// INPUT STREAM
            
            secure_socket.startHandshake();

  
            
            
            
            Runnable run2 = new Runnable() {
                public void run() {
                    try {
                    	
                    	//bReader dan okur listenOut a yazar
                        int IN=0; 
                        byte[] receivedData = new byte[999999999];
                        while ((IN = listenIn.read(receivedData)) != -1){	//Read until return -1
                        	secureOut.write(receivedData,0,IN);
                        	secureOut.flush();
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
                         while ((OUT = secureIn.read(sendedData)) != -1){	//Read until return -1
                         	listenOut.write(sendedData,0,OUT);
                         	listenOut.flush();
                         }
                        
                      
                         

                     } catch (IOException e) {
 						// TODO Auto-generated catch block
 						e.printStackTrace();
 					}
                 }
              };
              new Thread(run).start();
  
              
              

 
            //secureIn.close();
            //secureOut.close();
            //secure_socket.close();
            
            //fileNametoGet = scan.next();	//WAIT UNTIL INPUT (For MultiThread Test)

        } catch (Exception e) {
            e.printStackTrace();
        }
    }



	private static SSLSocketFactory getSSLSocketFactory(String type) {
		if (type.equals("TLS")) {
		    SocketFactory ssf = null;
		    try {
			SSLContext ctx;
		        KeyManagerFactory kmf;
		        KeyStore ks;
		        char[] passphrase = "importkey".toCharArray();

		        ctx = SSLContext.getInstance("TLS");
		        kmf = KeyManagerFactory.getInstance("SunX509");
		        ks = KeyStore.getInstance("JKS");

		        ks.load(new FileInputStream("keystore.ImportKey"), passphrase);
		        kmf.init(ks, passphrase);

			ctx.init(kmf.getKeyManagers(), null, null);

		        ssf = ctx.getSocketFactory();
		        return (SSLSocketFactory) ssf;
		    } catch (Exception e) {
		        e.printStackTrace();
		    }
		} else {
		    return (SSLSocketFactory) SSLSocketFactory.getDefault();
		}
		return null;
        }
	
}
