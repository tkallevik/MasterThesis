package fluxcapacitor;

import java.io.IOException;

import loadingdock.ILoadingDockFacade;
import transfercontroller.ITransferControllerFacade;
import utilities.LoopRunnable;
import utilities.rpc.RPCListenerFactory;
import archivecontroller.IArchiveControllerFacade;
import archivecontroller.IArchiveQuery;

public class FluxCapacitorFacadeFactory {
	public FluxCapacitorFacade createFacade(IArchiveControllerFacade archiveControllerFacade, IArchiveQuery archiveQuery,
			ITransferControllerFacade transferControllerFacade, ILoadingDockFacade loadingDockFacade, String listenHost, int listenPort) throws IOException {
		
		FluxCapacitorServerRemoteFactory fluxCapacitorServerRemoteFactory = new FluxCapacitorServerRemoteFactory();
		
		FluxCapacitorFacade fluxCapacitorFacade = new FluxCapacitorFacade(archiveControllerFacade, archiveQuery, transferControllerFacade, loadingDockFacade, fluxCapacitorServerRemoteFactory, listenHost, listenPort);
		
		ObjectReferenceSerializer objectReferenceSerializer = new ObjectReferenceSerializer();
		FluxCapacitorServerRPCObject fluxCapacitorServerRPCObject = new FluxCapacitorServerRPCObject(fluxCapacitorFacade, objectReferenceSerializer);
		RPCListenerFactory rpcListenerFactory = new RPCListenerFactory();
		LoopRunnable loopRunnable = rpcListenerFactory.createRPCListener(listenPort, fluxCapacitorServerRPCObject);
		new Thread(loopRunnable, "FluxCapacitorListener").start();
		
		return fluxCapacitorFacade;
	}
}
