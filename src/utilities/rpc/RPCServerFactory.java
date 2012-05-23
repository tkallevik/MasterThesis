package utilities.rpc;

import java.io.IOException;
import java.net.Socket;

import utilities.ISocketHandlerFactory;
import utilities.LoopRunnable;

/**
 * This class is used for setting up the RPC Server subsystem.
 *
 */
public class RPCServerFactory implements ISocketHandlerFactory {
	private IRPCObject rpcObject;
	
	/**
	 * Create a new RPCServerFactory.
	 * 
	 * @param rpcObject the RPCObject to use for de-multiplexing RPC calls
	 */
	public RPCServerFactory(IRPCObject rpcObject) {
		this.rpcObject = rpcObject;
	}
	
	/**
	 * Create a new RPCServer.
	 * 
	 * @param socket the socket resulting from the incoming connection
	 * @return a LoopRunnable that will handle the server-side of this RPC session
	 * @throws IOException
	 */
	@Override
	public LoopRunnable createSocketHandler(Socket socket) throws IOException {
		RPCServer rpcServer = new RPCServer(socket.getInputStream(), socket.getOutputStream(), rpcObject);
		LoopRunnable loopRunnable = new LoopRunnable(rpcServer, null);
		
		return loopRunnable;
	}
}
