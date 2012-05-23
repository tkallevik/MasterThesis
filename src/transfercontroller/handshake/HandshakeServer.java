package transfercontroller.handshake;

import transfercontroller.Ticket;
import transfercontroller.TicketList;

/**
 * This class responds to negotiations of a ticket with a corresponding HandshakeClient.
 */
public class HandshakeServer implements IHandshakeServer {
	private TicketList ticketList;
	private Ticket ticket;
	private String host;
	private int port;
	
	/**
	 * Create a HandshakeServer object.
	 * 
	 * @param ticketList the list in which this object will store its tickets
	 * @param ticket a ticket to store the negotiated information
	 * @param host the host to give during handshakes
	 * @param port the port to give during handshakes
	 */
	public HandshakeServer(TicketList ticketList, Ticket ticket, String host, int port) {
		this.ticketList = ticketList;
		this.ticket = ticket;
		this.host = host;
		this.port = port;
	}
	
	@Override
	public String negotiateTicketId(String ticketIdSuggestion) {	
		if(ticketList.isAvailable(ticketIdSuggestion)) {
			ticket.setId(ticketIdSuggestion);
			return ticketIdSuggestion;
		}
		
		return ticketList.suggestAvailableId();
	}

	@Override
	public void confirmTicketId(String ticketId) throws HandshakeException {
		if(!ticketId.equals(ticket.getId())) {
			throw new HandshakeException("Client tried to confirm a non-negoatiated ticket id.");
		}
	}

	@Override
	public String requestHost() {
		return host;
	}

	@Override
	public int requestPort() {
		return port;
	}

	@Override
	public void done() {
		ticketList.add(ticket);
	}
}
