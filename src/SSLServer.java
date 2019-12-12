import java.io.*;
import java.net.*;
import java.security.KeyStore;
import java.util.HashMap;

import javax.net.*;
import javax.net.ssl.*;

public class SSLServer extends Thread{
	
	private int ServerSecureSocketListenPort;
	private int ServerLocalListenPort;
	
	private HashMap<Socket, String> hashMap = new HashMap<Socket, String>();

	public SSLServer(int ServerSecureSocketListenPort, int ServerLocalListenPort) {
		this.ServerSecureSocketListenPort = ServerSecureSocketListenPort;
		this.ServerLocalListenPort = ServerLocalListenPort;
	}

	public void run() {

		new TrayManager("Secure Socket Server(" + ServerLocalListenPort + ")");	//Tray Icon Yarat

		
		int i = 0;
		
		try {
			
			ServerSocketFactory ssf = SSLServer.getServerSocketFactory("TLS");
			ServerSocket ss = ssf.createServerSocket(ServerSecureSocketListenPort);
						
			while (true) {
				Socket socket = ss.accept();
				i++;
				hashMap.put(socket, "Client " + i);
				new ServerHelper(socket,ServerLocalListenPort).start();
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		}
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
