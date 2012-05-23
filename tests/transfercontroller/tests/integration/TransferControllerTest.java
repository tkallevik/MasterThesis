package transfercontroller.tests.integration;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Test;

import transfercomponent.testdoubles.TransferComponentFacadeMock;
import transfercontroller.ITransferControllerFacade;
import transfercontroller.TransferControllerFacadeFactory;
import utilities.ServiceUnavailableException;

public class TransferControllerTest {

	/**
	 * Set up a TransferController, use it to sendObjects, 
	 * verify that the transferComponentFacade was invoked with the correct arguments.
	 * 
	 * @throws InterruptedException
	 * @throws IOException
	 * @throws ServiceUnavailableException 
	 */
	@Test
	public void sendObjectsTest() throws InterruptedException, IOException, ServiceUnavailableException {
		// Set up fixture
		String[] containerIds = {"foo", "bar"};
		String host = "localhost";
		int port = 9765;
		
		TransferControllerFacadeFactory transferControllerFacadeFactory = new TransferControllerFacadeFactory();
		TransferComponentFacadeMock transferComponentFacadeMock = new TransferComponentFacadeMock();
		ITransferControllerFacade transferControllerFacade = transferControllerFacadeFactory.createFacade(transferComponentFacadeMock, host, port, null, 0);
		
		// Exercise SUT
		transferControllerFacade.sendContainers(containerIds, host, port);
		
		// Verify outcome
		assertTrue(transferComponentFacadeMock.invocationLog.getInvocationStatus("pushFile"));
		assertNotNull(transferComponentFacadeMock.ticketForPush);
		assertArrayEquals(containerIds, transferComponentFacadeMock.ticketForPush.getContainerIds());
	}

}
