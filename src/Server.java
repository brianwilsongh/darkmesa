import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;


public class Server {
	private static String[] passwords = new String[]{"hunter12", "javarules"};
	private static Set<String> hashedPasswords = new HashSet<>();
	
	private static PrivateKey serverPrivateKey;
	private static PublicKey serverPublicKey;

	public static void main(String[] args) throws NoSuchAlgorithmException {
		
		for (int idx = 0; idx < passwords.length; idx++){
			hashedPasswords.add(hash(passwords[idx]));
		}
		
		try {
			//create key pair for the server
			KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
			SecureRandom random = SecureRandom.getInstance("SHA1PRNG", "SUN");
			kpg.initialize(1024, random); //initiate 
			KeyPair keyPair = kpg.generateKeyPair();
			serverPrivateKey = keyPair.getPrivate();
			serverPublicKey = keyPair.getPublic();
			
			System.out.println("secret" + Arrays.toString(serverPrivateKey.getEncoded()));
			System.out.println("pkey" + Arrays.toString(serverPublicKey.getEncoded()));
			
			//encrypt sample data
			String data = "data to be encrypted";
			Cipher cipher = Cipher.getInstance("RSA");
			cipher.init(Cipher.ENCRYPT_MODE, serverPublicKey);
			byte[] encryptedData = cipher.doFinal(data.getBytes());
			System.out.println("Encrypted data is : " + Arrays.toString(encryptedData));
			
			
			Cipher decrypt=Cipher.getInstance("RSA");
			decrypt.init(Cipher.DECRYPT_MODE, serverPrivateKey);
			byte[] decryptedData = decrypt.doFinal(encryptedData);
			String decryptedString = new String(decryptedData);
			System.out.println("Decrypted data is : " + decryptedString);
			
		} catch (Exception e){
			e.printStackTrace();
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
	
	public void handleConnection(Socket socket) throws IOException, ClassNotFoundException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException{
		InputStreamReader isr = new InputStreamReader(socket.getInputStream());
		BufferedReader br = new BufferedReader(isr);
		
		ObjectOutputStream objectOut = new ObjectOutputStream(socket.getOutputStream()); //make stream to write public key to client
		objectOut.writeObject(serverPublicKey);
		ObjectInputStream objectIn = new ObjectInputStream(socket.getInputStream());
		String secret = decryptWithServerPrivate((byte[]) objectIn.readObject());
		System.out.println("Server says secret is: " + secret);
		
		String sentPassword = br.readLine();
		String encoded;
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			byte[] hash = digest.digest(sentPassword.getBytes(StandardCharsets.UTF_8));
			encoded = Base64.getEncoder().encodeToString(hash);
			if (!hashedPasswords.contains(encoded)){
				System.out.print("BAD GUY!");
				socket.close();
				return;
			}
			
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		

		
		String message = br.readLine();
		System.out.println("simple message: " + message);
		System.out.println("from: " + socket.getInetAddress());
		System.out.println("to: " + socket.getLocalSocketAddress());
		System.out.println("hostname: " + socket.getLocalAddress().getHostName());
		System.out.println("canonical host: " + socket.getLocalAddress().getCanonicalHostName());
		
		PrintStream ps = new PrintStream(socket.getOutputStream());
		ps.println("Server is Responding...");
		for (String line = br.readLine(); line != null; line = br.readLine()){
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if (line.equals("--endOfStream--") || ps.checkError()){
				ps.close();
				break;
			}
			ps.println("Server Output:" + line);
		}
		System.out.println("End of stream");
		socket.close();
	}
	
	private String decryptWithServerPrivate(byte[] data) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException{
		String decrypted = "";
		Cipher decrypt=Cipher.getInstance("RSA");
		decrypt.init(Cipher.DECRYPT_MODE, serverPrivateKey);
		byte[] decryptedData = decrypt.doFinal(data);
		decrypted = new String(decryptedData);
		return decrypted;
	}
	
	private static String hash(String string) throws NoSuchAlgorithmException{
		return Base64.getEncoder().encodeToString(MessageDigest.getInstance("SHA-256").digest(string.getBytes(StandardCharsets.UTF_8)));
	}

}
