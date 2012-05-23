package integration;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import org.junit.After;
import org.junit.Test;

import archivecontroller.ArchiveControllerFacade;
import archivecontroller.ObjectReference;
import archivecontroller.QueryException;
import archivecontroller.testdoubles.ArchiveObjectEventListenerMock;

import testutilities.FileOperations;
import transfercomponent.testdoubles.TransferComponentFacadeFake;
import transfercomponent.testdoubles.TransferComponentFacadeFake.TestCase;
import transfercontroller.handshake.HandshakeException;
import unitcontroller.UnitPointer;
import utilities.ServiceUnavailableException;

import fluxcapacitor.FluxCapacitorFacade;

public class FluxCapacitorTest {
	private ObjectReference[] objectsToTransfer;
	private UnitPointer[] dummyUnitPointers;
	private String dummyUnitId;
	private int fluxCapacitorListenPortA;
	private int fluxCapacitorListenPortB;
	private String ioControllerRepositoryPathA;
	private String ioControllerRepositoryPathB;
	private TransferComponentFacadeFake transferComponentFacadeFake;
	
	@Test
	public void testFluxCapacitorFull() throws IOException, ServiceUnavailableException, HandshakeException, QueryException, InterruptedException {
		sendTest();
	}
	
	@Test
	public void testFluxCapacitorFake() throws IOException, ServiceUnavailableException, HandshakeException, QueryException, InterruptedException {
		// If transferComponentFacadeFake is not null, it will be used for the test
		transferComponentFacadeFake = new TransferComponentFacadeFake(TestCase.STREAM_COPIER_NO_SOCKET);
		
		sendTest();
	}
	
	public void sendTest() throws IOException, ServiceUnavailableException, HandshakeException, QueryException, InterruptedException {
		// Set up fixture	
		FluxCapacitorTestFactory fluxCapacitorTestFactoryA = setUpHostA();
		FluxCapacitorTestFactory fluxCapacitorTestFactoryB = setUpHostB();
		
		// If a fake transferComponent exits, then it needs source and destination loadingDockFacades.
		if (transferComponentFacadeFake != null) {
			transferComponentFacadeFake.setLoadingDockFacades(fluxCapacitorTestFactoryA.getLoadingDockFacade(), fluxCapacitorTestFactoryB.getLoadingDockFacade());
		}
		
		ArchiveObjectEventListenerMock archiveObjectEventListenerMock = new ArchiveObjectEventListenerMock(fluxCapacitorTestFactoryB.getArchiveControllerFacade());
		archiveObjectEventListenerMock.addObjectReferences(objectsToTransfer);
		
		FluxCapacitorFacade fluxCapacitorFacadeA = fluxCapacitorTestFactoryA.getFluxCapacitorFacade();
		
		// Exercise SUT
		fluxCapacitorFacadeA.sendUnit(dummyUnitId, "localhost", fluxCapacitorListenPortB);
		
		// Wait
		synchronized(archiveObjectEventListenerMock) {
			archiveObjectEventListenerMock.waitForImports();
		}
		
		// Verify outcome
		ArchiveControllerFacade archiveControllerFacadeB = fluxCapacitorTestFactoryB.getArchiveControllerFacade();
		for (UnitPointer unitPointer : dummyUnitPointers) {
			assertTrue(archiveControllerFacadeB.hasObject(unitPointer.getComponentId(), unitPointer.getObjectId()));
		}
	}
	
	@Test
	public void pullTest() throws IOException, ServiceUnavailableException, HandshakeException, QueryException, InterruptedException {
		// Set up fixture	
		setUpHostA();
		FluxCapacitorTestFactory fluxCapacitorTestFactoryB = setUpHostB();
		
		ArchiveObjectEventListenerMock archiveObjectEventListenerMock = new ArchiveObjectEventListenerMock(fluxCapacitorTestFactoryB.getArchiveControllerFacade());
		archiveObjectEventListenerMock.addObjectReferences(objectsToTransfer);
		
		FluxCapacitorFacade fluxCapacitorFacadeB = fluxCapacitorTestFactoryB.getFluxCapacitorFacade();
		
		// Exercise SUT
		fluxCapacitorFacadeB.pullUnit(dummyUnitId, "localhost", fluxCapacitorListenPortA);
		
		// Wait
		synchronized(archiveObjectEventListenerMock) {
			archiveObjectEventListenerMock.waitForImports();
		}
		
		// Verify outcome
		ArchiveControllerFacade archiveControllerFacadeB = fluxCapacitorTestFactoryB.getArchiveControllerFacade();
		for (UnitPointer unitPointer : dummyUnitPointers) {
			assertTrue(archiveControllerFacadeB.hasObject(unitPointer.getComponentId(), unitPointer.getObjectId()));
		}
	}
	
