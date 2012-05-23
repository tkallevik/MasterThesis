package transfercomponent.tests.unit;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.junit.Test;

import transfercomponent.StreamCopier;
import utilities.ServiceUnavailableException;

public class StreamCopierTest {
	
	/**
	 * Gives the SUT two mock streams and tests the copyByte method.
	 * 
	 * @throws IOException
	 * @throws ServiceUnavailableException 
	 */
	@Test
	public void copyByteTest() throws IOException, ServiceUnavailableException {
		// Set up fixture
		// Create mock InputStream.
		byte[] inputData = new byte[2];
		inputData[0] = 0x4;
		inputData[1] = 0x5;
		ByteArrayInputStream inputStream = new ByteArrayInputStream(inputData);
		
		// Create mock OutputStream.
		byte[] outputData;
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		
		StreamCopier streamCopier = new StreamCopier(inputStream, outputStream);
		
		// Exercise SUT
		streamCopier.copyByte();
		streamCopier.copyByte();
		streamCopier.terminateAction();
		
		// Verify outcome
		// Get data from mock OutputStream, verify against input data.
		outputData = outputStream.toByteArray();
		
		assertEquals(inputData[0], outputData[0]);		
		assertEquals(inputData[1], outputData[1]);
		
	}

}
