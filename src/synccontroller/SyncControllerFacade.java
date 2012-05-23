package synccontroller;

import java.io.IOException;
import java.util.ArrayList;

import fluxcapacitor.IFluxCapacitorFacade;

import archivecontroller.IArchiveQuery;
import archivecontroller.ObjectReference;
import archivecontroller.QueryException;
import transfercontroller.handshake.HandshakeException;
import unitcontroller.Branch;
import unitcontroller.IUnitControllerFacade;
import utilities.ServiceUnavailableException;

public class SyncControllerFacade implements ISyncControllerFacade, ISyncControllerServer {
	private SyncControllerServerRemoteFactory syncControllerServerRemoteFactory;
	private IArchiveQuery archiveQuery;
	private IFluxCapacitorFacade fluxCapacitorFacade;
	
	public SyncControllerFacade(SyncControllerServerRemoteFactory syncControllerServerRemoteFactory, IArchiveQuery archiveQuery, IFluxCapacitorFacade fluxCapacitorFacade) {
		this.syncControllerServerRemoteFactory = syncControllerServerRemoteFactory;
		this.archiveQuery = archiveQuery;
		this.fluxCapacitorFacade = fluxCapacitorFacade;
	}
	
	@Override
	public void sync(String host, int port) throws IOException, ServiceUnavailableException, HandshakeException, QueryException {
		ISyncControllerServer syncControllerServer = syncControllerServerRemoteFactory.createRemoteServer(host, port);
		Branch[] remoteBranches = syncControllerServer.getBranches();
		
		Branch[] localBranches = archiveQuery.getAllBranches();
		
		Branch[] branchesToPush = getOneWayDiff(localBranches, remoteBranches);
		Branch[] branchesToPull = getOneWayDiff(remoteBranches, localBranches);
		
		String[] unitIdsToPush = archiveQuery.getUnitIds(branchesToPush);
		String[] unitIdsToPull = syncControllerServer.getUnitIds(branchesToPull);
		
		String fluxCapacitorFacadeHost = syncControllerServer.requestFluxCapacitorFacadeHost();
		int fluxCapacitorFacadePort = syncControllerServer.requestFluxCapacitorFacadePort();
		for (String unitId : unitIdsToPush) {
			fluxCapacitorFacade.sendUnit(unitId, fluxCapacitorFacadeHost, fluxCapacitorFacadePort);
		}
		
		for (String unitId : unitIdsToPull) {
			fluxCapacitorFacade.pullUnit(unitId, fluxCapacitorFacadeHost, fluxCapacitorFacadePort);
		}
		
		// Push the actual branches
		for (Branch branch : branchesToPush) {
			ObjectReference[] objectReferences = {new ObjectReference(archiveQuery.getComponentId(IUnitControllerFacade.class), branch.getId())};
			fluxCapacitorFacade.sendObjects(objectReferences, fluxCapacitorFacadeHost, fluxCapacitorFacadePort);
		}
		
		// Pull the actual branches
		for (Branch branch : branchesToPull) {
			ObjectReference[] objectReferences = {new ObjectReference(archiveQuery.getComponentId(IUnitControllerFacade.class), branch.getId())};
			fluxCapacitorFacade.pullObjects(objectReferences, fluxCapacitorFacadeHost, fluxCapacitorFacadePort);
		}
	}
	
	private Branch[] getOneWayDiff(Branch[] aList, Branch[] bList) {
		ArrayList<Branch> branchIdsFromANotFoundInB = new ArrayList<Branch>();
		
		for (Branch a : aList) {
			boolean found = false;
			for (Branch b : bList) {
				if (a.equals(b)) {
					found = true;
				}
			}
				
			if (!found) {
				branchIdsFromANotFoundInB.add(a);
			}
		}
		
		Branch[] branchArray = new Branch[branchIdsFromANotFoundInB.size()];
		
		return branchIdsFromANotFoundInB.toArray(branchArray);
	}

	@Override
	public Branch[] getBranches() {
		return archiveQuery.getAllBranches();
	}
	
	@Override
	public String requestFluxCapacitorFacadeHost() throws HandshakeException {
		return fluxCapacitorFacade.getHost();
	}

	@Override
	public int requestFluxCapacitorFacadePort() throws HandshakeException {
		return fluxCapacitorFacade.getPort();
	}

	@Override
	public String[] getUnitIds(Branch[] branches) throws QueryException {
		return archiveQuery.getUnitIds(branches);
	}
}
