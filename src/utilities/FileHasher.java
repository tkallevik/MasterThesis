package utilities;

import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Class used for hashing files.
 * 
 * Originally based on http://www.rgagnon.com/javadetails/java-0416.html
 *
 */
public class FileHasher {
	public static final String SHA256 = "SHA-256";
	public static final String MD5 = "MD5";
	
	private String hashAlgorithm;
	
	public FileHasher(String hashAlgorithm) {
		this.hashAlgorithm = hashAlgorithm;
	}
	
	/**
	 * Hash a file with the specified algorithm.
	 * 
	 * @param path which file to hash
	 * @param hashAlgorithm which algorithm to use
	 * @return a string representing the hash for the file
	 * @throws NoSuchAlgorithmException
	 * @throws IOException
	 */
	public String hashFile(String path) throws NoSuchAlgorithmException, IOException {
		InputStream inputStream = new FileInputStream(path);
		
		byte[] buffer = new byte[1024];

		MessageDigest messageDigest = MessageDigest.getInstance(hashAlgorithm);
		
		// Read file into message digest
		int numberOfBytesRead;
		do {
			numberOfBytesRead = inputStream.read(buffer);
			if (numberOfBytesRead > 0) {
				messageDigest.update(buffer, 0, numberOfBytesRead);
			}
		} while (numberOfBytesRead != -1);
		
		inputStream.close();
		
		// Complete message digest
		byte[] hash = messageDigest.digest();
		
		// Convert hash to string
		String hashString = "";
		
		for (int i=0; i < hash.length; i++) {
			hashString += Integer.toString((hash[i] & 0xff) + 0x100, 16).substring(1);
		}
		
		return hashString;
	}
}