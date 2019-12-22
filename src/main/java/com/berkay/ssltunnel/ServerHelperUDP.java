package com.berkay.ssltunnel;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.Arrays;

/* YAPILANLAR:
 * bReader dan okudugunu listenOut'a yaz
 * listenIn den geleni bWriter a yaz
 *
 */

public class ServerHelperUDP extends Thread {

    private Socket secure_socket;
    private int ServerLocalListenPort;


    DatagramSocket clientSocket;    //Yeni

    public ServerHelperUDP(Socket incoming, int ServerLocalListenPort) {
        this.secure_socket = incoming;
        this.ServerLocalListenPort = ServerLocalListenPort;

    }

    int fromByteArray(byte[] bytes) {
        return ((bytes[0] & 0xFF) << 24) |
                ((bytes[1] & 0xFF) << 16) |
                ((bytes[2] & 0xFF) << 8 ) |
                ((bytes[3] & 0xFF) << 0 );
    }

    public void run() {
        try {

            DataInputStream bReader = new DataInputStream(new BufferedInputStream(secure_socket.getInputStream()));
            DataOutputStream bWriter = new DataOutputStream(new BufferedOutputStream(secure_socket.getOutputStream()));

            clientSocket = new DatagramSocket();

            Runnable run2 = () -> {
                try {

                    int IN;
                    byte[] receivedData = new byte[1024];
                    while ((IN = bReader.read(receivedData)) != -1){	//Read until return -1

                        byte[] lenght = Arrays.copyOfRange(receivedData, 0, 4);    //Ilk dor bytei sil
                        int uzunluk = fromByteArray(lenght);
                        byte[] filteredByteArray = Arrays.copyOfRange(receivedData, 4, receivedData.length - 4);    //Ilk dor bytei sil
                        clientSocket.send(new DatagramPacket(filteredByteArray, uzunluk, InetAddress.getByName("127.0.0.1"), ServerLocalListenPort));
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }

            };
            new Thread(run2).start();



        try {

            while (true){	//Read until return -1

                byte[] receiveData = new byte[1024];
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                clientSocket.receive(receivePacket);

                //String modifiedSentence = new String(receivePacket.getData());
                //System.out.println("SERVER:" + modifiedSentence);
                //System.out.println("GeriGLen: " + receivePacket.getLength());

                byte[] bytelen = ByteBuffer.allocate(4).putInt(receivePacket.getLength()).array();

                byte[] concatted = new byte[4 + receivePacket.getLength()];
                System.arraycopy(bytelen, 0, concatted, 0, 4);
                System.arraycopy(receiveData, 0, concatted, 4, receivePacket.getLength());

                bWriter.write(concatted);
                bWriter.flush();


            }

        } catch (IOException e) {
            e.printStackTrace();
        }





        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}