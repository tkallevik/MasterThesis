package transfercontroller.handshake;

import java.io.IOException;
import java.net.Socket;

import transfercontroller.TicketList;
import utilities.ISocketHandlerSpawner;
import utilities.LoopRunnable;

/**
 * This class will spawn HandshakeServer sessions.
 *
 */
public class HandshakeServerSpawner implements ISocketHandlerSpawner {
	private HandshakeServerFactory handshakeServerFactory;
	private TicketList ticketList;
	private String transferComponentHost;
	private int transferComponentPort;
	
	/**
	 * Create a new TransferComponentFacadeSpawner.
	 * 
	 * @param handshakeServerFactory the HandshakeServerFactory to use for HandshakeServer construction
	 * @param ticketList the TicketList that will be used by the HandshakeServer sessions
	 * @param transferComponentHost the host to use for incoming transfers
	 * @param transferComponentPort the port to use for incoming transfers
	 */
	public HandshakeServerSpawner(HandshakeServerFactory handshakeServerFactory, TicketList ticketList, String transferComponentHost, int transferComponentPort) {
		this.handshakeServerFactory = handshakeServerFactory;
		this.ticketList = ticketList;
		this.transferComponentHost = transferComponentHost;
		this.transferComponentPort = transferComponentPort;
	}
	
	@Override
	public void spawn(Socket socket) throws IOException {
		LoopRunnable loopRunnable = handshakeServerFactory.createHandshakeServer(socket, ticketList, transferComponentHost, transferComponentPort);
		new Thread(loopRunnable, "HandshakeServer").start();
	}

	@Override
	public void setThreadName(String threadName) {
		// TODO Auto-generated method stub
		
	}

}
