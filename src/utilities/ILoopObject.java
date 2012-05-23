package utilities;

public interface ILoopObject {
	/**
	 * A method that is supposed to be called by a simple loop.
	 * 
	 * @throws Exception
	 */
	public void loopAction() throws Exception;
	
	/**
	 * A method that is supposed to be called when the loop exits.
	 * 
	 * @throws Exception
	 */
	public void terminateAction() throws Exception;
}
