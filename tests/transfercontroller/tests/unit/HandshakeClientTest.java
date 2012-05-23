package transfercontroller.tests.unit;

import static org.junit.Assert.*;

import org.junit.Test;

import transfercontroller.Ticket;
import transfercontroller.handshake.HandshakeClient;
import transfercontroller.handshake.HandshakeException;
import transfercontroller.testdoubles.HandshakeServerMock;
import transfercontroller.testdoubles.TicketListSuggestMock;

public class HandshakeClientTest {

	/**
	 * Sets the SUT up for smooth sailing with a ticketlist and counterpart that will
	 * suggest/accept the same ticket ids. Verify that the method returns the right id.
	 * 
	 * @throws HandshakeException 
	 */
	@Test
	public void performTest1() throws HandshakeException {
		// Set up fixture
		String[] idsToSuggest = {"foobar"};
		TicketListSuggestMock ticketListSuggestMock = new TicketListSuggestMock(idsToSuggest);
		HandshakeServerMock handshakeServerMock = new HandshakeServerMock(idsToSuggest);
		
		HandshakeClient handshakeClient = new HandshakeClient(ticketListSuggestMock, handshakeServerMock, new Ticket());
	
		// Exercise SUT
		Ticket ticket = handshakeClient.perform();
		
		// Verify outcome
		assertEquals(idsToSuggest[0], ticket.getId());
	}
	
	/**
	 * Sets up the SUT with a counterpart that will reject the first offer and suggest a different
	 * id. Verify that the method returns the right id.
	 * 
	 * @throws HandshakeException 
	 */
	@Test
	public void performTest2() throws HandshakeException {
		// Set up fixture
		String[] idsToSuggestA = {"foo"};
		String[] idsToSuggestB = {"bar"};
		TicketListSuggestMock ticketListSuggestMock = new TicketListSuggestMock(idsToSuggestA);
		HandshakeServerMock handshakeServerMock = new HandshakeServerMock(idsToSuggestB);
		
		HandshakeClient handshakeClient = new HandshakeClient(ticketListSuggestMock, handshakeServerMock, new Ticket());
	
		// Exercise SUT
		Ticket ticket = handshakeClient.perform();
		
		// Verify outcome
		assertEquals(idsToSuggestB[0], ticket.getId());
	}
	
	/**
	 * Set up the SUT with a counterpart that will reject the two first suggestions, and 
	 * make two suggestions that are not available in the SUT's list. Test that it returns
	 * the value that is available on both parts.
	 * 
	 * @throws HandshakeException 
	 */
	@Test
	public void performTest3() throws HandshakeException {
		// Set up fixture
		String[] idsToSuggestA = {"foo", "bar", "foobar"};
		String[] idsToOccupy = {"notfoo", "notbar"};
		String[] idsToSuggestB = {"notfoo", "notbar", "foobar"};
		TicketListSuggestMock ticketListSuggestMock = new TicketListSuggestMock(idsToSuggestA);
		ticketListSuggestMock.occupy(idsToOccupy);
		HandshakeServerMock handshakeServerMock = new HandshakeServerMock(idsToSuggestB);
		
		HandshakeClient handshakeClient = new HandshakeClient(ticketListSuggestMock, handshakeServerMock, new Ticket());
	
		// Exercise SUT
		Ticket ticket = handshakeClient.perform();
		
		// Verify outcome
		assertEquals(idsToSuggestA[2], ticket.getId());
	}
}
