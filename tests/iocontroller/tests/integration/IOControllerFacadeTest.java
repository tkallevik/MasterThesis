package iocontroller.tests.integration;

import static org.junit.Assert.*;
import iocontroller.FileDeletionException;
import iocontroller.IIOControllerFacade;
import iocontroller.IOControllerFacadeFactory;

import java.io.IOException;

import org.junit.Test;

public class IOControllerFacadeTest {
	
	@Test
	public void readWriteTest() throws IOException, FileDeletionException {
		// Set up fixture
		String repositoryPath = "tests/dummy/ioControllerRepository/A/";		
		IOControllerFacadeFactory ioControllerFacadeFactory = new IOControllerFacadeFactory();
		IIOControllerFacade ioControllerFacade = ioControllerFacadeFactory.createFacade(repositoryPath);
		
		// Exercise checkOut()
		String checkoutId = ioControllerFacade.checkOut();
		
		// Exercise openOutputStream which verifies checkOut() and set up for write()
		int outputStreamId = ioControllerFacade.openOutputStream(checkoutId);
		
		// Exercise write() which verifies openOutputStream()
		int data = 23;
		ioControllerFacade.write(outputStreamId, data);
		
		// Exercise openInputStream() which set up for read()
		int inputStreamId = ioControllerFacade.openInputStream(checkoutId);
		
		// Exercise read() which verifies openInputStream()
		int result = ioControllerFacade.read(inputStreamId);
		
		// Verify read()
		assertEquals(data, result);
		
		// Close input and output streams
		ioControllerFacade.closeInputStream(inputStreamId);
		ioControllerFacade.closeOutputStream(outputStreamId);
		
		// Exercise commit()
		String binaryObjectId1 = ioControllerFacade.commit(checkoutId);
		
		// Exercise checkOut(binaryObjectId) which verifies commit()
		checkoutId = ioControllerFacade.checkOut(binaryObjectId1);
		
		// Verify checkOut(binaryObjectId) and commit()
		// Verifies that the binaryObject contains the original data
		inputStreamId = ioControllerFacade.openInputStream(checkoutId);
		result = ioControllerFacade.read(inputStreamId);
		ioControllerFacade.closeInputStream(inputStreamId);
		assertEquals(data, result);
		
		// Write some new data (modify the object)
		int newData = 42;
		outputStreamId = ioControllerFacade.openOutputStream(checkoutId);
		ioControllerFacade.write(outputStreamId, newData);
		ioControllerFacade.closeOutputStream(outputStreamId);
		String binaryObjectId2 = ioControllerFacade.commit(checkoutId);
		
		// Verify that the new binary object is different from the old one
		assertFalse(binaryObjectId1.equals(binaryObjectId2));
		
		// Tear down
		ioControllerFacade.deleteBinaryObject(binaryObjectId1);
		ioControllerFacade.deleteBinaryObject(binaryObjectId2);
	}
}
