package transfercontroller.tests.integration;

import static org.junit.Assert.*;

import org.junit.Test;

import transfercontroller.Ticket;
import transfercontroller.TicketList;
import transfercontroller.handshake.HandshakeClient;
import transfercontroller.handshake.HandshakeException;
import transfercontroller.handshake.HandshakeServer;
import transfercontroller.handshake.IHandshakeServer;

public class HandshakeTest {

	/**
	 * Set two handshakes up with each others and see if they generate equal tickets.
	 * @throws HandshakeException 
	 */
	@Test
	public void integrationTest() throws HandshakeException {
		// Set up fixture
		TicketList ticketListClient = new TicketList();
		TicketList ticketListServer = new TicketList();
		Ticket ticketServer = new Ticket();
		IHandshakeServer handshakeServer = new HandshakeServer(ticketListServer, ticketServer, null, 0);
		HandshakeClient handshakeClient = new HandshakeClient(ticketListClient, handshakeServer, new Ticket());
	
		// Exercise SUT
		Ticket ticketClient = handshakeClient.perform();
		
		// Verify outcome
		assertNotNull(ticketClient);
		assertEquals(ticketClient, ticketServer);
	}

}
