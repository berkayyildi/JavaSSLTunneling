import java.io.*;
import java.security.KeyStore;
import javax.net.*;
import javax.net.ssl.*;
import java.util.Properties;
import java.util.Scanner;


public class SSLSocketClient {

    public static void main(String[] args) throws Exception {


    Properties systemProps = System.getProperties();
    systemProps.put("javax.net.ssl.trustStore", "keystore.ImportKey");
        try {
            SSLSocketFactory factory = getSSLSocketFactory("TLS");
            SSLSocket socket =
                (SSLSocket)factory.createSocket("localhost", 9999);

            socket.startHandshake();

            PrintWriter out = new PrintWriter(
                                  new BufferedWriter(
                                  new OutputStreamWriter(
                                  socket.getOutputStream())));
            

            System.out.print("Enter File Name: ");
            
            @SuppressWarnings("resource")
			Scanner scan = new Scanner(System.in);
            String fileNametoGet = scan.next();
            

            out.print(fileNametoGet + "\n");
            out.flush();

            if (out.checkError())
                System.out.println(
                    "SSLSocketClient:  java.io.PrintWriter error");

            
            /* read response */
            DataInputStream in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            
            int length = 0;
            try {
            	length = in.readInt();	//Read length
            }
            catch (Exception e) {
				System.out.println("File Not Found");
			}
            
 
            @SuppressWarnings("resource")
			BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream("received_" + fileNametoGet));
            int IN=0; 
            byte[] receivedData = new byte[length];
            while ((IN = in.read(receivedData)) != -1){	//Read until return -1
            	bos.write(receivedData,0,IN);
            }
            
            bos.close();	//Bu text dosya olayini cozdu
            in.close();
            out.close();
            socket.close();
            
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
