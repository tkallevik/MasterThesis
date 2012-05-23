package utilities.rpc;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.EOFException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import utilities.EndOfLoopException;
import utilities.ILoopObject;


/**
 * This class parses the input from the RPC communication stream into information needed for a method invocation, 
 * and sends them to the de-multiplexer. It also handles return values for the same invocations.
 *
 */
public class RPCServer implements ILoopObject {
	/**
	 * The encoding for the invoke command
	 */
	public static final String INVOKE = "INVOKE";
	
	/**
	 * The encoding for the delimiter between command and data
	 */
	public static final String DELIMITER = " ";
	
	/**
	 * The encoding for the argument command
	 */
	public static final String ARGUMENT = "ARG";
	
	/**
	 * The encoding for the command to signal number of arguments to follow
	 */
	public static final String NUMBER_OF_ARGUMENTS = "NOA";
	
	/**
	 * The encoding for the return value command
	 */
	public static final String RETURN_VALUE = "RETURN";
	
	/**
	 * The encoding for the command to return void
	 */
	public static final String RETURN_VOID = "VOID";
	
	/**
	 * The encoding for the command to return null
	 */
	public static final String RETURN_NULL = "NULL";

	/**
	 * The encoding for null values passed as arguments
	 */
	public static final String ARGUMENT_NULL = "NULL";
	
	private BufferedReader reader;
	private BufferedWriter writer;
	private IRPCObject rpcObject;

	/**
	 * Create a new RPCServer.
	 * 
	 * @param inputStream the InputStream to receive invocations from
	 * @param outputStream the OutputStream to send returns on
	 * @param rpcObject the RPCObject to invoke methods on
	 */
	public RPCServer(InputStream inputStream, OutputStream outputStream, IRPCObject rpcObject) {
		this.reader = new BufferedReader(new InputStreamReader(inputStream));
		this.writer = new BufferedWriter(new OutputStreamWriter(outputStream));
		this.rpcObject = rpcObject;
	}

	/**
	 * @throws EndOfLoopException when the RPC connection is closed
	 */
	@Override
	public void loopAction() throws Exception {
		try {
			listenAndProcess();	
		} catch(EOFException e) {
			EndOfLoopException endOfLoopException = new EndOfLoopException("The connection was closed.");
			endOfLoopException.initCause(e);
			throw endOfLoopException;
		}
	}

	@Override
	public void terminateAction() throws Exception {
		reader.close();
		writer.close();
	}
	
	/**
	 * Listen for RPC calls on the reader, and process them.
	 * 
	 * @throws Exception
	 */
	private void listenAndProcess() throws Exception {
		String input = reader.readLine();
		
		if (input == null) {
			throw new EOFException();
		}
		
		if (input.startsWith(INVOKE + DELIMITER)) {
			String methodName = input.substring(INVOKE.length() + DELIMITER.length());
			
			input = reader.readLine();
			if (input.startsWith(NUMBER_OF_ARGUMENTS + DELIMITER)) {
				int numberOfArguments = Integer.parseInt(input.substring(NUMBER_OF_ARGUMENTS.length() + DELIMITER.length()));
				String[] args = new String[numberOfArguments];
				for(int i = 0; i < args.length; i++) {
					input = reader.readLine();
					if (input.startsWith(ARGUMENT + DELIMITER)) {
						args[i] = input.substring(ARGUMENT.length() + DELIMITER.length());
						if (args[i].equals(ARGUMENT_NULL)) {
							args[i] = null;
						}
					}
				}
				
				String returnValue = rpcObject.invokeMethod(methodName, args);
				if (returnValue == null) {
					writer.write(RETURN_NULL);
				} else if (returnValue.equals(RETURN_VOID)) {
					writer.write(RETURN_VOID);
				} else {
					writer.write(RETURN_VALUE + DELIMITER + returnValue);
				}
				writer.newLine();
				writer.flush();
			}
		}
	}
}
