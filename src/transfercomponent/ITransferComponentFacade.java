package transfercomponent;

import java.io.IOException;
import java.net.UnknownHostException;

import transfercontroller.Ticket;
import utilities.ServiceUnavailableException;

public interface ITransferComponentFacade {

	/**
	 * Stop a transfer with a given ticket id.
	 * 
	 * @param ticketId the ticket id of the transfer to stop
	 * @throws ServiceUnavailableException 
	 * @throws IOException 
	 */
	public abstract void stop(String ticketId) throws ServiceUnavailableException, IOException;

	/**
	 * Execute a ticket.
	 * 
	 * @param ticket the ticket to execute
	 * @throws UnknownHostException
	 * @throws IOException
	 * @throws ServiceUnavailableException if the transfer component or a required service could not be reached
	 */
	public abstract void pushFile(Ticket ticket) throws UnknownHostException, IOException, ServiceUnavailableException;

	/**
	 * Check if a given ticket is active in the TransferComponent.
	 * 
	 * @param ticketId the id of the ticket
	 * @return true if the ticket is registered with the TransferComponent, false otherwise
	 * @throws ServiceUnavailableException 
	 */
	public boolean hasActiveTicket(String ticketId) throws ServiceUnavailableException;

	public void listenForFiles(int transferPort) throws IOException;

}