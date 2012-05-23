package iocontroller.tests.unit;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;


import org.junit.Test;

import iocontroller.FileDeletionException;
import iocontroller.FileOperationAbstraction;

import utilities.FileHasher;


public class FileOperationAbstractionTest {

	/**
	 * Create an empty file, move it using FileOperationFacade, and verify its new path.
	 * 
	 * @throws IOException
	 */
	@Test
	public void moveTest() throws IOException {
		// Set up fixture
		String sourcePath = "tests/dummy/foobar.moveTest.source";
		String destinationPath = "tests/dummy/foobar.moveTest.destination";
		File sourceFile = new File(sourcePath);
		sourceFile.createNewFile();
		
		FileOperationAbstraction fileOperationAbstraction = new FileOperationAbstraction(null);
		
		// Exercise SUT
		fileOperationAbstraction.move(sourcePath, destinationPath);
		
		// Verify outcome
		File destinationFile = new File(destinationPath);
		assertTrue(destinationFile.exists());
		
		// Tear down
		destinationFile.delete();
	}
	
	/**
	 * Create an empty file, delete it using FileOperationFacade and verify its non-existence.
	 * 
	 * @throws IOException
	 * @throws FileDeletionException 
	 */
	@Test
	public void deleteTest() throws IOException, FileDeletionException {
		// Set up fixture
		String path = "tests/dummy/foobar.deleteTest";
		File file = new File(path);
		file.createNewFile();
		
		FileOperationAbstraction fileOperationAbstraction = new FileOperationAbstraction(null);
		
		// Exercise SUT
		fileOperationAbstraction.delete(path);
		
		// Verify outcome
		assertFalse(file.exists());
	}
	
	/**
	 * Tell the SUT to MD5 hash a file that we already know the hash of, and compare it to the result.
	 * 
	 * @throws IOException
	 * @throws NoSuchAlgorithmException 
	 */
	@Test
	public void hashTest() throws IOException, NoSuchAlgorithmException {
		// Set up fixture
		String originalHash = "fa67fd969978bb5a8ce66e80c5496815";
		String path = "tests/dummy/foobar";
		FileHasher fileHasher = new FileHasher(FileHasher.MD5);
		FileOperationAbstraction fileOperationAbstraction = new FileOperationAbstraction(fileHasher);
		
		// Exercise SUT
		String resultingHash = fileOperationAbstraction.hash(path);
		
		// Verify outcome
		assertEquals(originalHash, resultingHash);
	}
}
