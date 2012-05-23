package utilities.testdoubles;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import testutilities.InvocationLog;

/**
 * This class is used as a mock for a ServerSocket.
 *
 */
public class ServerSocketMock extends ServerSocket {
	public InvocationLog invocationLog;
	private Socket socket;

	/**
	 * Create a new ServerSocketMock.
	 * 
	 * @param socket the socket to return from accept()
	 * @throws IOException
	 */
	public ServerSocketMock(Socket socket) throws IOException {
		this.invocationLog = new InvocationLog();
		this.socket = socket;
	}
	
	@Override
	public Socket accept() throws IOException {
		invocationLog.addInvocation("accept");
		return socket;
	}
	
	@Override
	public void close() throws IOException {
		invocationLog.addInvocation("close");
	}

}
