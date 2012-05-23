package utilities.tests;

import static org.junit.Assert.*;

import org.junit.Test;

import utilities.LoopRunnable;
import utilities.testdoubles.LoopObjectMock;

public class LoopRunnableTest {

	/**
	 * Constructs a loop runnable, puts it in a thread and 
	 * verifies that it invokes loopAction on the ILoopObject it has been given.
	 * 
	 * @throws InterruptedException
	 */
	@Test
	public void invokeLoopActionTest() throws InterruptedException {
		// Set up fixture
		LoopObjectMock loopObject = new LoopObjectMock();
		LoopRunnable loopRunnable = new LoopRunnable(loopObject, null);
		
		// Exercise SUT
		new Thread(loopRunnable).start();
		
		synchronized (loopObject) {
			loopObject.wait();
		}
		loopRunnable.stop();
		
		// Verify outcome
		assertTrue(loopObject.invocationLog.getInvocationStatus("loopAction"));
	}
	
	/**
	 * Set up a LoopRunnable with an objectToWaitOn and a ILoopObject with nothing more to do,
	 * verify that it waits on it.
	 * 
	 * @throws InterruptedException
	 */
	@Test
	public void waitTest() throws InterruptedException {
		// Set up fixture
		LoopObjectMock loopObject = new LoopObjectMock();
		loopObject.setNothingMoreToDo();
		Object objectToWaitOn = new Object();
		LoopRunnable loopRunnable = new LoopRunnable(loopObject, objectToWaitOn);
		
		// Exercise SUT
		Thread thread = new Thread(loopRunnable);
		thread.start();
		
		synchronized (loopObject) {
			loopObject.wait();
		}
		
		Thread.sleep(100);
		
		loopRunnable.stop();
		
		// Verify outcome
		assertTrue(loopObject.invocationLog.getInvocationStatus("loopAction"));
		assertEquals(Thread.State.WAITING, thread.getState());
	}

}
