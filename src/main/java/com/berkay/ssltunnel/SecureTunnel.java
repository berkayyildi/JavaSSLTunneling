package com.berkay.ssltunnel;

import java.io.File;
import java.io.IOException;

import org.ini4j.Ini;
import org.ini4j.Profile;


public class SecureTunnel {

	public static void main(String[] args) throws Exception {

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

