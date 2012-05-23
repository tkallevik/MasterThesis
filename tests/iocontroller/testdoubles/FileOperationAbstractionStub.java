package iocontroller.testdoubles;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import iocontroller.FileOperationAbstraction;


import testutilities.InvocationLog;
import utilities.FileHasher;

/**
 * The responsibility of this class is to trap method invocations, 
 * serve bogus return data and store invocation arguments for the FileOperationAbstraction.
 *
 */
public class FileOperationAbstractionStub extends FileOperationAbstraction {
	public InvocationLog invocationLog;
	public static final String MOCK_HASH = "this is a mock hash returned by the FileOperationAbstractionStub";
	public String receivedDestinationPath;
	
	public FileOperationAbstractionStub(FileHasher fileHasher) {
		super(fileHasher);
		invocationLog = new InvocationLog();
	}
	
	public void move(String sourcePath, String destinationPath) {
		invocationLog.addInvocation("move");
		receivedDestinationPath = destinationPath;
	}

	public void delete(String path) {
		invocationLog.addInvocation("delete");
	}

	public String hash(String path, String hashAlgorithm) throws NoSuchAlgorithmException, IOException {
		invocationLog.addInvocation("hash");
		
		return MOCK_HASH;
	}
}
