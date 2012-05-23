package utilities.rpc;

import java.io.IOException;
import java.net.ServerSocket;

import utilities.Listener;
import utilities.LoopRunnable;
import utilities.SocketHandlerSpawner;

public class RPCListenerFactory {
	public LoopRunnable createRPCListener(int listenPort, IRPCObject rpcObject) throws IOException {
			ServerSocket serverSocket = new ServerSocket(listenPort);
			RPCServerFactory rpcServerFactory = new RPCServerFactory(rpcObject);
			SocketHandlerSpawner rpcServerSpawner = new SocketHandlerSpawner(rpcServerFactory);
			Listener listener = new Listener(serverSocket, rpcServerSpawner);
			LoopRunnable loopRunnable = new LoopRunnable(listener, null);
			
			return loopRunnable;
	}
}
