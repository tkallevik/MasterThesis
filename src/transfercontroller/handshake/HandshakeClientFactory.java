package transfercontroller.handshake;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import transfercontroller.Ticket;
import transfercontroller.TicketList;
import transfercontroller.TransferJob;
import utilities.rpc.RPCClient;

/**
 * This class creates HandshakeClients.
 */
public class HandshakeClientFactory {
	/**
	 * Create a new HandshakeClient with a fresh ticket and a corresponding TransferControllerFacadeRemote.
	 * 
	 * @param transferJob the TransferJob the HandshakeClient is supposed to negotiate
	 * @return a new HandshakeClient connected to a remote HandshakeServer, as specified in the transferJob
	 * @throws UnknownHostException
	 * @throws IOException
	 */
	public HandshakeClient createHandshakeClient(TransferJob transferJob, TicketList ticketList) throws UnknownHostException, IOException {
		Ticket ticket = new Ticket();
		ticket.setHost(transferJob.getHost());
		ticket.setPort(transferJob.getPort());
		ticket.setContainerIds(transferJob.getContainerIds());
		
		HandshakeServerRemote handshakeServerRemote = createHandshakeServerRemote(transferJob.getHost(), transferJob.getPort());
		
		HandshakeClient handshakeClient = new HandshakeClient(ticketList, handshakeServerRemote, ticket);
		
		return handshakeClient;
	}

	/**
	 * Connect to the specified host, and a set up an RPC connection with a remote HandshakeServer.
	 * 
	 * @param host the host to connect to
	 * @param port the port to connect to
	 * @return a TransferControllerFacadeRemote connected with the RPC connection
	 * @throws UnknownHostException
	 * @throws IOException
	 */
	private HandshakeServerRemote createHandshakeServerRemote(String host, int port) throws UnknownHostException, IOException {
		Socket socket = new Socket(host, port);
		
		RPCClient rpcClient = new RPCClient(socket.getInputStream(), socket.getOutputStream());
		HandshakeServerRemote handshakeServerRemote = new HandshakeServerRemote(rpcClient);
		
		return handshakeServerRemote;
	}
}
