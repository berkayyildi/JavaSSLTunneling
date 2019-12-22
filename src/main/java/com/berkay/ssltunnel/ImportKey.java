package com.berkay.ssltunnel;

import java.io.*;
import java.security.*;
import java.security.spec.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.util.Collection;

public class ImportKey  {

    String keystorename;

    private static InputStream fullStream ( String fname ) throws IOException {
        FileInputStream fis = new FileInputStream(fname);
        DataInputStream dis = new DataInputStream(fis);
        byte[] bytes = new byte[dis.available()];
        dis.readFully(bytes);
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        return bais;
    }


    public ImportKey(String keystorename) {

        this.keystorename = keystorename;

        ProcessBuilder processBuilder = new ProcessBuilder();
        // -- Linux --
        // Run a shell command
        processBuilder.command("bash", "-c", "openssl genrsa -out key.pem 2048 && openssl req -new -x509 -key key.pem -out cert.pem -days 365 -subj \"/C=TR/ST=Kayisdagi/L=Atasehir/O=Yeditepe University/OU=Computer Science/CN=ssl.berkayyildiz.com\" && openssl pkcs8 -topk8 -nocrypt -in key.pem -inform PEM -out key.der -outform DER && openssl x509 -in cert.pem -inform PEM -out cert.der -outform DER");
        //processBuilder.command("path/to/hello.sh");
        // -- Windows --
        // Run a command
        //processBuilder.command("cmd.exe", "/c", "dir C:\\Users\\mkyong");


        try {
            Process process = processBuilder.start();
            StringBuilder output = new StringBuilder();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line + "\n");
            }

            int exitVal = process.waitFor();
            if (exitVal == 0) {
                System.out.println("PEM GEN Success!");
            } else {
                //abnormal...
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        // change this if you want another password by default
        String keypass = "importkey";

        // change this if you want another alias by default
        String defaultalias = "importkey";

        // change this if you want another keystorefile by default
        //String keystorename = System.getProperty("keystore");

        if (keystorename == null)
            keystorename = "keystore.ImportKey"; // especially this ;-)


        // parsing command line input
        String keyfile = "key.der";
        String certfile = "cert.der";

        try {
            // initializing and clearing keystore
            KeyStore ks = KeyStore.getInstance("JKS", "SUN");
            ks.load( null , keypass.toCharArray());
            System.out.println("Using keystore-file : "+keystorename);
            ks.store(new FileOutputStream ( keystorename  ),
                    keypass.toCharArray());
            ks.load(new FileInputStream ( keystorename ),
                    keypass.toCharArray());

            // loading Key
            InputStream fl = fullStream (keyfile);
            byte[] key = new byte[fl.available()];
            KeyFactory kf = KeyFactory.getInstance("RSA");
            fl.read ( key, 0, fl.available() );
            fl.close();
            PKCS8EncodedKeySpec keysp = new PKCS8EncodedKeySpec ( key );
            PrivateKey ff = kf.generatePrivate (keysp);

            // loading CertificateChain
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            InputStream certstream = fullStream (certfile);

            Collection c = cf.generateCertificates(certstream) ;
            Certificate[] certs = new Certificate[c.toArray().length];

            if (c.size() == 1) {
                certstream = fullStream (certfile);
                System.out.println("One certificate, no chain.");
                Certificate cert = cf.generateCertificate(certstream) ;
                certs[0] = cert;
            } else {
                System.out.println("Certificate chain length: "+c.size());
                certs = (Certificate[])c.toArray();
            }

            // storing keystore
            ks.setKeyEntry(defaultalias, ff,
                    keypass.toCharArray(),
                    certs );
            System.out.println ("Key and certificate stored.");
            System.out.println ("Alias:"+defaultalias+"  Password:"+keypass);
            ks.store(new FileOutputStream ( keystorename ),
                    keypass.toCharArray());
        } catch (Exception ex) {
            ex.printStackTrace();
        }




        ProcessBuilder processBuilder2 = new ProcessBuilder();
        // -- Linux --
        // Run a shell command
        processBuilder2.command("bash", "-c", "rm cert.der key.pem key.der cert.pem");
        //processBuilder.command("path/to/hello.sh");
        // -- Windows --
        // Run a command
        //processBuilder.command("cmd.exe", "/c", "dir C:\\Users\\mkyong");


        try {
            Process process = processBuilder2.start();
            StringBuilder output = new StringBuilder();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line + "\n");
            }

            int exitVal = process.waitFor();
            if (exitVal == 0) {
                System.out.println("PEM GEN Success!");
            } else {
                //abnormal...
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }



}// KeyStore
