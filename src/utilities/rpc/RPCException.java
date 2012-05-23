package utilities.rpc;

/**
 * Exception to indicate errors in the RPC system.
 */
public class RPCException extends Exception {
	private static final long serialVersionUID = 1L;
	
	public RPCException(String message) {
		super(message);
	}
}
