package com.berkay.ssltunnel;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.awt.*;
import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.security.KeyStore;
import java.util.Arrays;
import java.util.Properties;

public class ClientUDP extends Thread{

    private int ClientSecureSocketListenPort;
    private int ClientLocalListenPort;
    private String destinationIp;
    private SSLContext ctx;


    private DataInputStream listenIn;
    private DataOutputStream listenOut;
    private DataInputStream secureIn;
    private DataOutputStream secureOut;

    DatagramPacket globpacket = null;

    int PortNo = 0;

    public ClientUDP(int ClientSecureSocketListenPort, int ClientLocalListenPort, String destinationIp, String Key) {
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

    int fromByteArray(byte[] bytes) {
        return ((bytes[0] & 0xFF) << 24) |
                ((bytes[1] & 0xFF) << 16) |
                ((bytes[2] & 0xFF) << 8 ) |
                ((bytes[3] & 0xFF) << 0 );
    }


    public void run() {

        TrayManager tm = new TrayManager("Secure Socket UDP Client(" + destinationIp + ":" + ClientLocalListenPort + ")");	//Tray Icon Yarat

        try {


            SSLSocketFactory factory = ctx.getSocketFactory();
            SSLSocket secure_socket = (SSLSocket)factory.createSocket(destinationIp, ClientSecureSocketListenPort);

            secureIn = new DataInputStream(new BufferedInputStream(secure_socket.getInputStream()));
            secureOut= new DataOutputStream(new BufferedOutputStream(secure_socket.getOutputStream()));	// INPUT STREAM

            DatagramSocket dsocket = new DatagramSocket(ClientLocalListenPort);

            Runnable run2 = () -> {
                try {

                    int IN;
                    byte[] receivedData = new byte[1024];
                    while ((IN = secureIn.read(receivedData)) != -1){	//Read until return -1

                        byte[] lenght = Arrays.copyOfRange(receivedData, 0, 4);    //Ilk dor bytei sil
                        int uzunluk = fromByteArray(lenght);
                        byte[] filteredByteArray = Arrays.copyOfRange(receivedData, 4, receivedData.length - 4);    //Ilk dor bytei sil

                        //String msg = new String(filteredByteArray, 0, uzunluk); // Convert the contents to a string, and display them
                        //System.out.println("CLIENT_SSL_GELEN:" + msg);
                        //System.out.println("CUzunluk: " + uzunluk);

                        dsocket.send(new DatagramPacket(filteredByteArray, uzunluk, InetAddress.getByName("127.0.0.1"), PortNo));   //portno save edileni aldigi icin stuck kalkiyor
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            };
            new Thread(run2).start();



            while (true) {  // Now loop forever, waiting to receive packets and printing them.

                byte[] buffer = new byte[1024]; // Create a buffer to read datagrams into. If a packet is larger than this buffer, the excess will simply be discarded!

                globpacket = new DatagramPacket(buffer, buffer.length); // Create a packet to receive data into the buffer
                dsocket.receive(globpacket); // Wait to receive a datagram

                PortNo = globpacket.getPort();
                //System.out.println("PortNo: " + PortNo);

                String msg = new String(buffer, 0, globpacket.getLength()); // Convert the contents to a string, and display them
                //System.out.println("CLIENT_UDP_GELEN:" + msg);

                byte[] bytelen = ByteBuffer.allocate(4).putInt(globpacket.getLength()).array();

                //System.out.println("Gonderi_Uzunlugu:" + packet.getLength());

                byte[] concatted = new byte[4 + globpacket.getLength()];
                System.arraycopy(bytelen, 0, concatted, 0, 4);
                System.arraycopy(buffer, 0, concatted, 4, globpacket.getLength());

                secureOut.write(concatted);
                secureOut.flush();

            }


        } catch (Exception exception) {
            tm.setColor(Color.red);
            exception.printStackTrace();
        }
    }

}
