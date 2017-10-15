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
			while (true){
				Socket socket = serverSocket.accept();
				handleConnection(socket);
				System.out.println("Handled a connection");
			}
			
		} catch (Exception e){
			e.printStackTrace();
		}

	}
	
	public static void handleConnection(Socket socket) throws IOException{
		InputStreamReader isr = new InputStreamReader(socket.getInputStream());
		BufferedReader br = new BufferedReader(isr);
		
		String message = br.readLine();
		System.out.println("message: " + message);
		System.out.println("from: " + socket.getInetAddress());
		System.out.println("to: " + socket.getLocalSocketAddress());
		System.out.println("hostname: " + socket.getLocalAddress().getHostName());
		System.out.println("canonical host: " + socket.getLocalAddress().getCanonicalHostName());
		
		PrintStream ps = new PrintStream(socket.getOutputStream());
		ps.println(" Responding...");
		for (String line = br.readLine(); line != null; line = br.readLine()){
			if (line.equals("--endOfStream--")){
				ps.close();
				break;
			}
			ps.println("SERVER:" + line);
		}
		System.out.println("End of stream");
		socket.close();
	}

}
