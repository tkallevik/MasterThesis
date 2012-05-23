package synccontroller;

import java.io.IOException;

import utilities.rpc.RPCClient;
import utilities.rpc.RPCClientFactory;

public class SyncControllerServerRemoteFactory {
	public ISyncControllerServer createRemoteServer(String host, int port) throws IOException {
		BranchSerializer branchSerializer = new BranchSerializer();
		RPCClientFactory rpcClientFactory = new RPCClientFactory(host, port);
		RPCClient rpcClient = rpcClientFactory.createRPCClient();
		SyncControllerServerRemote syncControllerServerRemote = new SyncControllerServerRemote(rpcClient, branchSerializer);
		
		return syncControllerServerRemote;
	}
}
