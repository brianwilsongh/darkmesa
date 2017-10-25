import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class Client {
	
	OutputStream socketOutputStream;
	InputStream socketInputStream;
	
	private PublicKey mServerPublicKey;
	
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
			socketOutputStream = socket.getOutputStream();
			socketInputStream = socket.getInputStream();
			
			ObjectInputStream objectIn = new ObjectInputStream(socketInputStream);
			mServerPublicKey = (PublicKey) objectIn.readObject();
			ObjectOutputStream objectOut = new ObjectOutputStream(socketOutputStream);
			objectOut.writeObject(encryptWithServerPublic("Secret message from client"));
			
			
			PrintStream ps = new PrintStream(socket.getOutputStream());
			ps.println("aegon");
			ps.println("Hello to server");
			
			File path = new File("urls.txt");
			
			if (!path.exists()){
				System.out.println("ERROR FINDING THE FILE");
				return;
			}
			
			InputStreamReader fileIsr = new InputStreamReader(new FileInputStream(path));
			BufferedReader fileBr = new BufferedReader(fileIsr);
			
			for (String line = fileBr.readLine(); line != null; line = fileBr.readLine()){
				ps.println(line);
			}
			ps.println("--endOfStream--"); //special line to inform server to start processing
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
	
	private byte[] encryptWithServerPublic(String data) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException{
		Cipher cipher = Cipher.getInstance("RSA");
		cipher.init(Cipher.ENCRYPT_MODE, mServerPublicKey);
		byte[] encryptedData = cipher.doFinal(data.getBytes());
		return encryptedData;
	}
	
	private String hash(String string) throws NoSuchAlgorithmException{
		return Base64.getEncoder().encodeToString(MessageDigest.getInstance("SHA-256").digest(string.getBytes(StandardCharsets.UTF_8)));
	}

}
