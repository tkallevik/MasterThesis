package integration;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import loadingdock.ContainerHeader;
import loadingdock.testdoubles.ContainerEventListenerMock;

import org.junit.After;
import org.junit.Test;

import testutilities.FileOperations;
import transfercontroller.TransferControllerFacade;
import unitcontroller.UnitPointer;
import utilities.ServiceUnavailableException;

public class TransferAndLoadingDockTest {
	private String[] containerIdsToTransfer;
	private String[] dummyComponentHeaderObjects;
	private String[] dummyDataObjects;
	private int transferControllerListenPortB = generatePort();
	private String ioControllerRepositoryPathA;
	private String ioControllerRepositoryPathB;
	private ContainerEventListenerMock containerEventListenerMock;

	@Test
	public void test() throws IOException, ServiceUnavailableException {
		// Set up fixture		
		FluxCapacitorTestFactory fluxCapacitorTestFactoryA = setUpHostA();
		FluxCapacitorTestFactory fluxCapacitorTestFactoryB = setUpHostB();
		
		TransferControllerFacade transferControllerFacadeA = fluxCapacitorTestFactoryA.getTransferControllerFacade();
		
		// Exercise SUT
		transferControllerFacadeA.sendContainers(containerIdsToTransfer, "localhost", transferControllerListenPortB);
		
		// Wait
		synchronized(containerEventListenerMock) {
			containerEventListenerMock.waitForContainers();
		}
		
		// Verify outcome
		for (int i = 0; i < containerIdsToTransfer.length; i++) {
			ContainerHeader containerHeader = fluxCapacitorTestFactoryB.getLoadingDockFacade().getInboundContainerHeader(containerIdsToTransfer[i]);
			assertNotNull(containerHeader);
			assertEquals(dummyComponentHeaderObjects[i], containerHeader.getComponentHeaderBinaryObjectId());
			assertEquals(dummyDataObjects[i], containerHeader.getDataBinaryObjectId());
			assertTrue(fluxCapacitorTestFactoryB.getIOControllerFacade().hasBinaryObject(dummyComponentHeaderObjects[i]));
			assertTrue(fluxCapacitorTestFactoryB.getIOControllerFacade().hasBinaryObject(dummyDataObjects[i]));
		}
	}
	

	
	private FluxCapacitorTestFactory setUpHostA() throws IOException {
		ioControllerRepositoryPathA = "tests/dummy/ioControllerRepository/A/";
		String transferHost = "localhost";
		int controllerPort = generatePort();
		String controllerHost = "localhost";
		int transferPort = generatePort();
		String fluxCapacitorListenHostA = "localhost";
		int fluxCapacitorListenPortA = generatePort();
		
		FluxCapacitorTestFactory fluxCapacitorTestFactory = new FluxCapacitorTestFactory();		
		fluxCapacitorTestFactory.setUpHost(ioControllerRepositoryPathA, transferHost, controllerHost, controllerPort, transferPort, fluxCapacitorListenHostA, fluxCapacitorListenPortA);
		
		// Create dummy containers
		containerIdsToTransfer = new String[3];
		dummyComponentHeaderObjects = new String[containerIdsToTransfer.length];
		dummyDataObjects = new String[containerIdsToTransfer.length];
		for (int i = 0; i < containerIdsToTransfer.length; i++) {
			UnitPointer componentHeader = fluxCapacitorTestFactory.createBinaryObject();
			UnitPointer data = fluxCapacitorTestFactory.createBinaryObject();
			containerIdsToTransfer[i] = fluxCapacitorTestFactory.getLoadingDockFacade().createContainer("test", componentHeader.getObjectId(), data.getObjectId());
			dummyComponentHeaderObjects[i] = componentHeader.getObjectId();
			dummyDataObjects[i] = data.getObjectId();
		}
		
		return fluxCapacitorTestFactory;
	}
	
	private FluxCapacitorTestFactory setUpHostB() throws IOException {
		ioControllerRepositoryPathB = "tests/dummy/ioControllerRepository/B/";
		String transferHost = "localhost";
		String controllerHost = "localhost";
		transferControllerListenPortB = generatePort();
		int transferPort = generatePort();
		String fluxCapacitorListenHostB = "localhost";
		int fluxCapacitorListenPortB = generatePort();
		
		FluxCapacitorTestFactory fluxCapacitorTestFactory = new FluxCapacitorTestFactory();
		fluxCapacitorTestFactory.setUpHost(ioControllerRepositoryPathB, transferHost, controllerHost, transferControllerListenPortB, transferPort, fluxCapacitorListenHostB, fluxCapacitorListenPortB);
		
		containerEventListenerMock = new ContainerEventListenerMock(containerIdsToTransfer);
		fluxCapacitorTestFactory.getLoadingDockFacade().addContainerEventListener(containerEventListenerMock);
		
		return fluxCapacitorTestFactory;
	}

	private int generatePort() {
		return (int) (Math.random() * 60000) + 1024;
	}
	
	@After
	public void tearDownHostA() {
		FileOperations.emptyFolder(new File(ioControllerRepositoryPathA));
	}
	
	@After
	public void tearDownHostB() {
		FileOperations.emptyFolder(new File(ioControllerRepositoryPathB));
	}

}
