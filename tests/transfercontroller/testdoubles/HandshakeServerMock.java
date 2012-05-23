package transfercontroller.testdoubles;

import transfercontroller.Ticket;
import transfercontroller.handshake.HandshakeException;
import transfercontroller.handshake.HandshakeServer;

/**
 * Returns preconfigured ids to ticket negotiation requests.
 */
public class HandshakeServerMock extends HandshakeServer {
	public String[] idsToSuggest;
	public int i = 0;
	
	public HandshakeServerMock(String[] idsToSuggest) {
		super(null, new Ticket(), null, 0);
		this.idsToSuggest = idsToSuggest;
	}
	
	@Override
	public void confirmTicketId(String ticketId) throws HandshakeException {
		
	}
	
	@Override
	public String negotiateTicketId(String ticketIdSuggestion) {
		if (i >= idsToSuggest.length)
			return null;
		
		return idsToSuggest[i++];
	}
	
	@Override
	public void done() {
		
	}
}
