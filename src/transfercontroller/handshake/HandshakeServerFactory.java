package transfercontroller.handshake;

import java.io.IOException;
import java.net.Socket;

import transfercontroller.Ticket;
import transfercontroller.TicketList;
import utilities.LoopRunnable;
import utilities.rpc.RPCServer;

/**
 * This class creates HandshakeServers.
 */
public class HandshakeServerFactory {
	/**
	 * Create a new HandshakeServer associated with the connected socket through the RPCServer.
	 * 
	 * @param socket the socket resulting from the incoming connection 
	 * @param ticketList the TicketList that the HandshakeServer will use
	 * @param transferComponentHost the host to use for incoming transfers
	 * @param transferComponentPort the port to use for incoming transfers
	 * @return a LoopRunnable that will handle the server-side of this RPC session
	 * @throws IOException
	 */
	public LoopRunnable createHandshakeServer(Socket socket, TicketList ticketList, String transferComponentHost, int transferComponentPort) throws IOException {
		Ticket ticket = new Ticket();
		HandshakeServer handshakeServer = new HandshakeServer(ticketList, ticket, transferComponentHost, transferComponentPort);
		HandshakeServerRPCObject handshakeServerRPCObject = new HandshakeServerRPCObject(handshakeServer);
		RPCServer rpcServer = new RPCServer(socket.getInputStream(), socket.getOutputStream(), handshakeServerRPCObject);
		LoopRunnable loopRunnable = new LoopRunnable(rpcServer, null);
		
		return loopRunnable;
	}
}
