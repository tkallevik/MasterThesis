package transfercomponent;

import java.io.IOException;
import java.net.ServerSocket;

import utilities.ISocketHandlerSpawner;
import utilities.Listener;
import utilities.LoopRunnable;
import utilities.SocketHandlerSpawner;

/**
 * The responsibility of this class is to construct a listener.
 *
 */
public class ListenerFactory {
	private TransferComponentFactory transferComponentFactory;
	
	/**
	 * Creates a new ListenerFactory.
	 *  
	 * @param transferComponentFactory the transport component factory that is used for creating a TransferComponentSpawner
	 */
	public ListenerFactory(TransferComponentFactory transferComponentFactory) {
		this.transferComponentFactory = transferComponentFactory;
	}
	
	/**
	 * Create a listener with a specified port and path.
	 * 
	 * @param port the port to listen on
	 * @return a LoopRunnable encapsulating the constructed listener
	 * @throws IOException
	 */
	public LoopRunnable createListener(int port) throws IOException {
		ServerSocket serverSocket = new ServerSocket(port);
		ISocketHandlerSpawner socketHandlerSpawner = new SocketHandlerSpawner(transferComponentFactory);
		socketHandlerSpawner.setThreadName("TransferComponentListener");
		Listener listener = new Listener(serverSocket, socketHandlerSpawner);
		
		return new LoopRunnable(listener, null);
	}
}
