package integration;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.junit.After;
import org.junit.Test;

import archivecontroller.ObjectReference;
import archivecontroller.QueryException;

import synccontroller.SyncControllerFacade;
import synccontroller.SyncControllerFacadeFactory;
import testutilities.FileOperations;
import transfercontroller.handshake.HandshakeException;
import unitcontroller.UnitPointer;
import utilities.ServiceUnavailableException;

public class SyncControllerTest {
	private String ioControllerRepositoryPathA;
	private String ioControllerRepositoryPathB;
	private ArrayList<ObjectReference> allObjects;
	
	@Test
	public void test() throws IOException, ServiceUnavailableException, HandshakeException, QueryException, InterruptedException {
		// Set up fixture
		allObjects = new ArrayList<ObjectReference>();
		
		// Set up A
		int syncListenPortA = generatePort();
		FluxCapacitorTestFactory fluxCapacitorTestFactoryA = setUpHostA();
		SyncControllerFacadeFactory syncControllerFacadeFactoryA = new SyncControllerFacadeFactory();
		SyncControllerFacade syncControllerFacadeA = syncControllerFacadeFactoryA.createFacade(fluxCapacitorTestFactoryA.getArchiveControllerFacade(), 
				fluxCapacitorTestFactoryA.getFluxCapacitorFacade(), syncListenPortA);
		
		// Insert dummy data for host A
		createBranch(fluxCapacitorTestFactoryA, 1);
		createBranch(fluxCapacitorTestFactoryA, 4);
		
		// Set up B
		int syncListenPortB = generatePort();
		FluxCapacitorTestFactory fluxCapacitorTestFactoryB = setUpHostB();
		SyncControllerFacadeFactory syncControllerFacadeFactoryB = new SyncControllerFacadeFactory();
		syncControllerFacadeFactoryB.createFacade(fluxCapacitorTestFactoryB.getArchiveControllerFacade(), 
				fluxCapacitorTestFactoryB.getFluxCapacitorFacade(), syncListenPortB);

		// Insert dummy data for host B
		createBranch(fluxCapacitorTestFactoryB, 5);
		String branchId = createBranch(fluxCapacitorTestFactoryB, 3);
		
		// Exercise SUT
		syncControllerFacadeA.sync("localhost", syncListenPortB);
		
		Thread.sleep(3000);
		
		// Verify outcome
		for (ObjectReference objectReference : allObjects) {
			String componentId = objectReference.getComponentId();
			String objectId = objectReference.getObjectId();
			assertTrue(fluxCapacitorTestFactoryA.getArchiveControllerFacade().hasObject(componentId, objectId));
			assertTrue(fluxCapacitorTestFactoryB.getArchiveControllerFacade().hasObject(componentId, objectId));
		}
		
		// Insert more dummy data for host B to create a fast-forwardable conflict
		String unitId = fluxCapacitorTestFactoryB.createUnit(branchId);
		
		// Exercise SUT
		syncControllerFacadeA.sync("localhost", syncListenPortB);

		Thread.sleep(1000);
		
		// Verify that the branch was synced and fast-forwarded
		assertFalse(fluxCapacitorTestFactoryA.getUnitControllerFacade().hasConflicts());
		assertTrue(fluxCapacitorTestFactoryA.getUnitControllerFacade().hasObject(unitId));
		assertTrue(fluxCapacitorTestFactoryA.getUnitControllerFacade().hasObject(branchId));
		assertEquals(unitId, fluxCapacitorTestFactoryA.getUnitControllerFacade().getBranchUnit(branchId));
		
		// Insert more dummy data for host A and B to create a non-fast-forwardable conflict
		String unitIdA = fluxCapacitorTestFactoryA.createUnit(branchId);
		String unitIdB = fluxCapacitorTestFactoryB.createUnit(branchId);
		
		// Exercise SUT
		syncControllerFacadeA.sync("localhost", syncListenPortB);

		Thread.sleep(1000);
		
		// Verify that the branch was synced and that conflicts exists
		assertTrue(fluxCapacitorTestFactoryA.getUnitControllerFacade().hasConflicts());
		assertTrue(fluxCapacitorTestFactoryB.getUnitControllerFacade().hasConflicts());
		
		assertTrue(fluxCapacitorTestFactoryA.getUnitControllerFacade().hasObject(unitIdA));
		assertTrue(fluxCapacitorTestFactoryB.getUnitControllerFacade().hasObject(unitIdA));
		
		assertTrue(fluxCapacitorTestFactoryA.getUnitControllerFacade().hasObject(unitIdB));
		assertTrue(fluxCapacitorTestFactoryB.getUnitControllerFacade().hasObject(unitIdB));
		
		assertEquals(unitIdA, fluxCapacitorTestFactoryA.getUnitControllerFacade().getBranchUnit(branchId));
		assertEquals(unitIdB, fluxCapacitorTestFactoryB.getUnitControllerFacade().getBranchUnit(branchId));
	}
	
