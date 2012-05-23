package transfercomponent.tests.integration;

import static org.junit.Assert.assertArrayEquals;
import iocontroller.testdoubles.IOControllerFacadeMock;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import loadingdock.testdoubles.LoadingDockFacadeMock;

import org.junit.Test;

import transfercomponent.TransferComponentFacade;
import transfercomponent.TransferComponentFacadeFactory;
import transfercontroller.Ticket;
import utilities.ServiceUnavailableException;

public class PushTest {

	@Test
	public void test() throws IOException, InterruptedException, NoSuchAlgorithmException, ServiceUnavailableException {
		// Set up fixture
		String host = "localhost";
		int port = 9994;
		Ticket ticket = new Ticket();
		ticket.setHost(host);
		ticket.setPort(port);
		String[] containerIds = {"1"}; 
		ticket.setContainerIds(containerIds);
		
		// Set up IOControllerFacadeMock
		int[] dataToReturn = {1,3,4,4};
		int[] dataWritten = new int[dataToReturn.length];
		IOControllerFacadeMock ioControllerFacadeMock = new IOControllerFacadeMock(dataToReturn, dataWritten);
		
		// Set up LoadingDockFacadeMock
		LoadingDockFacadeMock loadingDockFacadeMock = new LoadingDockFacadeMock(ioControllerFacadeMock);
		
		// Set up transfer component facade
		TransferComponentFacadeFactory transferComponentFacadeFactory = new TransferComponentFacadeFactory();
		TransferComponentFacade transferComponentFacade = transferComponentFacadeFactory.createFacade(loadingDockFacadeMock);
		
		// Exercise SUT		
		transferComponentFacade.listenForFiles(port);
		Thread.sleep(500);
		transferComponentFacade.pushFile(ticket);

		Thread.sleep(500);
		
		// Verify outcome
		assertArrayEquals(dataToReturn, dataWritten);
	}
}
