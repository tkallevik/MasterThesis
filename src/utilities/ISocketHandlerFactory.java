package utilities;

import java.io.IOException;
import java.net.Socket;

public interface ISocketHandlerFactory {
	/**
	 * Create a LoopRunnable with a subsystem for handling a socket.
	 * 
	 * @param socket the socket to handle
	 * @return the LoopRunnable with the subsystem
	 * @throws IOException 
	 */
	public LoopRunnable createSocketHandler(Socket socket) throws IOException;
}
