package com.berkay.ssltunnel;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.ini4j.Ini;
import org.ini4j.Profile;


public class SecureTunnel {

	public static void main(String[] args) throws Exception {


		File f = new File("config.ini");
		if(!f.exists()){

			new FileOutputStream("config.ini", false).close();	//Create empty file

			String newService = "test-settings";
			File file = new File("config.ini");
			Ini ini = new Ini(file);
			ini.put(newService, "server", "yes");
			ini.put(newService, "ListenPort", "1234");
			ini.put(newService, "DestinationIP", "10.10.10.1");
			ini.put(newService, "DestinationPort", "443");
			ini.put(newService, "Proto", "TCP");
			ini.put(newService, "Key", "keystore.ImportKey");

			ini.store();
			
		}else{
			System.out.println("File already exists");
		}



		File f2 = new File("keystore.ImportKey");
		if(!f2.exists()){

			new ImportKey("keystore.ImportKey");

		}else{
			System.out.println("Cert already exists");
		}





		File file = new File("config.ini");
		Ini ini = new Ini(file);
		for (String sectionName: ini.keySet()) {
			Profile.Section section = ini.get(sectionName);
			System.out.println( sectionName + " Settings:" + ini.get(sectionName));

			int ListenPort = Integer.parseInt(section.get("ListenPort"));
			String DestinationIP = section.get("DestinationIP");
			int DestinationPort = Integer.parseInt(section.get("DestinationPort"));
			String Key = section.get("Key");

			if(section.get("server").equals("yes")){	//SERVERSA
				if(section.get("Proto").equals("UDP")) {    //UDP ISE
					new ServerUDP(DestinationPort, ListenPort, DestinationIP, Key).start();    //Server 4444 ten dinlesin (443,4445,IP,KEY)
				}else{
					new Server(DestinationPort, ListenPort, DestinationIP, Key).start();    //Server 4444 ten dinlesin (443,4445,IP,KEY)
				}
			}else{	//CLIENTSA
				if(section.get("Proto").equals("UDP")) {    //UDP ISE
					new ClientUDP(DestinationPort, ListenPort, DestinationIP, Key).start();//Client 4445 ten dinlesin (443,4445,IP,KEY)
				}else{
					new Client(DestinationPort, ListenPort, DestinationIP, Key).start();//Client 4445 ten dinlesin (443,4445,IP,KEY)
				}
			}

		}


		//System.exit(0);


		System.out.println("Started");

	}
}

