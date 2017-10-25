import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class Utils {

	protected static String hash(String string) throws NoSuchAlgorithmException {
		return Base64.getEncoder()
				.encodeToString(MessageDigest.getInstance("SHA-256").digest(string.getBytes(StandardCharsets.UTF_8)));
	}
}
