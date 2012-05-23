package transfercomponent.testdoubles;

import testutilities.InvocationLog;
import transfercomponent.TransferComponentFacade;
import transfercontroller.Ticket;

/**
 * The responsibility of this class is to mock the TransferComponentFacade.
 */
public class TransferComponentFacadeMock extends TransferComponentFacade {
	public InvocationLog invocationLog = new InvocationLog();
	public Ticket ticketForPush;
	
	public TransferComponentFacadeMock() {
		super(null, null, null);
	}
	
	@Override
	public synchronized void stop(String ticketId) {
		invocationLog.addInvocation("stop");
		notifyAll();
	}
	
	@Override
	public synchronized void pushFile(Ticket ticket) {
		invocationLog.addInvocation("pushFile");
		ticketForPush = ticket;
		notifyAll();
	}

}
