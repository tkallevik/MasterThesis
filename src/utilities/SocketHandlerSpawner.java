package utilities;

import java.io.IOException;
import java.net.Socket;


/**
 * This class will spawn socket handlers.
 *
 */
public class SocketHandlerSpawner implements ISocketHandlerSpawner {
	private ISocketHandlerFactory socketHandlerFactory;
	private String threadName;

	/**
	 * Create a new SocketHandlerSpawner.
	 * 
	 * @param socketHandlerFactory the factory to use for setting up the socket handler
	 */
	public SocketHandlerSpawner(ISocketHandlerFactory socketHandlerFactory) {
		this.socketHandlerFactory = socketHandlerFactory;
		this.threadName = "SocketHandler";
	}
	
	@Override
	public void spawn(Socket socket) throws IOException {
		LoopRunnable loopRunnable = socketHandlerFactory.createSocketHandler(socket);
		new Thread(loopRunnable, threadName).start();
	}
	
	@Override
	public void setThreadName(String threadName) {
		this.threadName = threadName;
	}

}