	private FluxCapacitorTestFactory setUpHostA() throws IOException, QueryException {
		ioControllerRepositoryPathA = "tests/dummy/ioControllerRepository/A/";
		String transferHost = "localhost";
		int controllerPort = generatePort();
		String controllerHost = "localhost";
		int transferPort = generatePort();
		String fluxCapacitorListenHostA = "localhost";
		fluxCapacitorListenPortA = generatePort();
		
		FluxCapacitorTestFactory fluxCapacitorTestFactory = new FluxCapacitorTestFactory();
		
		// If a fake transferComponent exists, use it for this setup
		if (transferComponentFacadeFake != null) {
			fluxCapacitorTestFactory.setTransferComponent(transferComponentFacadeFake);
		}
		
		fluxCapacitorTestFactory.setUpHost(ioControllerRepositoryPathA, transferHost, controllerHost, controllerPort, transferPort, fluxCapacitorListenHostA, fluxCapacitorListenPortA);
		
		// Insert dummy data
		dummyUnitPointers = new UnitPointer[3];
		dummyUnitPointers[0] = fluxCapacitorTestFactory.createRevision();
		dummyUnitPointers[1] = fluxCapacitorTestFactory.createMetaDataObject();
		dummyUnitPointers[2] = fluxCapacitorTestFactory.createBinaryObject();
		dummyUnitId = fluxCapacitorTestFactory.createUnit(dummyUnitPointers);
		
		objectsToTransfer = new ObjectReference[dummyUnitPointers.length + 1];
		for (int i = 0; i < dummyUnitPointers.length; i++) {
			UnitPointer unitPointer = dummyUnitPointers[i];
			objectsToTransfer[i] = new ObjectReference(unitPointer.getComponentId(), unitPointer.getObjectId());
		}
		
		objectsToTransfer[objectsToTransfer.length - 1] = new ObjectReference(fluxCapacitorTestFactory.getUnitControllerFacade().getUuid(), dummyUnitId);
		
		return fluxCapacitorTestFactory;
	}
	
	private FluxCapacitorTestFactory setUpHostB() throws IOException {
		ioControllerRepositoryPathB = "tests/dummy/ioControllerRepository/B/";
		String transferHost = "localhost";
		String controllerHost = "localhost";
		int controllerPort = generatePort();
		int transferPort = generatePort();
		String fluxCapacitorListenHostB = "localhost";
		fluxCapacitorListenPortB = generatePort();
		
		FluxCapacitorTestFactory fluxCapacitorTestFactory = new FluxCapacitorTestFactory();
		
		// If a fake transferComponent exists, use it for this setup
		if (transferComponentFacadeFake != null) {
			fluxCapacitorTestFactory.setTransferComponent(transferComponentFacadeFake);
		}
		
		fluxCapacitorTestFactory.setUpHost(ioControllerRepositoryPathB, transferHost, controllerHost, controllerPort, transferPort, fluxCapacitorListenHostB, fluxCapacitorListenPortB);
		
		// Add flux capacitor as container listener on loading dock
		fluxCapacitorTestFactory.getLoadingDockFacade().addContainerEventListener(fluxCapacitorTestFactory.getFluxCapacitorFacade());
		
		return fluxCapacitorTestFactory;
	}
	
	@After
	public void tearDownHostA() {
		FileOperations.emptyFolder(new File(ioControllerRepositoryPathA));
	}
	
	@After
	public void tearDownHostB() {
		FileOperations.emptyFolder(new File(ioControllerRepositoryPathB));
	}

	private int generatePort() {
		return (int) (Math.random() * 60000) + 1024;
	}
}
