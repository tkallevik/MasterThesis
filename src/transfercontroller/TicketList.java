package transfercontroller;

import java.util.HashMap;

/**
 * This class maintains a list of tickets. It keeps its internal key values in sync
 * with its tickets' ids so as to easily retrieve them.
 */
public class TicketList {
	private HashMap<String, Ticket> list;

	/**
	 *  Create a new TicketList
	 */
	public TicketList() {
		list = new HashMap<String, Ticket>();
	}
	
	/**
	 * Get a ticket with a specified id from the list.
	 * 
	 * @param ticketId the id of the ticket to retrieve
	 * @return the ticket associated with the given id
	 */
	public Ticket get(String ticketId) {
		return list.get(ticketId);
	}

	/**
	 * Add a ticket to the list.
	 * 
	 * @param ticket the ticket to add
	 */
	public void add(Ticket ticket) {
		list.put(ticket.getId(), ticket);
	}

	/**
	 * Check the list to see if the specified id is available.
	 * 
	 * @param ticketId the id to check
	 * @return true if id is available, false otherwise
	 */
	public boolean isAvailable(String ticketId) {
		return !list.containsKey(ticketId);
	}	
	
	/**
	 * Generates a random id, checks its availability, loop if 
	 * it's occupied and return the first available one.
	 * 
	 * @return a random, available Integer object
	 */
	public String suggestAvailableId() {
		String ticketIdSuggestion;
		do {
			ticketIdSuggestion = String.valueOf(Math.random()*10000);
		} while(!isAvailable(ticketIdSuggestion));
		
		return ticketIdSuggestion;
	}

	/**
	 * Delete the ticket with the given id from the list.
	 * 
	 * @param ticketId the id of the ticket to delete
	 */
	public void delete(String ticketId) {
		list.remove(ticketId);
	}
}
