import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;


public class Server {
	private static String[] passwords = new String[]{"aegon", "tirion"};
	private static Set<String> shaCodes = new HashSet<>();

	public static void main(String[] args) throws NoSuchAlgorithmException {
		
		MessageDigest digest = MessageDigest.getInstance("SHA-256");
		for (int idx = 0; idx < passwords.length; idx++){
			String password = passwords[idx];
			byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
			String encoded = Base64.getEncoder().encodeToString(hash);
			System.out.println("Encoded: " + encoded);
			shaCodes.add(encoded);
		}
		
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
			serverSocket.setSoTimeout(300000);
			while (true){
				
				Socket socket = serverSocket.accept();
				try {
					handleConnection(socket);
					System.out.println("Handled a connection");
				} catch (SocketTimeoutException ste){
					System.out.println("Socket timed out!");
					socket.close();
				}
			}
			
		} catch (Exception e){
			e.printStackTrace();
		}

	}
	
	public static void handleConnection(Socket socket) throws IOException{
		InputStreamReader isr = new InputStreamReader(socket.getInputStream());
		BufferedReader br = new BufferedReader(isr);
		
		
		String sentPassword = br.readLine();
		String encoded;
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			byte[] hash = digest.digest(sentPassword.getBytes(StandardCharsets.UTF_8));
			encoded = Base64.getEncoder().encodeToString(hash);
			if (!shaCodes.contains(encoded)){
				System.out.print("BAD GUY!");
				socket.close();
				return;
			}
			
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		

		
		String message = br.readLine();
		System.out.println("message: " + message);
		System.out.println("from: " + socket.getInetAddress());
		System.out.println("to: " + socket.getLocalSocketAddress());
		System.out.println("hostname: " + socket.getLocalAddress().getHostName());
		System.out.println("canonical host: " + socket.getLocalAddress().getCanonicalHostName());
		
		PrintStream ps = new PrintStream(socket.getOutputStream());
		ps.println(" Responding...");
		for (String line = br.readLine(); line != null; line = br.readLine()){
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if (line.equals("--endOfStream--") || ps.checkError()){
				ps.close();
				break;
			}
			ps.println("SERVER:" + line);
		}
		System.out.println("End of stream");
		socket.close();
	}

}
