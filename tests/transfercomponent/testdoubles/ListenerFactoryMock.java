package transfercomponent.testdoubles;

import java.io.IOException;

import testutilities.InvocationLog;
import transfercomponent.ListenerFactory;
import transfercomponent.TransferComponentFactory;
import utilities.LoopRunnable;
import utilities.testdoubles.LoopRunnableMock;

/**
 * The responsibility of this class is to mock the ListenerFactory, and simply log the method invocations.
 *
 */
public class ListenerFactoryMock extends ListenerFactory {
	public InvocationLog invocationLog;
	private LoopRunnableMock runnableMock;
	
	public ListenerFactoryMock(TransferComponentFactory transferComponentFactory) {
		super(transferComponentFactory);
		invocationLog = new InvocationLog();
		runnableMock = new LoopRunnableMock();
	}
	
	public LoopRunnableMock getRunnableMock() {
		return runnableMock;
	}
	
	@Override
	public LoopRunnable createListener(int port) throws IOException {
		invocationLog.addInvocation("createListener");
		return runnableMock;
	}
}
