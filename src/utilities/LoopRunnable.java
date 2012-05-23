package utilities;

/**
 * The responsibility of this class is to execute the loopAction method on the ILoopObject.
 * The action will be called repeatedly until it is told to stop.
 * It implements Runnable, so it can be run by a thread. 
 *
 */
public class LoopRunnable implements Runnable {
	private ILoopObject iLoopObject;
	private boolean running;
	private Object objectToWaitOn;
	
	/**
	 * Creates a new LoopRunnable.
	 * 
	 * @param iLoopObject the object that contains the loopAction method.
	 */
	public LoopRunnable(ILoopObject iLoopObject, Object objectToWaitOn) {
		this.iLoopObject = iLoopObject;
		this.running = true;
		this.objectToWaitOn = objectToWaitOn;
	}
	
	/**
	 * Calls the loop action method in a loop, until the running variable is set to false.
	 * Also calls the terminate action on the loop object before it exits.
	 */
	@Override
	public void run() {
		try {
			while(running) {
				try {
					iLoopObject.loopAction();
				} catch (NothingMoreToActOnException nothingMoreToActOnException) {
					synchronized (objectToWaitOn) {
						objectToWaitOn.wait();
					}
				}
			}
		} catch(EndOfLoopException e) {
			
		} catch (Exception e) {
			/* TODO For the time being any exceptions thrown by the loopAction method 
			 * will have to be trapped here and cause the execution to stop.
			 * On a longer term, there might be some sort of pub/sub system to handle this.
			 */
			e.printStackTrace();
		} finally {
			try {
				stop();
				iLoopObject.terminateAction();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Stop the execution of the loopAction-method.
	 */
	public void stop() {
		running = false;
	}
}
