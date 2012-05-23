package transfercontroller.handshake;

public interface IHandshakeServer {

	/**
	 * Check availability of suggested ticket id and return confirmation or counter suggestion.
	 * 
	 * @param ticketIdSuggestion ticket id to suggest
	 * @return ticketIdSuggestion an available ticket id 
	 */
	public String negotiateTicketId(String ticketIdSuggestion) throws HandshakeException;

	/**
	 * Confirm a ticket id after it has been agreed upon.
	 * 
	 * @param ticketId the agreed ticket id
	 * @throws HandshakeException if the ticket id was in fact not agreed upon.
	 */
	public void confirmTicketId(String ticketId) throws HandshakeException;

	/**
	 * Request host to transfer to.
	 * 
	 * @return the hostname
	 */
	public String requestHost() throws HandshakeException;

	/**
	 * Request port to use for transfer.
	 * 
	 * @return the port
	 */
	public int requestPort() throws HandshakeException;
	
	/**
	 * Tell the HandshakeServer that the handshake is complete.
	 */
	public void done() throws HandshakeException;

}