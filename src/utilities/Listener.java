package utilities;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * The responsibility of this class is to listen for connections on a server socket.
 *
 */
public class Listener implements ILoopObject {
	ServerSocket serverSocket;
	ISocketHandlerSpawner socketHandlerSpawner;
	String path;
	
	/**
	 * Creates a new Listener.
	 * 
	 * @param serverSocket the server socket to accept connections from
	 * @param socketHandlerSpawner the object to hand incoming sockets to
	 */
	public Listener(ServerSocket serverSocket, ISocketHandlerSpawner socketHandlerSpawner) {
		this.serverSocket = serverSocket;
		this.socketHandlerSpawner = socketHandlerSpawner;
	}

	/**
	 * Calls the listen() method.
	 * 
	 * @throws Exception
	 */
	@Override
	public void loopAction() throws Exception {
		listen();
	}
	
	/**
	 * Closes the ServerSocket.
	 * 
	 * @throws IOException
	 */
	@Override
	public void terminateAction() throws IOException {
		serverSocket.close();
	}
	
	/**
	 * Listen for connections on the serverSocket.
	 * Resulting sockets are handed over to the socketHandlerSpawner.
	 * 
	 * @throws IOException
	 */
	public void listen() throws IOException {
		Socket socket = serverSocket.accept();
		socketHandlerSpawner.spawn(socket);
	}
}
