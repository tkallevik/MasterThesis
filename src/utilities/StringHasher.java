package utilities;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class StringHasher {
	public static String md5Sum(String s) throws Exception {
		byte[] bytesOfMessage = s.getBytes();

		MessageDigest md;
		try {
			md = MessageDigest.getInstance("MD5");
			byte[] hash = md.digest(bytesOfMessage);
			
			// Convert hash to string
			String hashString = "";
			
			for (int i=0; i < hash.length; i++) {
				hashString += Integer.toString((hash[i] & 0xff) + 0x100, 16).substring(1);
			}
			
			return hashString;
		} catch (NoSuchAlgorithmException e) {
			Exception exception = new Exception("Unable to hash string.");
			exception.initCause(e);
			throw exception;
		}
	}
}
