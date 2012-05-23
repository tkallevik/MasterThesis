package transfercontroller.tests.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

import org.junit.Test;

import transfercontroller.Ticket;
import transfercontroller.TicketList;
import transfercontroller.handshake.HandshakeClient;
import transfercontroller.handshake.HandshakeException;
import transfercontroller.handshake.HandshakeServer;
import transfercontroller.handshake.HandshakeServerRPCObject;
import transfercontroller.handshake.HandshakeServerRemote;
import utilities.LoopRunnable;
import utilities.rpc.RPCClient;
import utilities.rpc.RPCServer;

public class HandshakeRPCTest {
	/**
	 * Set up both sides of a handshake using RPC subsystem over pipes.
	 * Verify that the negotiated ticket is the same on both sides after the handshake.
	 * 
	 * @throws IOException
	 * @throws HandshakeException
	 */
	@Test
	public void test() throws IOException, HandshakeException {
		// Set up fixture
		// Pipe from client to server
		PipedInputStream inputStreamClient = new PipedInputStream();
		PipedOutputStream outputStreamServer = new PipedOutputStream(inputStreamClient);
		
		// Pipe from server to client
		PipedInputStream inputStreamServer = new PipedInputStream();
		PipedOutputStream outputStreamClient = new PipedOutputStream(inputStreamServer);
		
		// Set up server side
		Ticket ticketServer = new Ticket();
		TicketList ticketListServer = new TicketList();
		HandshakeServer handshakeServer = new HandshakeServer(ticketListServer, ticketServer, null, 0);
		HandshakeServerRPCObject handshakeServerRPCObject = new HandshakeServerRPCObject(handshakeServer);
		RPCServer rpcServer = new RPCServer(inputStreamServer, outputStreamServer, handshakeServerRPCObject);
		LoopRunnable loopRunnable = new LoopRunnable(rpcServer, null);
		new Thread(loopRunnable).start();
		
		// Set up client side
		TicketList ticketListClient = new TicketList();
		RPCClient rpcClient = new RPCClient(inputStreamClient, outputStreamClient);
		HandshakeServerRemote handshakeServerRemote = new HandshakeServerRemote(rpcClient);
		HandshakeClient handshakeClient = new HandshakeClient(ticketListClient, handshakeServerRemote, new Ticket());
		
		// Exercise SUT
		Ticket ticketClient = handshakeClient.perform();
		
		// Verify outcome
		assertNotNull(ticketClient);
		assertEquals(ticketClient, ticketServer);
	}

}
