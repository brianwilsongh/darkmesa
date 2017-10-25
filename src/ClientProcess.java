import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class ClientProcess implements Runnable {
	private Socket socket;

	private PublicKey serverPublicKey;
	private PrivateKey serverPrivateKey;

	public ClientProcess(Socket assignedSocket, PublicKey puKey, PrivateKey prKey) {
		socket = assignedSocket;
		serverPublicKey = puKey;
		serverPrivateKey = prKey;
	}

	@Override
	public void run() {
		try {
			InputStreamReader isr = new InputStreamReader(socket.getInputStream());
			BufferedReader br = new BufferedReader(isr);

			ObjectOutputStream objectOut = new ObjectOutputStream(socket.getOutputStream());
			objectOut.writeObject(serverPublicKey);
			ObjectInputStream objectIn = new ObjectInputStream(socket.getInputStream());
			String decryptedPassword = decryptWithServerPrivate((byte[]) objectIn.readObject());
			System.out.println("receive password: " + decryptedPassword);

			if (!Server.hashedPasswords.contains(decryptedPassword)){
				System.out.println("BAD GUY with pw: " + decryptedPassword);
				br.close();
				socket.close();
				return;
			}


			String message = br.readLine();
			System.out.println("simple message: " + message);
			System.out.println("from: " + socket.getInetAddress());
			System.out.println("to: " + socket.getLocalSocketAddress());
			System.out.println("hostname: " + socket.getLocalAddress().getHostName());
			System.out.println("canonical host: " + socket.getLocalAddress().getCanonicalHostName());

			PrintStream ps = new PrintStream(socket.getOutputStream());
			ps.println("Server is Responding...");
			for (String line = br.readLine(); line != null; line = br.readLine()) {
				try {
					Thread.sleep(200);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				if (line.equals("--endOfStream--") || ps.checkError()) {
					ps.close();
					break;
				}
				ps.println("Server Output:" + line);
			}
			System.out.println("End of stream");
			socket.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private String decryptWithServerPrivate(byte[] data) throws InvalidKeyException, NoSuchAlgorithmException,
			NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
		String decrypted = "";
		Cipher decrypt = Cipher.getInstance("RSA");
		decrypt.init(Cipher.DECRYPT_MODE, serverPrivateKey);
		byte[] decryptedData = decrypt.doFinal(data);
		decrypted = new String(decryptedData);
		return decrypted;
	}

}