	private FluxCapacitorTestFactory setUpHostA() throws IOException, QueryException {
		ioControllerRepositoryPathA = "tests/dummy/ioControllerRepository/A/";
		String transferHost = "localhost";
		int controllerPort = generatePort();
		String controllerHost = "localhost";
		int transferPort = generatePort();
		String fluxCapacitorListenHostA = "localhost";
		int fluxCapacitorListenPortA = generatePort();
		
		FluxCapacitorTestFactory fluxCapacitorTestFactory = new FluxCapacitorTestFactory();		
		fluxCapacitorTestFactory.setUpHost(ioControllerRepositoryPathA, transferHost, controllerHost, controllerPort, transferPort, fluxCapacitorListenHostA, fluxCapacitorListenPortA);
		
		// Add flux capacitor as container listener on loading dock
		fluxCapacitorTestFactory.getLoadingDockFacade().addContainerEventListener(fluxCapacitorTestFactory.getFluxCapacitorFacade());
		
		return fluxCapacitorTestFactory;
	}
	
	private FluxCapacitorTestFactory setUpHostB() throws IOException, QueryException {
		ioControllerRepositoryPathB = "tests/dummy/ioControllerRepository/B/";
		String transferHost = "localhost";
		String controllerHost = "localhost";
		int controllerPort = generatePort();
		int transferPort = generatePort();
		String fluxCapacitorListenHostB = "localhost";
		int fluxCapacitorListenPortB = generatePort();
		
		FluxCapacitorTestFactory fluxCapacitorTestFactory = new FluxCapacitorTestFactory();		
		fluxCapacitorTestFactory.setUpHost(ioControllerRepositoryPathB, transferHost, controllerHost, controllerPort, transferPort, fluxCapacitorListenHostB, fluxCapacitorListenPortB);
		
		// Add flux capacitor as container listener on loading dock
		fluxCapacitorTestFactory.getLoadingDockFacade().addContainerEventListener(fluxCapacitorTestFactory.getFluxCapacitorFacade());
		
		return fluxCapacitorTestFactory;
	}

	private String createBranch(FluxCapacitorTestFactory fluxCapacitorTestFactory, int numberOfUnits) throws IOException, QueryException {
		String[] unitIds = new String[numberOfUnits];
		String[] revisionIds = new String[numberOfUnits];
		
		for (int i = 0; i < numberOfUnits; i++) {
			String parentId;
			if (i == 0) {
				parentId = null;
			} else {
				parentId = revisionIds[i-1];
			}
			
			UnitPointer[] dummyUnitPointers = new UnitPointer[3];
			
			dummyUnitPointers[0] = fluxCapacitorTestFactory.createRevision(parentId);
			revisionIds[i] = dummyUnitPointers[0].getObjectId();
			
			dummyUnitPointers[1] = fluxCapacitorTestFactory.createMetaDataObject();
			dummyUnitPointers[2] = fluxCapacitorTestFactory.createBinaryObject();
			unitIds[i] = fluxCapacitorTestFactory.createUnit(dummyUnitPointers);
			
			for (int j = 0; j < dummyUnitPointers.length; j++) {
				UnitPointer unitPointer = dummyUnitPointers[j];
				allObjects.add(new ObjectReference(unitPointer.getComponentId(), unitPointer.getObjectId()));
			}
			
			allObjects.add(new ObjectReference(fluxCapacitorTestFactory.getUnitControllerFacade().getUuid(), unitIds[i]));
		}
		
		String dummyBranchId = fluxCapacitorTestFactory.createBranch(unitIds[unitIds.length-1]);
		allObjects.add(new ObjectReference(fluxCapacitorTestFactory.getUnitControllerFacade().getUuid(), dummyBranchId));
		
		return dummyBranchId;
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
