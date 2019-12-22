package com.berkay.ssltunnel;

import java.io.*;
import java.net.Socket;
import javax.net.ssl.*;

/* YAPILANLAR:
 * bReader dan okudugunu listenOut'a yaz
 * listenIn den geleni bWriter a yaz
 *
 */

public class ClientHelper extends Thread {

    private Socket listen_socket;
    private int ClientSecureSocketListenPort;
    private String destinationIp;
    private SSLContext ctx;

    private DataInputStream listenIn;
    private DataOutputStream listenOut;
    private DataInputStream secureIn;
    private DataOutputStream secureOut;



    public ClientHelper(Socket listen_socket, int ClientSecureSocketListenPort, String destinationIp, SSLContext ctx) {
        this.listen_socket = listen_socket;
        this.ClientSecureSocketListenPort = ClientSecureSocketListenPort;
        this.destinationIp = destinationIp;
        this.ctx = ctx;
    }

    public void run() {
        try {

                listenIn = new DataInputStream(new BufferedInputStream(listen_socket.getInputStream()));
                listenOut= new DataOutputStream(new BufferedOutputStream(listen_socket.getOutputStream()));	// INPUT STREAM

                SSLSocketFactory factory = ctx.getSocketFactory();
                SSLSocket secure_socket = (SSLSocket)factory.createSocket(destinationIp, ClientSecureSocketListenPort);

                secureIn = new DataInputStream(new BufferedInputStream(secure_socket.getInputStream()));
                secureOut= new DataOutputStream(new BufferedOutputStream(secure_socket.getOutputStream()));	// INPUT STREAM


                Runnable run2 = () -> {
                    try {

                        int IN;
                        byte[] receivedData = new byte[1024];
                        while ((IN = listenIn.read(receivedData)) != -1){	//Read until return -1
                            secureOut.write(receivedData,0,IN);
                            secureOut.flush();
                        }

                    } catch (IOException e) {
                        //e.printStackTrace();
                        //System.out.println("Socket Aniden Kapatildi");
                    }
                };
                new Thread(run2).start();


            try {

                int IN;
                byte[] receivedData = new byte[1024];
                while ((IN = secureIn.read(receivedData)) != -1){	//Read until return -1
                    listenOut.write(receivedData,0,IN);
                    listenOut.flush();
                }

            } catch (IOException e) {
                //e.printStackTrace();
                //System.out.println("Socket Aniden Kapatildi");
            }
            secure_socket.close();
            listen_socket.close();
            Thread.currentThread().interrupt();
            return;

            //ss.close();

        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }




}