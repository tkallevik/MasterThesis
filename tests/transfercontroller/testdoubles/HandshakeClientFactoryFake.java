package transfercontroller.testdoubles;

import transfercontroller.TicketList;
import transfercontroller.TransferJob;
import transfercontroller.handshake.HandshakeClient;
import transfercontroller.handshake.HandshakeClientFactory;

/**
 * This class returns the HandshakeClientMock given in the constructor.
 */
public class HandshakeClientFactoryFake extends HandshakeClientFactory {
	HandshakeClientMock handshakeClientMock;
	
	public HandshakeClientFactoryFake(HandshakeClientMock handshakeClientMock) {
		this.handshakeClientMock = handshakeClientMock;
	}

	@Override
	public HandshakeClient createHandshakeClient(TransferJob transferJob, TicketList ticketList) {		
		return handshakeClientMock;
	}
}
