package utilities.rpc;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;


/**
 * This class transmits multiplexed method invocations to the corresponding RPCServer.
 *
 */
public class RPCClient {
	private BufferedReader reader;
	private BufferedWriter writer;
	
	/**
	 * Create a new RPCClient.
	 * 
	 * @param inputStream the InputStream to receive return values on
	 * @param outputStream the OutputStream to send method invocations on
	 */
	public RPCClient(InputStream inputStream, OutputStream outputStream) {
		this.reader = new BufferedReader(new InputStreamReader(inputStream));
		this.writer = new BufferedWriter(new OutputStreamWriter(outputStream));
	}
	
	/**
	 * Encode a multiplexed method invocation, and write it to the outputStream.
	 * Wait for the return on the inputStream. 
	 * 
	 * @param methodName the method to invoke
	 * @param args the arguments for the method
	 * @return a String representing the return value from the method
	 * @throws IOException
	 * @throws RPCException if there is a problem with the RPC communication
	 */
	public String invokeMethod(String methodName, String[] args) throws RPCException {	
		try {
			writer.write(RPCServer.INVOKE + RPCServer.DELIMITER + methodName);
			writer.newLine();
			
			writer.write(RPCServer.NUMBER_OF_ARGUMENTS + RPCServer.DELIMITER + args.length);
			writer.newLine();
			
			for (String arg : args) {
				if (arg == null) {
					arg = RPCServer.ARGUMENT_NULL;
				}
				writer.write(RPCServer.ARGUMENT + RPCServer.DELIMITER + arg);
				writer.newLine();
			}
			
			writer.flush();
			
			String input = reader.readLine();
			if (input.startsWith(RPCServer.RETURN_VALUE + RPCServer.DELIMITER)) {
				return input.substring(RPCServer.RETURN_VALUE.length() + RPCServer.DELIMITER.length());
			} else if(input.startsWith(RPCServer.RETURN_VOID)) {
				return RPCServer.RETURN_VOID;
			} else if(input.startsWith(RPCServer.RETURN_NULL)) {
				return null;
			}
		} catch(Exception e) {
			RPCException rpcException = new RPCException("An error occured with the RPC system");
			rpcException.initCause(e);
			throw rpcException;
		}
		
		throw new RPCException("The invoked method gave an invalid return");
	}

	public void close() {
		try {
			reader.close();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
