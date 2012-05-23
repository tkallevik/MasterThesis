package transfercontroller;

/**
 * This is a model object to hold meta data for a particular transfer agreed upon by
 * both hosts.
 */
public class Ticket {
	private String id;
	private String host;
	private int port;
	private String[] containerIds;
	
	/**
	 * Creates a new Ticket with specified id.
	 * 
	 * @param id the id of the new ticket
	 */
	public Ticket(String id) {
		this.id = id;
	}
	
	/**
	 * Creates a new Ticket.
	 */
	public Ticket() {
	}

	/**
	 * Get the ticket's id.
	 * 
	 * @return the id of the ticket
	 */
	public String getId() {
		return id;
	}

	/**
	 * Set the ticket's id.
	 * 
	 * @param id the id to be set
	 */
	public void setId(String id) {
		this.id = id;
	}
	
	/**
	 * Get the host to connect to.
	 * 
	 * @return the host
	 */
	public String getHost() {
		return host;
	}

	/**
	 * Set the host to connect to.
	 * 
	 * @param host the host
	 */
	public void setHost(String host) {
		this.host = host;
	}

	/**
	 * Get the port to connect to.
	 * 
	 * @return the port
	 */
	public int getPort() {
		return port;
	}

	/**
	 * Set the port to connect to.
	 * 
	 * @param port the port
	 */
	public void setPort(int port) {
		this.port = port;
	}

	/**
	 * Get ids of containers to transfer.
	 * 
	 * @return an array containing the container ids
	 */
	public String[] getContainerIds() {
		return containerIds;
	}

	/**
	 * Set the ids of containers to transfer.
	 * 
	 * @param containerIds an array containing the container ids
	 */
	public void setContainerIds(String[] containerIds) {
		this.containerIds = containerIds;
	}

	/**
	 * The other ticket is considered equal to this ticket if and only if it is 
	 * of the same type and contains the same values.
	 * 
	 * @return true if the other ticket is considered equal, false otherwise
	 */
	@Override
	public boolean equals(Object otherTicket) {
		// Check that the otherTicket is actually a Ticket.
		if (!(otherTicket instanceof Ticket)) {
			return false;
		}
		
		Ticket ticket = (Ticket)otherTicket;
		
		// Check that either the references are equal (they are both null), or that the values are equal.
		boolean result = id == ticket.getId() || id.equals(ticket.getId());
		result &= host == ticket.getHost() || host.equals(ticket.getHost());
		result &= port == ticket.getPort();
		
		if (containerIds != null) {
			// Loop through containerIds and check that the arrays are equal.
			for(int i = 0; i < containerIds.length; i++) {
				result &= containerIds[i].equals(ticket.containerIds[i]);
			}
		} else {
			// If containerIds is null, check that they are both null.
			result &= containerIds == ticket.getContainerIds(); 
		}
		
		return result;
	}
}
