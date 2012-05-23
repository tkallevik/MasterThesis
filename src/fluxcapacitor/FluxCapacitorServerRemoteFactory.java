package fluxcapacitor;

import java.io.IOException;

import utilities.rpc.RPCClient;
import utilities.rpc.RPCClientFactory;

public class FluxCapacitorServerRemoteFactory {
	public IFluxCapacitorServer createRemoteServer(String host, int port) throws IOException {
		ObjectReferenceSerializer objectReferenceSerializer = new ObjectReferenceSerializer();
		RPCClientFactory rpcClientFactory = new RPCClientFactory(host, port);
		RPCClient rpcClient = rpcClientFactory.createRPCClient();
		FluxCapacitorServerRemote fluxCapacitorServerRemote = new FluxCapacitorServerRemote(rpcClient, objectReferenceSerializer);
		
		return fluxCapacitorServerRemote;
	}
}
