package utilities.rpc;

import java.io.IOException;
import java.net.Socket;


/**
 * This class is used for setting up the RPC Client subsystem.
 *
 */
public class RPCClientFactory {
	private String host;
	private int port;
	
	/**
	 * Create a new RPCClientFactory.
	 * 
	 * @param host the host of the corresponding RPCServer 
	 * @param port the port to use for connecting to the RPCServer
	 */
	public RPCClientFactory(String host, int port) {
		this.host = host;
		this.port = port;
	}
	
	/**
	 * Create a new RPCClient.
	 * 
	 * @return an RPCClient connected to an RPCServer
	 * @throws IOException
	 */
	public RPCClient createRPCClient() throws IOException {
		Socket socket = new Socket(host, port);
		RPCClient rpcClient = new RPCClient(socket.getInputStream(), socket.getOutputStream());
		
		return rpcClient;
	}
}
