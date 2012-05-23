package transfercontroller.tests.unit;

import static org.junit.Assert.*;

import org.junit.Test;

import transfercontroller.Ticket;
import transfercontroller.TicketList;

public class TicketListTest {

	/**
	 * Create a TicketList, add a ticket to it, retrieve the ticket and verify it.
	 */
	@Test
	public void addTest() {
		// Set up fixture
		TicketList ticketList = new TicketList();
		Ticket originalTicket = new Ticket();
		originalTicket.setId("foo");		
		
		// Exercise SUT
		ticketList.add(originalTicket);
		
		// Verify outcome
		Ticket ticket = ticketList.get(originalTicket.getId());
		assertEquals(originalTicket, ticket);
	}
	
	/**
	 * Create a TicketList, add a ticket to it, tell the SUT to delete the ticket and verify that it was deleted.
	 */
	@Test
	public void deleteTest() {
		// Set up fixture
		TicketList ticketList = new TicketList();
		Ticket ticket = new Ticket();
		String ticketId = "foo";
		ticket.setId(ticketId);
		ticketList.add(ticket);
		
		// Exercise SUT
		ticketList.delete(ticketId);
		
		// Verify outcome
		assertNull(ticketList.get(ticketId));
	}

}
