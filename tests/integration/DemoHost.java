package integration;

import java.io.IOException;

import posixcontroller.POSIXControllerFacade;
import posixcontroller.POSIXControllerFacadeFactory;

import fluxcapacitor.FluxCapacitorFacade;
import fluxcapacitor.FluxCapacitorFacadeFactory;

import iocontroller.IOControllerFacade;
import iocontroller.IOControllerFacadeFactory;
import loadingdock.LoadingDockFacade;
import loadingdock.LoadingDockFacadeFactory;
import metadatacontroller.MetaDataControllerFacade;
import metadatacontroller.MetaDataControllerFacadeFactory;
import revisioncontroller.RevisionControllerFacade;
import revisioncontroller.RevisionControllerFacadeFactory;
import synccontroller.SyncControllerFacade;
import synccontroller.SyncControllerFacadeFactory;
import transfercomponent.TransferComponentFacade;
import transfercomponent.TransferComponentFacadeFactory;
import transfercontroller.TransferControllerFacade;
import transfercontroller.TransferControllerFacadeFactory;
import unitcontroller.UnitControllerFacade;
import unitcontroller.UnitControllerFacadeFactory;
import archivecontroller.ArchiveControllerFacade;
import archivecontroller.ArchiveControllerFacadeFactory;
import archivecontroller.CLIBranchConflictEventListener;
import archivecontroller.IArchiveComponent;
import archivecontroller.testdoubles.TestBranchConflictEventListener;

public class DemoHost {
	public ArchiveControllerFacade archive;
	public POSIXControllerFacade posix;
	public SyncControllerFacade sync;
	public TestBranchConflictEventListener conflicts;
	public int syncControllerListenPort;
	
	public DemoHost(String ioControllerRepositoryPath) throws IOException {
		String transferHost = "localhost";
		int controllerPort = generatePort();
		String controllerHost = "localhost";
		int transferPort = generatePort();
		String fluxCapacitorListenHost = "localhost";
		int fluxCapacitorListenPort = generatePort();
		syncControllerListenPort = generatePort();
		int componentIndex = 0;
		IArchiveComponent[] components = new IArchiveComponent[4];
		
		// IO Controller
		IOControllerFacadeFactory ioControllerFacadeFactory = new IOControllerFacadeFactory();
		IOControllerFacade ioControllerFacade = ioControllerFacadeFactory.createFacade(ioControllerRepositoryPath);
		components[componentIndex++] = ioControllerFacade;
		
		// Loading Dock
		LoadingDockFacadeFactory loadingDockFacadeFactory = new LoadingDockFacadeFactory();
		LoadingDockFacade loadingDockFacade = loadingDockFacadeFactory.createFacade(ioControllerFacade);
		
		// Transfer Component
		TransferComponentFacadeFactory transferComponentFacadeFactory = new TransferComponentFacadeFactory();
		TransferComponentFacade transferComponentFacade = transferComponentFacadeFactory.createFacade(loadingDockFacade);
		transferComponentFacade.listenForFiles(transferPort);
		
		// Transfer Controller
		TransferControllerFacadeFactory transferControllerFacadeFactory = new TransferControllerFacadeFactory();
		TransferControllerFacade transferControllerFacade = transferControllerFacadeFactory.createFacade(transferComponentFacade, controllerHost, controllerPort, transferHost, transferPort);
		
		// Unit Controller
		UnitControllerFacadeFactory unitControllerFacadeFactory = new UnitControllerFacadeFactory();
		UnitControllerFacade unitControllerFacade = unitControllerFacadeFactory.createFacade(ioControllerFacade);
		components[componentIndex++] = unitControllerFacade;
		
		// Meta Data Controller
		MetaDataControllerFacadeFactory metaDataControllerFacadeFactory = new MetaDataControllerFacadeFactory();
		MetaDataControllerFacade metaDataControllerFacade = metaDataControllerFacadeFactory.createFacade(ioControllerFacade);
		components[componentIndex++] = metaDataControllerFacade;
		
		// Revision Controller
		RevisionControllerFacadeFactory revisionControllerFacadeFactory = new RevisionControllerFacadeFactory();
		RevisionControllerFacade revisionControllerFacade = revisionControllerFacadeFactory.createFacade(ioControllerFacade);
		components[componentIndex++] = revisionControllerFacade;
		
		// Archive Controller
		ArchiveControllerFacadeFactory archiveControllerFacadeFactory = new ArchiveControllerFacadeFactory();
		ArchiveControllerFacade archiveControllerFacade = archiveControllerFacadeFactory.createFacade(components);
		unitControllerFacade.addBranchEventListener(archiveControllerFacade);
		TestBranchConflictEventListener testBranchConflictEventListener = new TestBranchConflictEventListener();
		archiveControllerFacade.addBranchConflictEventListener(testBranchConflictEventListener);
		archiveControllerFacade.addBranchConflictEventListener(new CLIBranchConflictEventListener());
		
		// Flux Capacitor
		FluxCapacitorFacadeFactory fluxCapacitorFacadeFactory = new FluxCapacitorFacadeFactory();
		FluxCapacitorFacade fluxCapacitorFacade = fluxCapacitorFacadeFactory.createFacade(archiveControllerFacade, archiveControllerFacade, transferControllerFacade, loadingDockFacade, fluxCapacitorListenHost, fluxCapacitorListenPort);
		
		// POSIX Controller
		POSIXControllerFacadeFactory posixControllerFacadeFactory = new POSIXControllerFacadeFactory();
		POSIXControllerFacade posixControllerFacade = posixControllerFacadeFactory.createFacade(ioControllerFacade, archiveControllerFacade);
		
		// Sync Controller
		SyncControllerFacadeFactory syncControllerFacadeFactory = new SyncControllerFacadeFactory();
		SyncControllerFacade syncControllerFacade = syncControllerFacadeFactory.createFacade(archiveControllerFacade, fluxCapacitorFacade, syncControllerListenPort);
		
		// Add flux capacitor as container listener on loading dock
		loadingDockFacade.addContainerEventListener(fluxCapacitorFacade);
		
		archive = archiveControllerFacade;
		posix = posixControllerFacade;
		sync = syncControllerFacade;
		conflicts = testBranchConflictEventListener;
	}

	private int generatePort() {
		return (int) (Math.random() * 60000) + 1024;
	}
}
