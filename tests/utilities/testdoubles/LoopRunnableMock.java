package utilities.testdoubles;

import testutilities.InvocationLog;
import utilities.ILoopObject;
import utilities.LoopRunnable;

/**
 * The responsibility of this class is to mock a (Loop)Runnable, and simply log the method invocations.
 *
 */
public class LoopRunnableMock extends LoopRunnable implements Runnable {
	public InvocationLog invocationLog = new InvocationLog();
	
	public LoopRunnableMock() {
		// If this is supposed to be used as an ordinary runnable
		// we need a default constructor like this.
		// Since this is a mock we can simply pass null as the ILoopObject argument.
		super(null, null);
	}
	
	public LoopRunnableMock(ILoopObject iLoopObject, Object objectToWaitOn) {
		super(iLoopObject, objectToWaitOn);
	}
	
	/**
	 * Logs the invocation of the run method, notifies other test threads and waits until die() is invoked.
	 * The purpose of the last point is to keep the thread alive until the invocation log has been checked.
	 */
	@Override
	public synchronized void run() {
		invocationLog.addInvocation("run");
		
		// A lot of tests will be waiting for this thread to start, before checking
		// the invocation log.
		notifyAll();
				
		try {
			wait();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Notify and thereby stop the waiting thread.
	 */
	public synchronized void die() {
		notifyAll();
	}
}
