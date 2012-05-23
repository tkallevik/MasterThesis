package utilities;

/**
 * Exception to indicate that an agent or service could not be reached.
 */
public class ServiceUnavailableException extends Exception {
	private static final long serialVersionUID = 1L;
	
	public ServiceUnavailableException(String message) {
		super(message);
	}
}
