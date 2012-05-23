package transfercontroller.tests.unit;

import static org.junit.Assert.*;

import org.junit.Test;

import transfercontroller.Ticket;

public class TicketTest {

	/**
	 * Test that two different tickets with the same values is considered equal.
	 */
	@Test
	public void equalsTest1() {
		// Set up fixture
		String[] containerIds = {"foo", "bar"};
		
		Ticket ticketA = new Ticket();
		ticketA.setId("bar");
		ticketA.setHost("foobar");
		ticketA.setPort(9999);
		ticketA.setContainerIds(containerIds);
		
		Ticket ticketB = new Ticket();
		ticketB.setId("bar");
		ticketB.setHost("foobar");
		ticketB.setPort(9999);
		ticketB.setContainerIds(containerIds);
		
		// Exercise SUT
		boolean equality = ticketA.equals(ticketB);
		
		// Verify outcome
		assertTrue(equality);
	}
	
	/**
	 * Test that two different tickets with different ids is considered not equal.
	 */
	@Test
	public void equalsTest2() {
		// Set up fixture
		Ticket ticketA = new Ticket();
		ticketA.setId("bar");
		
		Ticket ticketB = new Ticket();
		ticketB.setId("foo");
		
		// Exercise SUT
		boolean equality = ticketA.equals(ticketB);
		
		// Verify outcome
		assertFalse(equality);
	}
	
	/**
	 * Test that an object of type other than ticket is considered not equal.
	 */
	@Test
	public void equalsTest3() {
		// Set up fixture
		Ticket ticket = new Ticket();
		ticket.setId("bar");
		
		String string = "Mock";
		
		// Exercise SUT
		boolean equality = ticket.equals(string);
		
		// Verify outcome
		assertFalse(equality);
	}

}
