package com.berkay.ssltunnel;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import java.awt.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.KeyStore;
import java.util.Properties;

/*
 * listenIn den oku (4445) -> secureOut a yaz
 * secureIn den oku (444) -> listenOut a yaz
 */

public class Client extends Thread{

	private int ClientSecureSocketListenPort;
	private int ClientLocalListenPort;
	private String destinationIp;
	private SSLContext ctx;

	public Client(int ClientSecureSocketListenPort, int ClientLocalListenPort, String destinationIp, String Key) {
		this.ClientSecureSocketListenPort = ClientSecureSocketListenPort;
		this.ClientLocalListenPort = ClientLocalListenPort;
		this.destinationIp = destinationIp;

		//private static SSLSocketFactory getSSLSocketFactory(String type) { fonsiyonu bu

		Properties systemProps = System.getProperties();
		systemProps.put("javax.net.ssl.trustStore", Key);

		KeyManagerFactory kmf;
		KeyStore ks;
		char[] passphrase = "importkey".toCharArray();

		try {
			ctx = SSLContext.getInstance("TLS");
			kmf = KeyManagerFactory.getInstance("SunX509");
			ks = KeyStore.getInstance("JKS");

			ks.load(new FileInputStream(Key), passphrase);
			kmf.init(ks, passphrase);

			ctx.init(kmf.getKeyManagers(), null, null);
		} catch (Exception e) {
			e.printStackTrace();
		}

		//-------------------------------------

	}

	public void run() {

		TrayManager tm = new TrayManager("Secure Socket TCP Client(" + destinationIp + ":" + ClientLocalListenPort + ")");	//Tray Icon Yarat

		try {

			ServerSocket listenSocketPort = new ServerSocket(ClientLocalListenPort);	//4445

			while (true) {
				Socket socket = listenSocketPort.accept();
				System.out.println("Client New: " + socket.getPort());	//4445 e gelen porta atanmış yeni
				tm.setColor(Color.green);

				new ClientHelper(socket,ClientSecureSocketListenPort,destinationIp,ctx).start();
			}
		} catch (Exception exception) {
			tm.setColor(Color.red);
			exception.printStackTrace();
		}
	}

}
