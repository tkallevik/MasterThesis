package transfercontroller;

import java.io.IOException;

import transfercomponent.ITransferComponentFacade;
import transfercontroller.handshake.HandshakeClientFactory;
import transfercontroller.handshake.HandshakeServerListenerFactory;

/**
 * This class creates a TransferControllerFacade.
 *
 */
public class TransferControllerFacadeFactory {

	/**
	 * Create a new TransferControllerFacade.
	 * 
	 * @param transferComponentFacade the transfer component to use for file transfers
	 * @param handshakeClientFactory the factory to use for creating HandshakeClients
	 * @param handshakePort the port to listen for handshakes on
	 * @param transferComponentHost the host to use for incoming transfers
	 * @param transferComponentPort the port to use for incoming transfers
	 * @return the created TransferControllerFacade
	 * @throws IOException 
	 */
	public TransferControllerFacade createFacade(ITransferComponentFacade transferComponentFacade, 
			String handshakeHost, int handshakePort, String transferComponentHost, int transferComponentPort) throws IOException {
		// Set up TicketList
		TicketList ticketList = new TicketList();
		
		// Set up HandshakeClientFactory
		HandshakeClientFactory handshakeClientFactory = new HandshakeClientFactory();
		
		// Set up the actual facade
		TransferControllerFacade transferControllerFacade = new TransferControllerFacade(ticketList, transferComponentFacade, handshakeClientFactory, handshakeHost, handshakePort);
		
		// Set up HandshakeServerListener
		HandshakeServerListenerFactory handshakeServerListenerFactory = new HandshakeServerListenerFactory();
		new Thread(handshakeServerListenerFactory.createHandshakeServerListener(handshakePort, ticketList, transferComponentHost, transferComponentPort), "HandshakeServerListener").start();	
		
		return transferControllerFacade;
	}
}
