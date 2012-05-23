package utilities.testdoubles;

import java.io.IOException;

import testutilities.InvocationLog;
import utilities.ILoopObject;
import utilities.NothingMoreToActOnException;

/**
 * The responsibility of this class is to mock the ILoopObject, and simply log the method invocations.
 *
 */
public class LoopObjectMock implements ILoopObject {
	public InvocationLog invocationLog = new InvocationLog();
	private boolean nothingMoreToDo = false;

	/**
	 * Tell the mock to throw an NothingMoreToActOnException when loopAction is invoked.
	 */
	public void setNothingMoreToDo() {
		nothingMoreToDo = true;
	}
	
	@Override
	public synchronized void loopAction() throws Exception {
		invocationLog.addInvocation("loopAction");
		notifyAll();
		
		if (nothingMoreToDo) {
			throw new NothingMoreToActOnException("This exception is thrown because the mock was told to");
		}
	}

	@Override
	public void terminateAction() throws IOException {
		invocationLog.addInvocation("terminateAction");
	}

}
