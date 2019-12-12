
public class SecureTunnel {
	
	public static void main(String[] args) {
	
		new SSLServer(444, 4444).start();	//Server 4444 ten dinlesin
		new SSLSocketClient(444, 4445).start();//Client 4445 ten dinlesin
		
		new SSLServer(344, 3444).start();	//Server 3444 ten dinlesin
		new SSLSocketClient(344, 3445).start();//Client 3445 ten dinlesin
		
		System.out.println("SA");

	}
}
