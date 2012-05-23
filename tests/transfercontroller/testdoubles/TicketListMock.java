package transfercontroller.testdoubles;

import testutilities.InvocationLog;
import transfercontroller.Ticket;
import transfercontroller.TicketList;

public class TicketListMock extends TicketList {
	public InvocationLog invocationLog;
	
	public TicketListMock() {
		invocationLog = new InvocationLog();
	}
	
	@Override
	public void add(Ticket ticket) {
		invocationLog.addInvocation("add");
	}
	
	@Override
	public void delete(String ticketId) {
		invocationLog.addInvocation("delete");
	}
}
