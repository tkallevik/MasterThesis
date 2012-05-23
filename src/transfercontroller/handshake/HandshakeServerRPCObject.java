package transfercontroller.handshake;

import utilities.rpc.IRPCObject;
import utilities.rpc.RPCException;
import utilities.rpc.RPCServer;

/**
 * This class is the de-multiplexer for the RPC communication with HandshakeServer. 
 *
 */
public class HandshakeServerRPCObject implements IRPCObject {
	private HandshakeServer handshakeServer;
	
	/**
	 * Create a new TransferControllerFacadeRPCObject.
	 * 
	 * @param handshakeServer the handshakeServer to invoke methods on
	 */
	public HandshakeServerRPCObject(HandshakeServer handshakeServer) {
		this.handshakeServer = handshakeServer;
	}
	
	@Override
	public String invokeMethod(String methodName, String[] args) throws HandshakeException, RPCException {
		if(methodName.equals("negotiateTicketId")) {
			return handshakeServer.negotiateTicketId(args[0]);
		} else if(methodName.equals("confirmTicketId")) {
			handshakeServer.confirmTicketId(args[0]);
			return RPCServer.RETURN_VOID;
		} else if(methodName.equals("requestHost")) {
			return handshakeServer.requestHost();
		} else if(methodName.equals("requestPort")) {
			return "" + handshakeServer.requestPort();
		} else if(methodName.equals("done")) {
			handshakeServer.done();
			return RPCServer.RETURN_VOID;
		}
		
		throw new RPCException("Method not found: " + methodName);
	}

}
