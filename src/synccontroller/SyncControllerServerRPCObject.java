package synccontroller;

import java.io.IOException;

import archivecontroller.QueryException;
import transfercontroller.handshake.HandshakeException;
import unitcontroller.Branch;
import utilities.ServiceUnavailableException;
import utilities.rpc.IRPCObject;
import utilities.rpc.RPCException;
import utilities.rpc.RPCServer;
import utilities.rpc.Serializer;

public class SyncControllerServerRPCObject implements IRPCObject {
	private ISyncControllerServer syncControllerServer;
	private BranchSerializer branchSerializer;
	
	public SyncControllerServerRPCObject(ISyncControllerServer syncControllerServer, BranchSerializer branchSerializer) {
		this.syncControllerServer = syncControllerServer;
		this.branchSerializer = branchSerializer;
	}
	
	@Override
	public String invokeMethod(String methodName, String[] args) throws HandshakeException, RPCException, QueryException, IOException, ServiceUnavailableException {
		if(methodName.equals("getBranches")) {
			Branch[] branches = syncControllerServer.getBranches();
			
			if (branches == null) {
				return RPCServer.RETURN_NULL;
			}
			
			String[] serializedBranches = new String[branches.length];
			for (int i = 0; i < branches.length; i++) {
				serializedBranches[i] = branchSerializer.serialize(branches[i]);
			}
			
			return Serializer.serializeArray(serializedBranches);
		} else if(methodName.equals("requestFluxCapacitorFacadeHost")) {
			return syncControllerServer.requestFluxCapacitorFacadeHost();
		} else if(methodName.equals("requestFluxCapacitorFacadePort")) {
			return "" + syncControllerServer.requestFluxCapacitorFacadePort();
		} else if(methodName.equals("getUnitIds")) {
			String[] serializedBranches = Serializer.deSerializeArray(args[0]);
			Branch[] branches = new Branch[serializedBranches.length];
			for (int i = 0; i < branches.length; i++) {
				branches[i] = branchSerializer.deSerialize(serializedBranches[i]);
			}
			
			String[] unitIds = syncControllerServer.getUnitIds(branches);
			
			return Serializer.serializeArray(unitIds);
		}
		
		throw new RPCException("Method not found: " + methodName);
	}
}
