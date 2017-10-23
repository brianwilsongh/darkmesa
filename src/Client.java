import java.io.*;
import java.net.*;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.util.Arrays;

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
			
			KeyPairGenerator kpg = KeyPairGenerator.getInstance("DSA", "SUN");
			SecureRandom random = SecureRandom.getInstance("SHA1PRNG", "SUN");
			kpg.initialize(1024, random);
			KeyPair pair = kpg.generateKeyPair();
			PrivateKey privateKey = pair.getPrivate();
			PublicKey publicKey = pair.getPublic();
			System.out.println("secret" + Arrays.toString(privateKey.getEncoded()));
			System.out.println("pkey" + Arrays.toString(publicKey.getEncoded()));
			
			Socket socket = new Socket("localhost", 1100);
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
