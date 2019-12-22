package com.berkay.ssltunnel;

import java.io.*;
import java.net.Socket;

/* YAPILANLAR:
 * bReader dan okudugunu listenOut'a yaz
 * listenIn den geleni bWriter a yaz
 * 
 */

public class ServerHelper extends Thread {

		private Socket secure_socket;
		private int ServerLocalListenPort;
		private String destinationIp;

		public ServerHelper(Socket incoming, int ServerLocalListenPort, String destinationIp) {
			this.secure_socket = incoming;
			this.ServerLocalListenPort = ServerLocalListenPort;
			this.destinationIp = destinationIp;
		}

		public void run() {
			try {

				DataInputStream bReader = new DataInputStream(new BufferedInputStream(secure_socket.getInputStream()));
				DataOutputStream bWriter = new DataOutputStream(new BufferedOutputStream(secure_socket.getOutputStream()));

				@SuppressWarnings("resource")
				Socket listen_socket=new Socket(destinationIp,ServerLocalListenPort);	//PROXY PORTU
				DataInputStream listenIn = new DataInputStream(new BufferedInputStream(listen_socket.getInputStream()));
				DataOutputStream listenOut=new DataOutputStream(new BufferedOutputStream(listen_socket.getOutputStream()));	//PROXY INPUT STREAM

				System.out.println("Server New: " + listen_socket);

	            Runnable run2 = () -> {
					try {

						int IN;
						byte[] receivedData = new byte[1024];
						while ((IN = bReader.read(receivedData)) != -1){	//Read until return -1
							listenOut.write(receivedData,0,IN);
							listenOut.flush();
						}

					} catch (IOException e) {
						//e.printStackTrace();
						//System.out.println("Socket Aniden Kapatildi");
						Thread.currentThread().interrupt();
						return;
					}

				};
	             new Thread(run2).start();



				try {

					int IN;
					byte[] receivedData = new byte[1024];
					while ((IN = listenIn.read(receivedData)) != -1){	//Read until return -1
						bWriter.write(receivedData,0,IN);
						bWriter.flush();
					}

				} catch (IOException e) {
					//e.printStackTrace();
					//System.out.println("Socket Aniden Kapatildi");
					Thread.currentThread().interrupt();
					return;
				}


				secure_socket.close();
				listen_socket.close();
				Thread.currentThread().interrupt();
				return;
				
				
			} catch (Exception exception) {
				exception.printStackTrace();
			}
		}
	}