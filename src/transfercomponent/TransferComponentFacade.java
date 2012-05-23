package transfercomponent;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.HashMap;

import transfercontroller.Ticket;
import utilities.ServiceUnavailableException;

/**
 * The responsibility of this class is to act as a facade for the transfer component.
 *
 */
public class TransferComponentFacade implements ITransferComponentFacade {
	private TransferComponentFactory transferComponentFactory;
	private ListenerFactory listenerFactory;
	private HashMap<String, StreamCopier> streamCopierRegister;
	
	/**
	 * Creates a new TransferComponentFacade.
	 * 
	 * @param transferComponentFactory the factory that will be used to construct the necessary sender subsystem
	 * @param listenerFactory the factory that will be used to construct the necessary receiver subsystem
	 * @param streamCopierRegister the list where all StreamCopiers a registered
	 */
	public TransferComponentFacade(TransferComponentFactory transferComponentFactory, ListenerFactory listenerFactory, HashMap<String, StreamCopier> streamCopierRegister) {
		this.transferComponentFactory = transferComponentFactory;
		this.listenerFactory = listenerFactory;
		this.streamCopierRegister = streamCopierRegister;
	}

	/**
	 * Open a socket to listen for connections and accept files.
	 * 
	 * @param port to listen on
	 * @throws IOException
	 */
	public void listenForFiles(int port) throws IOException {
		Runnable listenSystem = listenerFactory.createListener(port);
		new Thread(listenSystem, "ListenSystem").start();
	}

	@Override
	public void stop(String ticketId) throws IOException {
		StreamCopier streamCopier = streamCopierRegister.get(ticketId);
		streamCopier.terminateAction();
	}

	@Override
	public void pushFile(Ticket ticket) throws UnknownHostException, IOException, ServiceUnavailableException {		
		Thread[] threads = new Thread[ticket.getContainerIds().length];
		
		// Start all transfers
		int i = 0;
		for (String containerId : ticket.getContainerIds()) {
			Runnable transferSystem = transferComponentFactory.createPushSender(ticket.getHost(), ticket.getPort(), containerId, ticket.getId());
			threads[i] = new Thread(transferSystem, "TransferSystem");
			threads[i].start();
			i++;
		}
		
		// Wait for all transfers to complete
		for (Thread thread : threads) {
			try {
				thread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public boolean hasActiveTicket(String ticketId) {
		return streamCopierRegister.containsKey(ticketId);
	}
}
