package transfercontroller;

/**
 * This class represents a set of objects to be transferred, as well as where
 * they are to be transferred to and the status of the operation. 
 */
public class TransferJob {
	private String[] containerIds;
	private String host;
	private int port;
	private String ticketId;
	
	/**
	 * Create a TransferJob.
	 * 
	 * @param containerIds ids of containers to transfer
	 * @param host host to connect to
	 * @param port port to connect to
	 */
	public TransferJob(String[] containerIds, String host, int port) {
		this.containerIds = containerIds;
		this.host = host;
		this.port = port;
	}

	/**
	 * Get the host.
	 * 
	 * @return the host to connect to
	 */
	public String getHost() {
		return host;
	}

	/**
	 * Set the host.
	 * 
	 * @param host the host to connect to
	 */
	public void setHost(String host) {
		this.host = host;
	}

	/**
	 * Get the port.
	 * 
	 * @return the port to connect to
	 */
	public int getPort() {
		return port;
	}

	/**
	 * Set the port.
	 * 
	 * @param port the port to connect to
	 */
	public void setPort(int port) {
		this.port = port;
	}

	/**
	 * Get the ids of the objects to transfer.
	 * 
	 * @return an array containing the ids
	 */
	public String[] getContainerIds() {
		return containerIds;
	}
	
	/**
	 * Set the ids of the containers to transfer.
	 * 
	 * @param containerIds an array containing the ids
	 */
	public void setContainerIds(String[] containerIds) {
		this.containerIds = containerIds;
	}

	/**
	 * Get the ticket id.
	 * 
	 * @return the id of the associated ticket
	 */
	public String getTicketId() {
		return ticketId;
	}

	/**
	 * Set the ticket id.
	 * 
	 * @param ticketId the id of the associated ticket
	 */
	public void setTicketId(String ticketId) {
		this.ticketId = ticketId;
	}
	
	/**
	 * The other transfer job is considered equal to this transfer job if and only if it is 
	 * of the same type and contains the same values.
	 * 
	 * @return true if the other transfer job is considered equal, false otherwise
	 */
	@Override
	public boolean equals(Object otherTransferJob) {
		// Check that the otherTransferJob is actually a TransferJob.
		if (!(otherTransferJob instanceof TransferJob)) {
			return false;
		}
		
		TransferJob transferJob = (TransferJob) otherTransferJob;
		
		// Check that either the references are equal (they are both null), or that the values are equal.
		boolean result = host == transferJob.getHost() || host.equals(transferJob.getHost());
		result &= port == transferJob.getPort();
		result &= ticketId == transferJob.getTicketId() || ticketId.equals(transferJob.getTicketId());
		
		if (containerIds != null) {
			// Loop through containerIds and check that the arrays are equal.
			for(int i = 0; i < containerIds.length; i++) {
				result &= containerIds[i].equals(transferJob.containerIds[i]);
			}
		} else {
			// If containerIds is null, check that they are both null.
			result &= containerIds == transferJob.getContainerIds(); 
		}
		
		return result;
	}
}
