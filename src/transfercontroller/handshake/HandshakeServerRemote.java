package transfercontroller.handshake;

import utilities.rpc.RPCClient;

/**
 * This class is the multiplexer for the RPC communication with the HandshakeServer.
 *
 */
public class HandshakeServerRemote implements IHandshakeServer {
	private RPCClient rpcClient;

	/**
	 * Create a new TransferControllerFacadeRemote.
	 * 
	 * @param rpcClient the RPCClient to use for remote communication
	 */
	public HandshakeServerRemote(RPCClient rpcClient) {
		this.rpcClient = rpcClient;
	}

	@Override
	public String negotiateTicketId(String ticketIdSuggestion) throws HandshakeException {
		String[] args = {ticketIdSuggestion};
		return invokeMethod("negotiateTicketId", args);
	}

	@Override
	public void confirmTicketId(String ticketId) throws HandshakeException {
		String[] args = {ticketId};
		invokeMethod("confirmTicketId", args);
	}

	@Override
	public String requestHost() throws HandshakeException {
		String[] args = {};
		return invokeMethod("requestHost", args);
	}

	@Override
	public int requestPort() throws HandshakeException {
		String[] args = {};
		return Integer.parseInt(invokeMethod("requestPort", args));
	}
	
	@Override
	public void done() throws HandshakeException {
		String[] args = {};
		invokeMethod("done", args);
		rpcClient.close();
	}
	
	/**
	 * Wrap rpcClient's invokeMethod() and handle its exceptions by throwing a HandshakeException.
	 * 
	 * @param methodName the methodName to invoke
	 * @param args arguments for the method to invoke
	 * @return a String representing the return value of the method
	 * @throws HandshakeException if an Exception is caught from the RPCClient's invokeMethod()
	 */
	private String invokeMethod(String methodName, String[] args) throws HandshakeException {
		try {
			return rpcClient.invokeMethod(methodName, args);
		} catch (Exception e) {
			HandshakeException handshakeException = new HandshakeException("The invocation on the remote handshake server failed.");
			handshakeException.initCause(e);
			throw handshakeException;
		}
	}
}
