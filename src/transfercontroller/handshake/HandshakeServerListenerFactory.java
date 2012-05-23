package transfercontroller.handshake;

import java.io.IOException;
import java.net.ServerSocket;

import transfercontroller.TicketList;
import utilities.Listener;
import utilities.LoopRunnable;

/**
 * This class creates the subsystem for listening for handshakes.
 *
 */
public class HandshakeServerListenerFactory {
	/**
	 * Create the subsystem for listening for handshakes.
	 * 
	 * @param handshakePort the port to listen on
	 * @param ticketList the TicketList to work with
	 * @param transferComponentHost the host to use for incoming transfers
	 * @param transferComponentPort the port to use for incoming transfers
	 * @return a LoopRunnable representing the subsystem
	 * @throws IOException
	 */
	public LoopRunnable createHandshakeServerListener(int handshakePort, TicketList ticketList, String transferComponentHost, int transferComponentPort) throws IOException {
		ServerSocket serverSocket = new ServerSocket(handshakePort);
		HandshakeServerFactory handshakeServerFactory = new HandshakeServerFactory();
		HandshakeServerSpawner handshakeServerSpawner = new HandshakeServerSpawner(handshakeServerFactory, ticketList, transferComponentHost, transferComponentPort);
		Listener listener = new Listener(serverSocket, handshakeServerSpawner);
		LoopRunnable loopRunnable = new LoopRunnable(listener, null);
		
		return loopRunnable;
	}
}
