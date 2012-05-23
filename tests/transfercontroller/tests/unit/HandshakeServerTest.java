package transfercontroller.tests.unit;

import static org.junit.Assert.*;

import org.junit.Test;

import transfercontroller.Ticket;
import transfercontroller.handshake.HandshakeException;
import transfercontroller.handshake.HandshakeServer;
import transfercontroller.handshake.IHandshakeServer;
import transfercontroller.testdoubles.TicketListMock;
import transfercontroller.testdoubles.TicketListSuggestMock;

public class HandshakeServerTest {
	
	/**
	 * Set up a fixture that looks like an id has been agreed upon. 
	 * Verify that the confirm method throws a HandshakeException if it is passed
	 * a different id.
	 * 
	 * @throws HandshakeException
	 */
	@Test(expected=HandshakeException.class)
	public void confirmTicketIdTest1() throws HandshakeException {
		// Set up fixture
		String ticketId = "foo";
		Ticket ticket = new Ticket(ticketId);
		TicketListSuggestMock ticketListSuggestMock = new TicketListSuggestMock(null);
		IHandshakeServer handshakeServer = new HandshakeServer(ticketListSuggestMock, ticket, null, 0);
		
		// Exercise SUT
		handshakeServer.confirmTicketId("bar");
		
		// Verify outcome
	}
	
	/**
	 * Suggest an id we know to be available. Verify that it is set on the ticket and returned.
	 * @throws HandshakeException 
	 */
	@Test
	public void negotiateTicketIdTest1() throws HandshakeException {
		// Set up fixture
		String ticketId = "foobar";
		TicketListSuggestMock ticketListSuggestMock = new TicketListSuggestMock(null);
		Ticket ticket = new Ticket();
		IHandshakeServer handshakeServer = new HandshakeServer(ticketListSuggestMock, ticket, null, 0);
		
		// Exercise SUT
		String ticketIdRecieved = handshakeServer.negotiateTicketId(ticketId);
		
		// Verify outcome
		assertEquals(ticketId, ticketIdRecieved);
		assertEquals(ticketId, ticket.getId());
	}
	
	/**
	 * Suggest an id we know to be occupied and verify that the method returns an available
	 * counter suggestion.
	 * @throws HandshakeException 
	 */
	@Test
	public void negotiateTicketIdTest2() throws HandshakeException {
		// Set up fixture
		Ticket ticket = new Ticket();
		String[] idsToSuggest = {"foo"};
		String[] idsToOccupy = {"bar"};
		TicketListSuggestMock ticketListSuggestMock = new TicketListSuggestMock(idsToSuggest);
		ticketListSuggestMock.occupy(idsToOccupy);
		
		IHandshakeServer handshakeServer = new HandshakeServer(ticketListSuggestMock, ticket, null, 0);
		
		// Exercise SUT
		String ticketIdRecieved = handshakeServer.negotiateTicketId(idsToOccupy[0]);
		
		// Verify outcome
		assertEquals(idsToSuggest[0], ticketIdRecieved);
	}
	
	/**
	 * Set up a handshake server with a host and port that it should return,
	 * verify that the correct values are returned.
	 */
	@Test
	public void requestHostAndPortTest() {
		// Set up fixture
		String host = "localhost";
		int port = 9999;
		HandshakeServer handshakeServer = new HandshakeServer(null, null, host, port);
		
		// Exercise SUT
		String receivedHost = handshakeServer.requestHost();
		int receivedPort = handshakeServer.requestPort();
		
		// Verify outcome
		assertEquals(host, receivedHost);
		assertEquals(port, receivedPort);
	}

	@Test
	public void closeTest() {
		// Set up fixture
		TicketListMock ticketListMock = new TicketListMock();
		HandshakeServer handshakeServer = new HandshakeServer(ticketListMock, null, null, 0);
		
		// Exercise SUT
		handshakeServer.done();
		
		// Verify outcome
		assertTrue(ticketListMock.invocationLog.getInvocationStatus("add"));
	}
}
