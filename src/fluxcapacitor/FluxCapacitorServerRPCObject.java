package fluxcapacitor;

import java.io.IOException;

import archivecontroller.ObjectReference;
import archivecontroller.QueryException;
import transfercontroller.handshake.HandshakeException;
import utilities.ServiceUnavailableException;
import utilities.rpc.IRPCObject;
import utilities.rpc.RPCException;
import utilities.rpc.RPCServer;
import utilities.rpc.Serializer;

/**
 * This class is the de-multiplexer for the RPC communication with FluxCapacitorServer. 
 *
 */
public class FluxCapacitorServerRPCObject implements IRPCObject {
	private IFluxCapacitorServer fluxCapacitorServer;
	private ObjectReferenceSerializer objectReferenceSerializer;
	
	public FluxCapacitorServerRPCObject(IFluxCapacitorServer fluxCapacitorServer, ObjectReferenceSerializer objectReferenceSerializer) {
		this.fluxCapacitorServer = fluxCapacitorServer;
		this.objectReferenceSerializer = objectReferenceSerializer;
	}
	
	@Override
	public String invokeMethod(String methodName, String[] args) throws HandshakeException, RPCException, QueryException, IOException, ServiceUnavailableException {
		if(methodName.equals("getObjectReferences")) {
			ObjectReference[] objectReferences = fluxCapacitorServer.getObjectReferences(args[0]);
			
			if (objectReferences == null) {
				return RPCServer.RETURN_NULL;
			}
			
			String[] serializedObjectReferences = new String[objectReferences.length];
			for (int i = 0; i < objectReferences.length; i++) {
				serializedObjectReferences[i] = objectReferenceSerializer.serialize(objectReferences[i]);
			}
			
			return Serializer.serializeArray(serializedObjectReferences);
		} else if(methodName.equals("requestTransferControllerHost")) {
			return fluxCapacitorServer.requestTransferControllerHost();
		} else if(methodName.equals("requestTransferControllerPort")) {
			return "" + fluxCapacitorServer.requestTransferControllerPort();
		} else if(methodName.equals("sendUnit")) {
			try {
				fluxCapacitorServer.sendUnit(args[0], args[1], Integer.parseInt(args[2]));
			} catch (NumberFormatException e) {
				HandshakeException handshakeException = new HandshakeException("Port argument for sendUnit was not an integer.");
				handshakeException.initCause(e);
				throw handshakeException;
			}
			
			return RPCServer.RETURN_VOID;
		} else if(methodName.equals("hasObject")) {
			ObjectReference objectReference = objectReferenceSerializer.deSerialize(args[0]);
			
			if (fluxCapacitorServer.hasObject(objectReference)) {
				return "TRUE";
			}
			
			return "FALSE";
		} else if(methodName.equals("sendObjects")) {
			try {
				ObjectReference[] objectReferences = objectReferenceSerializer.deSerialize(Serializer.deSerializeArray(args[0]));
				fluxCapacitorServer.sendObjects(objectReferences, args[1], Integer.parseInt(args[2]));
			} catch (NumberFormatException e) {
				HandshakeException handshakeException = new HandshakeException("Port argument for sendObjects was not an integer.");
				handshakeException.initCause(e);
				throw handshakeException;
			}
			
			return RPCServer.RETURN_VOID;
		}
		
		throw new RPCException("Method not found: " + methodName);
	}

}
