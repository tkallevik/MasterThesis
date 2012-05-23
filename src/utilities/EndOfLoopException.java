package utilities;

/**
 * Exception to indicate that the loop should terminate.
 */
public class EndOfLoopException extends Exception {
	private static final long serialVersionUID = 1L;
	
	public EndOfLoopException(String message) {
		super(message);
	}
}
