package utilities.tests;

import static org.junit.Assert.*;

import java.io.IOException;
import java.net.Socket;

import org.junit.Test;

import utilities.Listener;
import utilities.testdoubles.ServerSocketMock;
import utilities.testdoubles.SocketHandlerSpawnerMock;

public class ListenerTest {
	private Listener listener;
	
	/**
	 * Set up a listener with mocks, verify that it invokes the correct methods and also
	 * that the Socket from the ServerSocketMock is handed over to the SocketHandlerSpawnerMock.
	 * 
	 * @throws IOException
	 * @throws InterruptedException
	 */
	@Test
	public void listenTest() throws IOException, InterruptedException {
			// Set up fixture	
			Socket socket = new Socket();
			ServerSocketMock serverSocketMock = new ServerSocketMock(socket);
			SocketHandlerSpawnerMock socketHandlerSpawnerMock = new SocketHandlerSpawnerMock();
			listener = new Listener(serverSocketMock, socketHandlerSpawnerMock);
			
			// Execute SUT
			listener.listen();
			
			// Verify outcome
			assertTrue(serverSocketMock.invocationLog.getInvocationStatus("accept"));
			assertTrue(socketHandlerSpawnerMock.invocationLog.getInvocationStatus("spawn"));
			assertEquals(socket, socketHandlerSpawnerMock.receivedSocket);
	}
	
	/**
	 * Set up a listener with mocks, and make sure that it closes the ServerSocket on terminateAction.
	 * 
	 * @throws IOException
	 */
	@Test
	public void terminateActiontest() throws IOException {
		// Set up fixture	
		Socket socket = new Socket();
		ServerSocketMock serverSocketMock = new ServerSocketMock(socket);
		listener = new Listener(serverSocketMock, null);
		
		// Execute SUT
		listener.terminateAction();
		
		// Verify outcome
		assertTrue(serverSocketMock.invocationLog.getInvocationStatus("close"));
	}

}
