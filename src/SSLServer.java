import java.io.*;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyStore;
import javax.net.*;
import javax.net.ssl.*;

public class SSLServer {
	
	static String fileNameToRead = "";

	public static void main(String[] args) throws Exception {

		ServerSocketFactory ssf = SSLServer.getServerSocketFactory("TLS");
		ServerSocket ss = ssf.createServerSocket(9999);

		Socket s = ss.accept();
		
		BufferedReader bReader = new BufferedReader(new InputStreamReader(s.getInputStream()));
		DataOutputStream bWriter = new DataOutputStream(s.getOutputStream());

		fileNameToRead = bReader.readLine();
		
		byte[] fileContent = null;
		
		 try {
			 fileContent = Files.readAllBytes(Paths.get(fileNameToRead));
	        } catch (Exception Hata) {
	            System.out.println(" Ýþlem yapýlýrken matematiksel bir hata oluþtu :" + Hata.getMessage());
	            s.close();
	    		ss.close();
	    		System.exit(0);
	        }
		 
		bWriter.writeInt(fileContent.length);
		bWriter.write(fileContent);	//byte yapildi
		
		s.close();
		ss.close();

	}

	private static ServerSocketFactory getServerSocketFactory(String type) {
		if (type.equals("TLS")) {
		    SSLServerSocketFactory ssf = null;
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

		        ssf = ctx.getServerSocketFactory();
		        return ssf;
		    } catch (Exception e) {
		        e.printStackTrace();
		    }
		} else {
		    return ServerSocketFactory.getDefault();
		}
		return null;
        }
}
