package transfercontroller.testdoubles;

import java.io.IOException;
import java.net.Socket;

import testutilities.InvocationLog;
import transfercontroller.TicketList;
import transfercontroller.handshake.HandshakeServerFactory;
import utilities.testdoubles.LoopRunnableMock;

/**
 * This class is a mock for the HandshakeServerFactory.
 * It traps invocations and returns a LoopRunnableMock.
 *
 */
public class HandshakeServerFactoryMock extends HandshakeServerFactory {
	public InvocationLog invocationLog;
	private LoopRunnableMock loopRunnableMock;
	
	public HandshakeServerFactoryMock() {
		invocationLog = new InvocationLog();
		loopRunnableMock = new LoopRunnableMock();
	}
	
	public LoopRunnableMock getRunnableMock() {
		return loopRunnableMock;
	}
	
	@Override
	public LoopRunnableMock createHandshakeServer(Socket socket, TicketList ticketList, String host, int port) throws IOException {
		invocationLog.addInvocation("createHandshakeServer");
		return loopRunnableMock;
	}
}
