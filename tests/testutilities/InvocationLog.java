package testutilities;

import java.util.HashMap;
import java.util.Map;

/**
 * The responsibility of this class is to store information about method invocations.
 * This is typically used in mock objects. 
 *
 */
public class InvocationLog {
	private Map<String,Boolean> invocations;
	
	/**
	 * Create a new InvocationLog.
	 */
	public InvocationLog() {
		invocations = new HashMap<String, Boolean>();
	}
	
	/**
	 * Mark the given method as having been invoked.
	 * @param methodName the method to mark
	 */
	public void addInvocation(String methodName) {
		invocations.put(methodName, true);
	}
	
	/**
	 * Get the invocation status for a given method.
	 * 
	 * @param methodName the method of which to get the status
	 * @return true if the method has been invoked, false otherwise
	 */
	public Boolean getInvocationStatus(String methodName) {
		Boolean b = invocations.get(methodName);
		if (b != null && b)
			return true;
		
		return false;
	}
}
