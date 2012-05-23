package utilities.tests;

import static org.junit.Assert.*;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import org.junit.Test;

import utilities.FileHasher;

public class FileHasherTest {

	/**
	 * Have the FileHasher hash a file, and verify that it agrees with the known md5sum of the file.
	 * 
	 * @throws NoSuchAlgorithmException
	 * @throws IOException
	 */
	@Test
	public void hashFileTest() throws NoSuchAlgorithmException, IOException {
		// Set up fixture
		String filePath = "tests/dummy/foobar.utilities.fileHasherTest";
		String md5sum = "e93869202d819dfb9b6198c24c9aa601";
		FileHasher fileHasher = new FileHasher(FileHasher.MD5);
		
		// Exercise SUT
		String result = fileHasher.hashFile(filePath);
		
		// Verify outcome
		assertEquals(md5sum, result);
	}

}
