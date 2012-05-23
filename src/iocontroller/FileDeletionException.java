package iocontroller;

/**
 * Exception to indicate that the file could not be deleted.
 */
public class FileDeletionException extends Exception {
	private static final long serialVersionUID = 1L;
	
	public FileDeletionException(String path) {
		super("The file at path: " + path + " could not be deleted.");
	}
}