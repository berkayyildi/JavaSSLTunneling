package com.berkay.ssltunnel;

import java.awt.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.KeyStore;
import javax.net.*;
import javax.net.ssl.*;
import java.util.HashMap;
import java.util.Properties;

/*
 * listenIn den oku (4445) -> secureOut a yaz
 * secureIn den oku (444) -> listenOut a yaz
 */

public class SSLClient extends Thread{

	private HashMap<Socket, String> hashMap = new HashMap<Socket, String>();

	private int ClientSecureSocketListenPort;
	private int ClientLocalListenPort;
	private String destinationIp;
	private String Key;

	private DataInputStream listenIn;
	private DataOutputStream listenOut;
	private DataInputStream secureIn;
	private DataOutputStream secureOut;

	public SSLClient(int ClientSecureSocketListenPort, int ClientLocalListenPort, String destinationIp, String Key) {
		this.ClientSecureSocketListenPort = ClientSecureSocketListenPort;
		this.ClientLocalListenPort = ClientLocalListenPort;
		this.destinationIp = destinationIp;
		this.Key = Key;

	}




	public void run() {

		TrayManager tm = new TrayManager("Secure Socket Client(" + destinationIp + ":" + ClientLocalListenPort + ")");	//Tray Icon Yarat

		Properties systemProps = System.getProperties();
		systemProps.put("javax.net.ssl.trustStore", Key);


		int i = 0;

		try {

			ServerSocket listenSocketPort = new ServerSocket(ClientLocalListenPort);

			while (true) {
				Socket socket = listenSocketPort.accept();
				System.out.println("Client New: " + socket.getPort());
				tm.setConnected();
				i++;
				hashMap.put(socket, "Client " + i);
				new SSLClientHelper(socket,ClientSecureSocketListenPort,destinationIp,Key).start();
			}
		} catch (Exception exception) {
			exception.printStackTrace();
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
