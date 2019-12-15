package com.berkay.ssltunnel;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.KeyStore;
import javax.net.*;
import javax.net.ssl.*;
import java.util.HashMap;
import java.util.Properties;


/* YAPILANLAR:
 * bReader dan okudugunu listenOut'a yaz
 * listenIn den geleni bWriter a yaz
 *
 */


public class SSLClientHelper extends Thread {

    private int ClientSecureSocketListenPort;
    private int ClientLocalListenPort;
    private String destinationIp;
    private String Key;

    private DataInputStream listenIn;
    private DataOutputStream listenOut;
    private DataInputStream secureIn;
    private DataOutputStream secureOut;

    Socket listen_socket;

    public SSLClientHelper(Socket listen_socket, int ClientSecureSocketListenPort, String destinationIp, String Key) {
        this.listen_socket = listen_socket;
        this.ClientSecureSocketListenPort = ClientSecureSocketListenPort;
        this.destinationIp = destinationIp;
        this.Key = Key;
    }

    public void run() {
        try {


            Properties systemProps = System.getProperties();
            systemProps.put("javax.net.ssl.trustStore", Key);


                listenIn = new DataInputStream(new BufferedInputStream(listen_socket.getInputStream()));
                listenOut=new DataOutputStream(new BufferedOutputStream(listen_socket.getOutputStream()));	// INPUT STREAM

                SSLSocketFactory factory = getSSLSocketFactory("TLS");
                SSLSocket secure_socket = (SSLSocket)factory.createSocket(destinationIp, ClientSecureSocketListenPort);

                secureIn = new DataInputStream(new BufferedInputStream(secure_socket.getInputStream()));
                secureOut=new DataOutputStream(new BufferedOutputStream(secure_socket.getOutputStream()));	// INPUT STREAM



                Runnable run2 = new Runnable() {
                    public void run() {
                        try {

                            //listenIn dan okur secureOut a yazar
                            int IN=0;
                            while ((IN = listenIn.read()) != -1){	//Read until return -1
                                secureOut.write(IN);
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


                            //secureIn den okur listenOut a yazar
                            int IN=0;
                            while ((IN = secureIn.read()) != -1){	//Read until return -1
                                listenOut.write(IN);
                                listenOut.flush();
                            }




                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                };
                new Thread(run).start();




            //secure_socket.close();
            //ss.close();


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