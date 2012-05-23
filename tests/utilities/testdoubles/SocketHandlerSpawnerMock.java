package utilities.testdoubles;

import java.io.IOException;
import java.net.Socket;

import testutilities.InvocationLog;
import utilities.ISocketHandlerSpawner;

/**
 * This class is used to as a mock for the SocketHandlerSpawner.
 *
 */
public class SocketHandlerSpawnerMock implements ISocketHandlerSpawner {
	public InvocationLog invocationLog = new InvocationLog();
	public Socket receivedSocket; 
	
	@Override
	public void spawn(Socket socket) throws IOException {
		invocationLog.addInvocation("spawn");
		receivedSocket = socket;
	}

	@Override
	public void setThreadName(String threadName) {
		invocationLog.addInvocation("setThreadName");
	}

}
