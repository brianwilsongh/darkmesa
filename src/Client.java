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
			
			File path = new File("urls.txt");
			
			if (!path.exists()){
				System.out.println("ERROR FINDING THE FILE");
				return;
			}
			
			InputStreamReader fileIsr = new InputStreamReader(new FileInputStream(path));
			BufferedReader fileBr = new BufferedReader(fileIsr);
			
			boolean fileBrFinished = false;
			for (String line = fileBr.readLine(); line != null; line = fileBr.readLine()){
				ps.println(line);
			}
			ps.println("--endOfStream--");
			fileBr.close();
			
			InputStreamReader isr = new InputStreamReader(socket.getInputStream()); //get IS of socket, not file
			BufferedReader br = new BufferedReader(isr);
			for (String line = br.readLine(); line != null; line = br.readLine()){
				System.out.println(line);
			}
			ps.close();
			br.close();
		} catch (Exception e){
			e.printStackTrace();
		}

	}

}
