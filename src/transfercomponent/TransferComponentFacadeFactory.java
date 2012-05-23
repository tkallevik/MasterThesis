package transfercomponent;

import java.util.HashMap;

import loadingdock.ILoadingDockFacade;

/**
 * The responsibility of this class is to create the TransferComponentFacade.
 *
 */
public class TransferComponentFacadeFactory {
	
	/**
	 * Create a new TransferComponentFacade.
	 * 
	 * @param objectArchiveFacade the ObjectArchive to associate with
	 * @return the created TransferComponentFacade
	 */
	public TransferComponentFacade createFacade(ILoadingDockFacade loadingDockFacade) {
		HashMap<String, StreamCopier> streamCopierRegister = new HashMap<String, StreamCopier>();
		TransferComponentFactory transferComponentFactory = new TransferComponentFactory(streamCopierRegister, loadingDockFacade);
		ListenerFactory listenerFactory = new ListenerFactory(transferComponentFactory);
		TransferComponentFacade transferComponentFacade = new TransferComponentFacade(transferComponentFactory, listenerFactory, streamCopierRegister);
		return transferComponentFacade;
	}
}
