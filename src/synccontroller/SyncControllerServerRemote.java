package synccontroller;

import archivecontroller.QueryException;
import transfercontroller.handshake.HandshakeException;
import unitcontroller.Branch;
import utilities.rpc.RPCClient;
import utilities.rpc.Serializer;

public class SyncControllerServerRemote implements ISyncControllerServer {
	private RPCClient rpcClient;
	private BranchSerializer branchSerializer;

	public SyncControllerServerRemote(RPCClient rpcClient, BranchSerializer branchSerializer) {
		this.rpcClient = rpcClient;
		this.branchSerializer = branchSerializer;
	}

	@Override
	public Branch[] getBranches() throws HandshakeException {
		String[] args = {};
		
		String[] serializedBranches = Serializer.deSerializeArray(invokeMethod("getBranches", args));
		Branch[] branches = new Branch[serializedBranches.length];
		for (int i = 0; i < serializedBranches.length; i++) {
			branches[i] = branchSerializer.deSerialize(serializedBranches[i]);
		}
		
		return branches;
	}
	
	@Override
	public String requestFluxCapacitorFacadeHost() throws HandshakeException {
		String[] args = {};
		return invokeMethod("requestFluxCapacitorFacadeHost", args);
	}

	@Override
	public int requestFluxCapacitorFacadePort() throws HandshakeException {
		String[] args = {};
		try {
			return Integer.parseInt(invokeMethod("requestFluxCapacitorFacadePort", args));
		} catch (NumberFormatException e) {
			HandshakeException handshakeException = new HandshakeException("Port returned from handshake was not an integer.");
			handshakeException.initCause(e);
			throw handshakeException;
		}
	}

	@Override
	public String[] getUnitIds(Branch[] branches) throws QueryException, HandshakeException {
		String[] serializedBranches = new String[branches.length];
		for (int i = 0; i < branches.length; i++) {
			serializedBranches[i] = branchSerializer.serialize(branches[i]);
		}
		
		String[] args = {Serializer.serializeArray(serializedBranches)};
		
		return Serializer.deSerializeArray(invokeMethod("getUnitIds", args));
	}
	
	/**
	 * Wrap rpcClient's invokeMethod() and handle its exceptions by throwing a HandshakeException.
	 * 
	 * @param methodName the methodName to invoke
	 * @param args arguments for the method to invoke
	 * @return a String representing the return value of the method
	 * @throws HandshakeException if an Exception is caught from the RPCClient's invokeMethod()
	 */
	private String invokeMethod(String methodName, String[] args) throws HandshakeException {
		try {
			return rpcClient.invokeMethod(methodName, args);
		} catch (Exception e) {
			HandshakeException handshakeException = new HandshakeException("The invocation on the remote flux capacitor server failed.");
			handshakeException.initCause(e);
			throw handshakeException;
		}
	}
}
