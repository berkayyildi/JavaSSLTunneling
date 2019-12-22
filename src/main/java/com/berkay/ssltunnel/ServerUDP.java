package com.berkay.ssltunnel;

import java.awt.*;
import java.io.*;
import java.net.*;
import java.security.KeyStore;
import javax.net.*;
import javax.net.ssl.*;

public class ServerUDP extends Thread{

    private int ServerSecureSocketListenPort;
    private int ServerLocalListenPort;
    private String destinationIp;
    private String Key;

    private ServerSocketFactory ssf;

    public ServerUDP(int ServerSecureSocketListenPort, int ServerLocalListenPort, String destinationIp, String Key) throws Exception{
        this.ServerSecureSocketListenPort = ServerSecureSocketListenPort;
        this.ServerLocalListenPort = ServerLocalListenPort;
        this.destinationIp = destinationIp;
        this.Key = Key;

        //private ServerSocketFactory getServerSocketFactory(String type) { Fonksiyonu bu
        SSLContext ctx;
        KeyManagerFactory kmf;
        KeyStore ks;
        char[] passphrase = "importkey".toCharArray();

        ctx = SSLContext.getInstance("TLS");
        kmf = KeyManagerFactory.getInstance("SunX509");
        ks = KeyStore.getInstance("JKS");

        ks.load(new FileInputStream(Key), passphrase);
        kmf.init(ks, passphrase);
        ctx.init(kmf.getKeyManagers(), null, null);

        ssf = ctx.getServerSocketFactory();
        //-----------------------------------
    }

    public void run() {

        TrayManager tm = new TrayManager("Secure Socket UDP Server(" + destinationIp + ":" + ServerLocalListenPort + ")");	//Tray Icon Yarat

        try {

            ServerSocket ss = ssf.createServerSocket(ServerSecureSocketListenPort);	//443

            while (true) {
                Socket socket = ss.accept();
                tm.setColor(Color.green);

                new ServerHelperUDP(socket,ServerLocalListenPort).start();
            }
        } catch (Exception exception) {
            tm.setColor(Color.red);
            exception.printStackTrace();
        }





    }



}
