package transfercomponent;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import utilities.EndOfLoopException;
import utilities.ILoopObject;
import utilities.ServiceUnavailableException;

/**
 * The responsibility of this class is to simply read data from a provided
 * InputStream and write it to a provided OutputStream. 
 * 
 */
public class StreamCopier implements ILoopObject {
	private InputStream inputStream;
	private OutputStream outputStream;
	
	/**
	 * Creates a new StreamCopier.
	 * 
	 * @param inputStream The stream that will be read from.
	 * @param outputStream The stream that will be written to.
	 */
	public StreamCopier(InputStream inputStream, OutputStream outputStream) {
		this.inputStream = inputStream;
		this.outputStream = outputStream;
	}
	
	/**
	 * Reads a byte from the InputStream, and writes it to the OutputStream.
	 * 
	 * @throws IOException, EOFException
	 * @throws ServiceUnavailableException 
	 */
	public void copyByte() throws IOException, ServiceUnavailableException {
		int input = inputStream.read();
		
		// Read returns -1 to signal EOF.
		// Close the stream and throw an EOFException.
		if (input == -1) {
			throw new EOFException();
		}
		
		outputStream.write(input);
	}

	/**
	 * Calls copyByte.
	 * 
	 * @throws IOException
	 * @throws EndOfLoopException when the end of file is received from the input stream
	 * @throws ServiceUnavailableException 
	 */
	@Override
	public void loopAction() throws IOException, EndOfLoopException, ServiceUnavailableException {
		try {
			copyByte();
		} catch(EOFException e) {
			EndOfLoopException endOfLoopException = new EndOfLoopException("Received end of file from the input stream.");
			endOfLoopException.initCause(e);
			throw endOfLoopException;
		}
	}

	/**
	 * Closes the input and output streams.
	 * 
	 * @throws IOException
	 */
	@Override
	public void terminateAction() throws IOException {
		outputStream.flush();
		outputStream.close();
		inputStream.close();
	}
}
