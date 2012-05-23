package transfercontroller.testdoubles;

import testutilities.InvocationLog;
import transfercontroller.Ticket;
import transfercontroller.TicketList;
import transfercontroller.handshake.HandshakeClient;
import transfercontroller.handshake.HandshakeException;
import transfercontroller.handshake.IHandshakeServer;

/**
 * The responsibility of this class is to mock the HandshakeClientMock, and simply log the method invocations.
 */
public class HandshakeClientMock extends HandshakeClient {
	private boolean failOnPerform = false;
	public InvocationLog invocationLog;
	
	public HandshakeClientMock(TicketList ticketList, IHandshakeServer handshakeServer, Ticket ticket) {
		super(ticketList, handshakeServer, ticket);
		invocationLog = new InvocationLog();
	}
	
	@Override
	public Ticket perform() throws HandshakeException {
		invocationLog.addInvocation("perform");
		
		if (failOnPerform) {
			throw new HandshakeException("Handshake failed on instruction from test.");
		}
			
		return new Ticket("foobar");
	}

	/**
	 * This method will make perform() throw an exception instead of returning a ticket.
	 */
	public void setFailOnPerform() {
		failOnPerform = true;
	}
}
