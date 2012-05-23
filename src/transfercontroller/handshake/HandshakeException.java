package transfercontroller.handshake;

/**
 * Exception to indicate errors in the handshaking procedure.
 */
public class HandshakeException extends Exception {
	private static final long serialVersionUID = 1L;
	
	public HandshakeException(String message) {
		super(message);
	}
}