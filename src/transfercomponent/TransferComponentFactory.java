package transfercomponent;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;

import loadingdock.ILoadingDockFacade;

import utilities.LoadingDockInputStream;
import utilities.LoadingDockOutputStream;
import utilities.ISocketHandlerFactory;
import utilities.LoopRunnable;

/**
 * The responsibility of this class is building the transfer component subsystem.
 *
 */
public class TransferComponentFactory implements ISocketHandlerFactory {
	private HashMap<String, StreamCopier> streamCopierRegister;
	private ILoadingDockFacade loadingDockFacade;
	
	/**
	 * Creates a new TransferComponentFactory.
	 * 
	 * @param streamCopierRegister the list to register all StreamCopiers with
	 */
	public TransferComponentFactory(HashMap<String, StreamCopier> streamCopierRegister, ILoadingDockFacade loadingDockFacade) {
		this.streamCopierRegister = streamCopierRegister;
		this.loadingDockFacade = loadingDockFacade;
	}
	
	/**
	 * Build the connecting side of the transfer component subsystem for pushing file.
	 * 
	 * @param host the host the subsystem will connect to
	 * @param port the port to connect to
	 * @param ticketId the id of the ticket associated with this transfer
	 * @return the runnable core of the transfer component
	 * @throws UnknownHostException
	 * @throws IOException
	 */
	public LoopRunnable createPushSender(String host, int port, String containerId, String ticketId) throws UnknownHostException, IOException {
		InputStream inputStream = new LoadingDockInputStream(loadingDockFacade, containerId);
		Socket socket = new Socket(host, port);
		OutputStream outputStream = socket.getOutputStream();
		
		// Send the containerId to the other side, before sending the actual data
		DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
		dataOutputStream.writeUTF(containerId);
		
		return createRunnable(inputStream, outputStream, ticketId);
	}
	
	/**
	 * Build the listening side of the transfer component subsystem for pushing file.
	 * 
	 * @param socket the socket to receive data on
	 * @return the runnable core of the transfer component
	 * @throws UnknownHostException
	 * @throws IOException
	 */
	public LoopRunnable createPushReceiver(Socket socket) throws IOException {		
		InputStream inputStream = socket.getInputStream();
		
		// Receive the containerId, before receiving the data
		DataInputStream dataInputStream = new DataInputStream(inputStream);
		String containerId = dataInputStream.readUTF();
		
		OutputStream outputStream = new LoadingDockOutputStream(loadingDockFacade, containerId);

		return createRunnable(inputStream, outputStream, null);
	}
	
	/**
	 * Constructs the StreamCopier and the runnable.
	 * 
	 * @param inputStream the InputStream to give to the StreamCopier
	 * @param outputStream the OutputStream to give to the StreamCopier
	 * @param ticketId the id of the ticket associated with this transfer
	 * @return a LoopRunnable encapsulating a StreamCopier with the given parameters
	 */
	private LoopRunnable createRunnable(InputStream inputStream, OutputStream outputStream, String ticketId) {
		StreamCopier streamCopier = new StreamCopier(inputStream, outputStream);
		streamCopierRegister.put(ticketId, streamCopier);
		
		LoopRunnable loopRunnable = new LoopRunnable(streamCopier, null);
		
		return loopRunnable;
	}

	@Override
	public LoopRunnable createSocketHandler(Socket socket) throws IOException {
		return createPushReceiver(socket);
	}
}
