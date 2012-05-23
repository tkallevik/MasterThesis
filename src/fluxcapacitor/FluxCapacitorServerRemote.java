package fluxcapacitor;

import java.io.IOException;

import archivecontroller.ObjectReference;
import archivecontroller.QueryException;
import transfercontroller.handshake.HandshakeException;
import utilities.ServiceUnavailableException;
import utilities.rpc.RPCClient;
import utilities.rpc.Serializer;

/**
 * This class is the multiplexer for the RPC communication with the FluxCapacitorServer.
 *
 */
public class FluxCapacitorServerRemote implements IFluxCapacitorServer {
	private RPCClient rpcClient;
	private ObjectReferenceSerializer objectReferenceSerializer;

	public FluxCapacitorServerRemote(RPCClient rpcClient, ObjectReferenceSerializer objectReferenceSerializer) {
		this.rpcClient = rpcClient;
		this.objectReferenceSerializer = objectReferenceSerializer;
	}

	@Override
	public ObjectReference[] getObjectReferences(String unitId) throws HandshakeException {
		String[] args = {unitId};
		
		String[] serializedObjectReferences = Serializer.deSerializeArray(invokeMethod("getObjectReferences", args));
		ObjectReference[] objectReferences = new ObjectReference[serializedObjectReferences.length];
		for (int i = 0; i < serializedObjectReferences.length; i++) {
			objectReferences[i] = objectReferenceSerializer.deSerialize(serializedObjectReferences[i]);
		}
		
		return objectReferences;
	}
	
	@Override
	public String requestTransferControllerHost() throws HandshakeException {
		String[] args = {};
		return invokeMethod("requestTransferControllerHost", args);
	}

	@Override
	public int requestTransferControllerPort() throws HandshakeException {
		String[] args = {};
		try {
			return Integer.parseInt(invokeMethod("requestTransferControllerPort", args));
		} catch (NumberFormatException e) {
			HandshakeException handshakeException = new HandshakeException("Port returned from handshake was not an integer.");
			handshakeException.initCause(e);
			throw handshakeException;
		}
	}

	@Override
	public void sendUnit(String unitId, String host, int port) throws IOException, ServiceUnavailableException, HandshakeException, QueryException {
		String[] args = {unitId, host, String.valueOf(port)};
		invokeMethod("sendUnit", args);
	}

	@Override
	public void sendObjects(ObjectReference[] objectReferences, String serverHost, int serverPort) 
			throws IOException, QueryException, HandshakeException, ServiceUnavailableException {
		String serializedObjectReferences = Serializer.serializeArray(objectReferenceSerializer.serialize(objectReferences));
		String[] args = {serializedObjectReferences, serverHost, String.valueOf(serverPort)};
		invokeMethod("sendObjects", args);
	}

	@Override
	public boolean hasObject(ObjectReference objectReference) throws HandshakeException {
		String[] args = {objectReferenceSerializer.serialize(objectReference)};
		return Boolean.valueOf(invokeMethod("hasObject", args)); 
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
