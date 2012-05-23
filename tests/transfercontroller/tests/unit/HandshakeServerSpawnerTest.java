package transfercontroller.tests.unit;

import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Test;

import transfercontroller.handshake.HandshakeServerSpawner;
import transfercontroller.testdoubles.HandshakeServerFactoryMock;

public class HandshakeServerSpawnerTest {

	/**
	 * Create a TransferComponentFacadeSpawner, and check that it calls the factory and the resulting runnable.
	 * 
	 * @throws IOException
	 * @throws InterruptedException
	 */
	@Test
	public void spawnTest() throws IOException, InterruptedException {
		// Set up fixture
		HandshakeServerFactoryMock handshakeServerFactoryMock = new HandshakeServerFactoryMock(); 
		HandshakeServerSpawner handshakeServerSpawner = new HandshakeServerSpawner(handshakeServerFactoryMock, null, null, 0);  
		
		// Exercise SUT
		handshakeServerSpawner.spawn(null);
		
		// Wait for thread. RunnableMock will notify itself when it starts.
		synchronized (handshakeServerFactoryMock.getRunnableMock()) {
			handshakeServerFactoryMock.getRunnableMock().wait();
		}
		
		// Verify outcome
		assertTrue(handshakeServerFactoryMock.invocationLog.getInvocationStatus("createHandshakeServer"));
		assertTrue(handshakeServerFactoryMock.getRunnableMock().invocationLog.getInvocationStatus("run"));
		
		// Tear down
		handshakeServerFactoryMock.getRunnableMock().die();
	}

}
