package ssltunnel;

import java.io.File;
import java.io.IOException;

import org.ini4j.Ini;
import org.ini4j.Profile;


public class SecureTunnel {

	public static void main(String[] args) throws IOException {

		File file = new File("config.ini");
		Ini ini = new Ini(file);
		for (String sectionName: ini.keySet()) {
			Profile.Section section = ini.get(sectionName);
			System.out.println( sectionName + " Settings:" + ini.get(sectionName));
			for (String optionKey: section.keySet()) {	//HER BIRI ICIN

				int ListenPort = Integer.parseInt(section.get("ListenPort"));
				String DestinationIP = section.get("DestinationIP");
				int DestinationPort = Integer.parseInt(section.get("DestinationPort"));
				String Key = section.get("Key");

				if (optionKey.equals("client")){
					if(section.get(optionKey).equals("yes")){	//SERVERSA
						new SSLServer(DestinationPort, ListenPort, DestinationIP, Key).start();	//Server 4444 ten dinlesin
					}else{	//CLIENTSA
						new SSLSocketClient(DestinationPort, ListenPort, DestinationIP, Key).start();//Client 4445 ten dinlesin
					}


				}

			}
		}


		//System.exit(0);


		System.out.println("Started");

	}
}

