import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ServerHelper extends Thread {

		private Socket s;

		public ServerHelper(Socket incoming) {
			this.s = incoming;
		}

		public void run() {
			try {
				

				BufferedReader bReader = new BufferedReader(new InputStreamReader(s.getInputStream()));
				DataOutputStream bWriter = new DataOutputStream(s.getOutputStream());
				

				String fileNameToRead = bReader.readLine();
				
				byte[] fileContent = null;
				
				 try {
					 fileContent = Files.readAllBytes(Paths.get(fileNameToRead));
			        } catch (Exception Hata) {
			            System.out.println("An error occurred:" + Hata.getMessage());
			            s.close();
			            return;	//File save etmeden cik
			    		//ss.close();
			        }

				 
				bWriter.writeInt(fileContent.length);
				bWriter.write(fileContent);	//byte yapildi
				bWriter.flush();
				
				
				s.close();
				//ss.close();
				
				
			} catch (Exception exception) {
				exception.printStackTrace();
			}
		}
	}