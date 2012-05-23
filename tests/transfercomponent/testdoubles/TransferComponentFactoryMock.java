package transfercomponent.testdoubles;

import java.net.Socket;

import testutilities.InvocationLog;
import transfercomponent.TransferComponentFactory;
import utilities.LoopRunnable;
import utilities.testdoubles.LoopRunnableMock;

/**
 * The responsibility of this class is to mock the TransferComponentFactory, and simply log the method invocations.
 *
 */
public class TransferComponentFactoryMock extends TransferComponentFactory {
	public InvocationLog invocationLog;
	private LoopRunnableMock runnableMock;
	
	public TransferComponentFactoryMock() {
		super(null, null);
		invocationLog = new InvocationLog();
		runnableMock = new LoopRunnableMock();
	}
	
	public LoopRunnableMock getRunnableMock() {
		return runnableMock;
	}
	
	@Override
	public LoopRunnable createPushSender(String host, int port, String path, String ticketId) {
		invocationLog.addInvocation("createPushSender");
		return runnableMock;
	}
	
	@Override
	public LoopRunnable createPushReceiver(Socket socket) {
		invocationLog.addInvocation("createPushReceiver");
		return runnableMock;
	}
}
