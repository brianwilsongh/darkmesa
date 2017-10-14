import java.io.*;
import java.net.*;


public class Server {

	public static void main(String[] args) {
		Server s = new Server();
		try {
			s.run();
		} catch (Exception e){
			e.printStackTrace();
		}

	}
	
	public void run() {
		try {
			ServerSocket serverSocket = new ServerSocket(1100);
			Socket socket = serverSocket.accept();
			InputStreamReader isr = new InputStreamReader(socket.getInputStream());
			BufferedReader br = new BufferedReader(isr);
			
			String message = br.readLine();
			System.out.println("message: " + message);
			System.out.println("from: " + socket.getInetAddress());
			System.out.println("to: " + socket.getLocalSocketAddress());
			
			if (message != null){
				PrintStream ps = new PrintStream(socket.getOutputStream());
				ps.println("Message was received!");
			}
		} catch (Exception e){
			e.printStackTrace();
		}

	}

}
