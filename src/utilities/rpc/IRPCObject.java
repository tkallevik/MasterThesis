package utilities.rpc;

public interface IRPCObject {
	/**
	 * De-multiplex a method invocation.
	 * 
	 * @param methodName the name of the method to invoke
	 * @param args the arguments for the method
	 * @return a String representing the return-value of the method
	 * @throws Exception
	 */
	public String invokeMethod(String methodName, String[] args) throws Exception;
}
