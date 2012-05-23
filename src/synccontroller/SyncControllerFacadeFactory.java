package synccontroller;

import java.io.IOException;

import utilities.LoopRunnable;
import utilities.rpc.RPCListenerFactory;
import archivecontroller.IArchiveQuery;
import fluxcapacitor.IFluxCapacitorFacade;

public class SyncControllerFacadeFactory {
	public SyncControllerFacade createFacade(IArchiveQuery archiveQuery, IFluxCapacitorFacade fluxCapacitorFacade, int listenPort) throws IOException {
		SyncControllerServerRemoteFactory syncControllerServerRemoteFactory = new SyncControllerServerRemoteFactory();
		
		SyncControllerFacade syncControllerFacade = new SyncControllerFacade(syncControllerServerRemoteFactory, archiveQuery, fluxCapacitorFacade);
		
		BranchSerializer branchSerializer = new BranchSerializer();
		SyncControllerServerRPCObject syncControllerServerRPCObject = new SyncControllerServerRPCObject(syncControllerFacade, branchSerializer);
		RPCListenerFactory rpcListenerFactory = new RPCListenerFactory();
		LoopRunnable loopRunnable = rpcListenerFactory.createRPCListener(listenPort, syncControllerServerRPCObject);
		new Thread(loopRunnable, "FluxCapacitorListener").start();
		
		return syncControllerFacade;
	}
}
