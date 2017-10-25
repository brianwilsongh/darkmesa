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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class Server {
	private static String[] passwords = new String[] { "hunter12", "javarules" };
	public static Set<String> hashedPasswords = new HashSet<>();

	private PrivateKey serverPrivateKey;
	private PublicKey serverPublicKey;
	
	private ServerSocket serverSocket; //this server socket

	public static void main(String[] args) throws NoSuchAlgorithmException, NoSuchProviderException {

		// try {
		// //sample for encrypt/decrypt
		// String data = "data to be encrypted";
		// Cipher cipher = Cipher.getInstance("RSA");
		// cipher.init(Cipher.ENCRYPT_MODE, serverPublicKey);
		// byte[] encryptedData = cipher.doFinal(data.getBytes());
		// System.out.println("Encrypted data is : " +
		// Arrays.toString(encryptedData));
		//
		// Cipher decrypt=Cipher.getInstance("RSA");
		// decrypt.init(Cipher.DECRYPT_MODE, serverPrivateKey);
		// byte[] decryptedData = decrypt.doFinal(encryptedData);
		// String decryptedString = new String(decryptedData);
		// System.out.println("Decrypted data is : " + decryptedString);
		// } catch (Exception e){
		// e.printStackTrace();
		// }

		try {
			new Server().run(4242);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public Server() throws NoSuchAlgorithmException, NoSuchProviderException {
		buildKeys(); // private and public
		for (int idx = 0; idx < passwords.length; idx++) {
			hashedPasswords.add(Utils.hash(passwords[idx])); // encrypt pws into set
		}
	}

	public void run(int port) {
		final ExecutorService processPool = Executors.newCachedThreadPool();
		try {
			serverSocket = new ServerSocket(port);
			serverSocket.setSoTimeout(300000);
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		Runnable serverTask = new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				while (true) {
					Socket thisSocket;
					try {
						thisSocket = serverSocket.accept();
						processPool.submit(new ClientProcess(thisSocket, serverPublicKey, serverPrivateKey));
						System.out.println("Submitted a connection to process pool");
					} catch (Exception e) {
						System.out.println("Socket timed out!");
					}
				}
			}
		};
		Thread listeningThread = new Thread(serverTask);
		listeningThread.start();

	}

	private void buildKeys() throws NoSuchAlgorithmException, NoSuchProviderException {
		// create key pair for the server
		KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
		SecureRandom random = SecureRandom.getInstance("SHA1PRNG", "SUN");
		kpg.initialize(1024, random); // initiate
		KeyPair keyPair = kpg.generateKeyPair();
		serverPrivateKey = keyPair.getPrivate();
		serverPublicKey = keyPair.getPublic();
		System.out.println("secret" + Arrays.toString(serverPrivateKey.getEncoded()));
		System.out.println("pkey" + Arrays.toString(serverPublicKey.getEncoded()));
	}

}
