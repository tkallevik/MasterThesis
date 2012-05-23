package transfercontroller;

import utilities.NothingMoreToActOnException;

/**
 * Exception to indicate that there are no more transferJobs to process.
 */
public class NoMoreJobsToProcessException extends NothingMoreToActOnException {
	private static final long serialVersionUID = 1L;
	
	public NoMoreJobsToProcessException(String message) {
		super(message);
	}
}
