package transfercontroller;

import java.io.IOException;
import java.net.UnknownHostException;

import transfercomponent.ITransferComponentFacade;
import transfercontroller.handshake.HandshakeClient;
import transfercontroller.handshake.HandshakeClientFactory;
import transfercontroller.handshake.HandshakeException;
import utilities.ServiceUnavailableException;

/**
 * This class acts as a facade to the Transfer Controller component.
 */
public class TransferControllerFacade implements ITransferControllerFacade {
	private TicketList ticketList;
	private ITransferComponentFacade transferComponentFacade;
	private HandshakeClientFactory handshakeClientFactory;
	private String handshakeHost;
	private int handshakePort;
	
	public TransferControllerFacade(TicketList ticketList, ITransferComponentFacade transferComponentFacade, 
			HandshakeClientFactory handshakeClientFactory, String handshakeHost, int handshakePort) {
		this.ticketList = ticketList;
		this.transferComponentFacade = transferComponentFacade;
		this.handshakeClientFactory = handshakeClientFactory;
		this.handshakeHost = handshakeHost;
		this.handshakePort = handshakePort;
	}

	@Override
	public void sendContainers(String[] containerIds, String host, int port) throws UnknownHostException, IOException, ServiceUnavailableException {
		TransferJob transferJob = new TransferJob(containerIds, host, port);
		
		try {
			HandshakeClient handshakeClient = handshakeClientFactory.createHandshakeClient(transferJob, ticketList);
			Ticket ticket = handshakeClient.perform();
			ticketList.add(ticket);
			transferComponentFacade.pushFile(ticket);
			transferJob.setTicketId(ticket.getId());
		} catch (HandshakeException e) {
			IOException ioException = new IOException("Unable to send objects.");
			ioException.initCause(e);
			throw ioException;
		}
	}

	@Override
	public String getHost() {
		return handshakeHost;
	}

	@Override
	public int getPort() {
		return handshakePort;
	}
}
