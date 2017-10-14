import java.io.*;
import java.net.*;

public class Client {
	
	public static void main(String[] args) {
		Client client = new Client();
		try {
			client.run();
		} catch (Exception e){
			e.printStackTrace();
		}
	}
	
	public void run() {
		try {
			Socket socket = new Socket("localhost", 1100);
			PrintStream ps = new PrintStream(socket.getOutputStream());
			ps.println("Hello to server");
			InputStreamReader isr = new InputStreamReader(socket.getInputStream());
			BufferedReader br = new BufferedReader(isr);
			String message = br.readLine();
			System.out.println(message);
		} catch (Exception e){
			e.printStackTrace();
		}

	}

}
