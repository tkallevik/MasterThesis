package transfercontroller.handshake;

import transfercontroller.Ticket;
import transfercontroller.TicketList;

/**
 * This class negotiates a ticket with a HandshakeServer counterpart.
 */
public class HandshakeClient {
	private TicketList ticketList;
	private Ticket ticket;
	private IHandshakeServer handshakeServer;
	
	/**
	 * Create a HandshakeClient object.
	 * 
	 * @param ticketList the list in which this object will store its tickets
	 * @param handshakeServer the corresponding handshake object representing the other host
	 */
	public HandshakeClient(TicketList ticketList, IHandshakeServer handshakeServer, Ticket ticket) {
		this.ticketList = ticketList;
		this.ticket = ticket;
		this.handshakeServer = handshakeServer;
	}
	
	/**
	 * Perform the handshake.
	 * 
	 * @return the negotiated ticket
	 * @throws HandshakeException
	 */
	public Ticket perform() throws HandshakeException {
		String ticketId = negotiateTicketId();
		String host = handshakeServer.requestHost();
		int port = handshakeServer.requestPort();
		handshakeServer.done();
		
		ticket.setId(ticketId);
		ticket.setHost(host);
		ticket.setPort(port);
		
		return ticket;
	}
	
	/**
	 * Keep suggesting available ticket ids until it is returned, or the counter suggestion is 
	 * available. Confirm the final id with the counterpart before returning.
	 * 
	 * @return negotioated ticket id
	 * @throws HandshakeException
	 */
	private String negotiateTicketId() throws HandshakeException {
		String ticketIdSuggestion;
		String recievedTicketId;
		do {
			ticketIdSuggestion = ticketList.suggestAvailableId();
			recievedTicketId = handshakeServer.negotiateTicketId(ticketIdSuggestion);
		} while(!(recievedTicketId.equals(ticketIdSuggestion) || ticketList.isAvailable(recievedTicketId)));
		
		handshakeServer.confirmTicketId(ticketIdSuggestion);
		
		return recievedTicketId;
	}
}
