package integration;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import org.junit.After;
import org.junit.Test;

import archivecontroller.QueryException;
import testutilities.FileOperations;
import transfercontroller.handshake.HandshakeException;
import utilities.ServiceUnavailableException;

public class Demo {
	private String ioControllerRepositoryPathA = "tests/dummy/ioControllerRepository/A/";
	private String ioControllerRepositoryPathB = "tests/dummy/ioControllerRepository/B/";
	
	/**
	 * Demo Use Case
	 */
	@Test
	public void test() throws IOException, ServiceUnavailableException, HandshakeException, QueryException, InterruptedException {
		// Set up fixture
		DemoHost A = new DemoHost(ioControllerRepositoryPathA);
		DemoHost B = new DemoHost(ioControllerRepositoryPathB);
		
		// Create new files
		String branchX = A.posix.create();
		String dataX = "fooXbar";
		String originalDataX = dataX;
		writeStringToBranch(A, branchX, dataX);
		
		String branchY = A.posix.create();
		String dataY = "fooYbar";
		writeStringToBranch(A, branchY, dataY);
		
		String branchZ = B.posix.create();
		String dataZ = "fooZbar";
		writeStringToBranch(B, branchZ, dataZ);
		
		// Verify content of new files
		assertEquals(dataX, readStringFromBranch(A, branchX));
		assertEquals(dataY, readStringFromBranch(A, branchY));
		assertEquals(dataZ, readStringFromBranch(B, branchZ));
		
		// Sync files
		A.sync.sync("localhost", B.syncControllerListenPort);
		
		Thread.sleep(2000);
		
		// Verify that files have been synced
		assertEquals(dataX, readStringFromBranch(B, branchX));
		assertEquals(dataY, readStringFromBranch(B, branchY));
		assertEquals(dataZ, readStringFromBranch(A, branchZ));
		
		// Edit a file
		dataX = "newFooXBar";
		writeStringToBranch(A, branchX, dataX);
		
		// Sync the edited file
		A.sync.sync("localhost", B.syncControllerListenPort);
		
		// Verify that the changes have been synced
		assertEquals(dataX, readStringFromBranch(B, branchX));
		
		// Add some metadata to a file
		String metadataKeyX = "someKey";
		String metadataValueX = "someKey";
		A.archive.addMetaDataValue(branchX, metadataKeyX, metadataValueX);
		
		// Verify that the metadata was added
		String[] metaDataValues = A.archive.getMetaDataValue(branchX, metadataKeyX);
		assertEquals(metadataValueX, metaDataValues[0]);
		
		// Sync the metadata
		A.sync.sync("localhost", B.syncControllerListenPort);
		
		// Verify that the metadata was synced
		metaDataValues = B.archive.getMetaDataValue(branchX, metadataKeyX);
		assertEquals(metadataValueX, metaDataValues[0]);
		
		// Edit different files on both hosts
		dataY = "newFooYBar";
		writeStringToBranch(A, branchY, dataY);
		dataZ = "newFooZBar";
		writeStringToBranch(B, branchZ, dataZ);
		
		// Sync the changes
		A.sync.sync("localhost", B.syncControllerListenPort);
		
		Thread.sleep(2000);
		
		// Verify that the changes has been synced
		assertEquals(dataY, readStringFromBranch(B, branchY));
		assertEquals(dataZ, readStringFromBranch(A, branchZ));
		
		// Edit the same file on both hosts
		dataZ = "conflictingStringFromA";
		writeStringToBranch(A, branchZ, dataZ);
		dataZ = "conflictingStringFromB";
		writeStringToBranch(B, branchZ, dataZ);
		
		// Sync the changes
		A.sync.sync("localhost", B.syncControllerListenPort);
		
		Thread.sleep(2000);
		
		// Verify that a merge conflict was raised on both hosts
		assertTrue(A.conflicts.hasConflict(branchZ));
		assertTrue(B.conflicts.hasConflict(branchZ));
		
		// Verify that old revisions can be read
		String unitId = A.archive.getUnit(branchX, 0);
		String branchId = A.archive.createBranch(unitId);
		assertEquals(originalDataX, readStringFromBranch(A, branchId));
	}
	
	private void writeStringToBranch(DemoHost host, String branchId, String stringToWrite) throws IOException, QueryException {
		int streamId = host.posix.openOutputStream(branchId);
		for (char c : stringToWrite.toCharArray()) {
			host.posix.write(streamId, c);
		}
		host.posix.closeOutputStream(streamId);
		host.posix.commit(branchId);
	}
	
	private String readStringFromBranch(DemoHost host, String branchId) throws IOException, QueryException {
		int streamId = host.posix.openInputStream(branchId);
		
		StringBuilder result = new StringBuilder();
		int b;
		do {
			b = host.posix.read(streamId);
			
			if (b != -1) {
				result.append((char) b);
			}
		} while (b != -1);
		
		host.posix.closeInputStream(streamId);
		host.posix.commitClean(branchId);
		
		return result.toString();
	}
	
	@After
	public void tearDownHostA() {
		FileOperations.emptyFolder(new File(ioControllerRepositoryPathA));
	}
	
	@After
	public void tearDownHostB() {
		FileOperations.emptyFolder(new File(ioControllerRepositoryPathB));
	}
}
