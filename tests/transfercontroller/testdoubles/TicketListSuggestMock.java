package transfercontroller.testdoubles;

import testutilities.InvocationLog;
import transfercontroller.Ticket;
import transfercontroller.TicketList;

/**
 * The responsibility of this class is to serve out preconfigured values when
 * asked for suggestions for available ticket ids. It also has a method for 
 * bulk adding of tickets to simulate occupied ids.
 */
public class TicketListSuggestMock extends TicketList {
	public InvocationLog invocationLog;
	private String[] idsToSuggest;
	private int i = 0;
	
	public TicketListSuggestMock(String[] idsToSuggest) {
		this.idsToSuggest = idsToSuggest;
		invocationLog = new InvocationLog();
	}
	
	@Override
	public void add(Ticket ticket) {
		invocationLog.addInvocation("add");
		super.add(ticket);
	}
	
	@Override
	public String suggestAvailableId() {
		return idsToSuggest[i++];
	}
	
	/**
	 * Adds all ids to the ticketlist, thereby making them unavailable.
	 * 
	 * @param idsToOccupy an array of ids to occupy
	 */
	public void occupy(String[] idsToOccupy) {
		for(String id : idsToOccupy) {
			Ticket ticket = new Ticket(id);
			add(ticket);
		}
	}
}
