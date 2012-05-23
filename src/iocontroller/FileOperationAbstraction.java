package iocontroller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.security.NoSuchAlgorithmException;

import iocontroller.FileDeletionException;
import utilities.FileHasher;

/**
 * A collection of file operations. 
 *
 */
public class FileOperationAbstraction {
	private FileHasher fileHasher;
	
	/**
	 * Creates a new FileOperationAbstraction.
	 * 
	 * @param fileHasher the object to use for file hashing
	 */
	public FileOperationAbstraction(FileHasher fileHasher) {
		this.fileHasher = fileHasher;
	}
	
	/**
	 * Move a file.
	 * 
	 * @param sourcePath which file to move
	 * @param destinationPath where to move the file to
	 */
	public void move(String sourcePath, String destinationPath) {
		File sourceFile = new File(sourcePath);
		File destinationFile = new File(destinationPath);
		
		sourceFile.renameTo(destinationFile);
	}
	
	/**
	 * Delete a file.
	 * 
	 * @param path which file to delete
	 * @throws FileDeletionException if the underlying delete function returns false
	 */
	public void delete(String path) throws FileDeletionException {
		File file = new File(path);
		boolean deleteSuccessful = file.delete();
		
		if(!deleteSuccessful) {
			throw new FileDeletionException(path);
		}
	}
	
	/**
	 * Hash a file using a specified algorithm.
	 * 
	 * @param path which file to hash
	 * @param hashAlgorithm which algorithm to use
	 * @return a string representing the hash of the file
	 * @throws NoSuchAlgorithmException
	 * @throws IOException
	 */
	public String hash(String path) throws NoSuchAlgorithmException, IOException {
		return fileHasher.hashFile(path);
	}

	/**
	 * Copy a file.
	 * 
	 * @author http://stackoverflow.com/questions/106770/standard-concise-way-to-copy-a-file-in-java
	 * @param sourcePath which file to copy
	 * @param destinationPath where to copy to
	 * @throws IOException
	 */
	public void copy(String sourcePath, String destinationPath) throws IOException {
		File sourceFile = new File(sourcePath);
		File destinationFile = new File(destinationPath);
		
	    if(!destinationFile.exists()) {
	        destinationFile.createNewFile();
	    }

	    FileChannel source = null;
	    FileChannel destination = null;

	    try {
	        source = new FileInputStream(sourceFile).getChannel();
	        destination = new FileOutputStream(destinationFile).getChannel();
	        destination.transferFrom(source, 0, source.size());
	    }
	    finally {
	        if(source != null) {
	            source.close();
	        }
	        if(destination != null) {
	            destination.close();
	        }
	    }
	}
	
	public void touch(String path) throws IOException {
		File file = new File(path);
		file.createNewFile();
	}
}
