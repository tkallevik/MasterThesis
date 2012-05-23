package utilities;

import java.io.IOException;
import java.net.Socket;

public interface ISocketHandlerSpawner {

	/**
	 * Spawn a subsystem with a given socket.
	 * 
	 * @param socket the socket to spawn a subsystem for
	 * @throws IOException
	 */
	public abstract void spawn(Socket socket) throws IOException;
	
	/**
	 * Set the name for the threads that are spawned.
	 * 
	 * @param threadName the thread name
	 */
	public void setThreadName(String threadName);

}