package loadingdock.tests.integration;

import static org.junit.Assert.*;

import java.io.IOException;

import iocontroller.FileDeletionException;
import iocontroller.IIOControllerFacade;
import iocontroller.IOControllerFacadeFactory;
import iocontroller.utilities.BinaryObjectCreator;
import loadingdock.ContainerHeader;
import loadingdock.ContainerHeaderInputStream;
import loadingdock.ContainerHeaderLengthCodec;
import loadingdock.ContainerHeaderOutputStream;
import loadingdock.ContainerInputStream;
import loadingdock.ContainerOutputStream;

import org.junit.Test;

public class ContainerStreamTest {
	private IIOControllerFacade ioControllerFacadeA;
	private IIOControllerFacade ioControllerFacadeB;
	private int[] componentHeader = {33, 67, 96, 13};
	private int[] data = {101, 77, 41, 3, 98, 105};

	@Test
	public void test() throws IOException, FileDeletionException {
		// Set up fixture
		int containerHeaderLengthNumberOfBytes = 8;
		IOControllerFacadeFactory ioControllerFacadeFactory = new IOControllerFacadeFactory();
		
		// Set up A
		String repositoryPathA = "tests/dummy/ioControllerRepository/A/";		
		ioControllerFacadeA = ioControllerFacadeFactory.createFacade(repositoryPathA);
		
		ContainerHeader containerHeaderA = createContainer();
		ContainerHeaderInputStream containerHeaderInputStream = new ContainerHeaderInputStream(containerHeaderA);
		ContainerHeaderLengthCodec containerHeaderLengthCodecA = new ContainerHeaderLengthCodec(containerHeaderLengthNumberOfBytes);
		ContainerInputStream containerInputStream = new ContainerInputStream(ioControllerFacadeA, containerHeaderInputStream, containerHeaderLengthCodecA);
		
		// Set up B
		String repositoryPathB = "tests/dummy/ioControllerRepository/B/";		
		ioControllerFacadeB = ioControllerFacadeFactory.createFacade(repositoryPathB);
		
		ContainerHeader containerHeaderB = new ContainerHeader();
		ContainerHeaderOutputStream containerHeaderOutputStream = new ContainerHeaderOutputStream(containerHeaderB);
		ContainerHeaderLengthCodec containerHeaderLengthCodecB = new ContainerHeaderLengthCodec(containerHeaderLengthNumberOfBytes);
		ContainerOutputStream containerOutputStream = new ContainerOutputStream(ioControllerFacadeB, containerHeaderOutputStream, containerHeaderLengthCodecB);
		
		// Exercise SUT
		int b;
		do {
			b = containerInputStream.read();
			
			if (b != -1) {
				containerOutputStream.write(b);
			}
		} while(b != -1);
		
		containerInputStream.close();
		containerOutputStream.close();
		
		// Verify outcome
		String serializedContainerHeaderA = containerHeaderA.serialize();
		String serializedContainerHeaderB = containerHeaderB.serialize();
		assertEquals(serializedContainerHeaderA, serializedContainerHeaderB);
		
		// Tear down
		ioControllerFacadeA.deleteBinaryObject(containerHeaderA.getComponentHeaderBinaryObjectId());
		ioControllerFacadeB.deleteBinaryObject(containerHeaderB.getComponentHeaderBinaryObjectId());
		ioControllerFacadeA.deleteBinaryObject(containerHeaderA.getDataBinaryObjectId());
		ioControllerFacadeB.deleteBinaryObject(containerHeaderB.getDataBinaryObjectId());
	}
	
	private ContainerHeader createContainer() throws IOException {
		String componentHeaderBinaryObjectId = BinaryObjectCreator.createBinaryObject(ioControllerFacadeA, componentHeader);
		long componentHeaderSize = componentHeader.length;
		String dataBinaryObjectId = BinaryObjectCreator.createBinaryObject(ioControllerFacadeA, data);
		long dataSize = data.length;
		
		return new ContainerHeader("testContainer", "foobar", componentHeaderBinaryObjectId, componentHeaderSize, dataBinaryObjectId, dataSize);
	}
}
