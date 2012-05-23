package utilities;

/**
 * Exception to indicate that there are nothing more to do.
 */
public class NothingMoreToActOnException extends Exception {
	private static final long serialVersionUID = 1L;
	
	public NothingMoreToActOnException(String message) {
		super(message);
	}
}
