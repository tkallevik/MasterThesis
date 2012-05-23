package integration;

import java.io.IOException;

import archivecontroller.ArchiveControllerFacade;
import archivecontroller.ArchiveControllerFacadeFactory;
import archivecontroller.CLIBranchConflictEventListener;
import archivecontroller.IArchiveComponent;
import archivecontroller.QueryException;

import fluxcapacitor.FluxCapacitorFacade;
import fluxcapacitor.FluxCapacitorFacadeFactory;

import iocontroller.IOControllerFacade;
import iocontroller.IOControllerFacadeFactory;
import iocontroller.utilities.BinaryObjectCreator;
import loadingdock.LoadingDockFacade;
import loadingdock.LoadingDockFacadeFactory;
import metadatacontroller.MetaDataControllerFacade;
import metadatacontroller.MetaDataControllerFacadeFactory;
import revisioncontroller.IRevisionControllerFacade;
import revisioncontroller.RevisionControllerFacade;
import revisioncontroller.RevisionControllerFacadeFactory;
import transfercomponent.ITransferComponentFacade;
import transfercomponent.TransferComponentFacadeFactory;
import transfercontroller.TransferControllerFacade;
import transfercontroller.TransferControllerFacadeFactory;
import unitcontroller.Unit;
import unitcontroller.UnitControllerFacade;
import unitcontroller.UnitControllerFacadeFactory;
import unitcontroller.UnitPointer;

public class FluxCapacitorTestFactory {	
	private IOControllerFacade ioControllerFacade;
	private LoadingDockFacade loadingDockFacade;
	private TransferControllerFacade transferControllerFacade;
	private ITransferComponentFacade transferComponentFacade;
	private UnitControllerFacade unitControllerFacade;
	private MetaDataControllerFacade metaDataControllerFacade;
	private RevisionControllerFacade revisionControllerFacade;
	private ArchiveControllerFacade archiveControllerFacade;
	private FluxCapacitorFacade fluxCapacitorFacade;

	public void setTransferComponent(ITransferComponentFacade transferComponentFacade) {
		this.transferComponentFacade = transferComponentFacade;
	}
	
	public void setUpHost(String ioControllerRepositoryPath, String transferHost, String controllerHost, int controllerPort, int transferPort, String fluxCapacitorListenHost, int fluxCapacitorListenPort) throws IOException {
		int componentIndex = 0;
		IArchiveComponent[] components = new IArchiveComponent[4];
		
		// IO Controller
		IOControllerFacadeFactory ioControllerFacadeFactory = new IOControllerFacadeFactory();
		ioControllerFacade = ioControllerFacadeFactory.createFacade(ioControllerRepositoryPath);
		components[componentIndex++] = ioControllerFacade;
		
		// Loading Dock
		LoadingDockFacadeFactory loadingDockFacadeFactory = new LoadingDockFacadeFactory();
		loadingDockFacade = loadingDockFacadeFactory.createFacade(ioControllerFacade);
		
		// Transfer Component
		if (transferComponentFacade == null) {
			TransferComponentFacadeFactory transferComponentFacadeFactory = new TransferComponentFacadeFactory();
			transferComponentFacade = transferComponentFacadeFactory.createFacade(loadingDockFacade);
			transferComponentFacade.listenForFiles(transferPort);
		}
		
		// Transfer Controller
		TransferControllerFacadeFactory transferControllerFacadeFactory = new TransferControllerFacadeFactory();
		transferControllerFacade = transferControllerFacadeFactory.createFacade(transferComponentFacade, controllerHost, controllerPort, transferHost, transferPort);
		
		// Unit Controller
		UnitControllerFacadeFactory unitControllerFacadeFactory = new UnitControllerFacadeFactory();
		unitControllerFacade = unitControllerFacadeFactory.createFacade(ioControllerFacade);
		components[componentIndex++] = unitControllerFacade;
		
		// Meta Data Controller
		MetaDataControllerFacadeFactory metaDataControllerFacadeFactory = new MetaDataControllerFacadeFactory();
		metaDataControllerFacade = metaDataControllerFacadeFactory.createFacade(ioControllerFacade);
		components[componentIndex++] = metaDataControllerFacade;
		
		// Revision Controller
		RevisionControllerFacadeFactory revisionControllerFacadeFactory = new RevisionControllerFacadeFactory();
		revisionControllerFacade = revisionControllerFacadeFactory.createFacade(ioControllerFacade);
		components[componentIndex++] = revisionControllerFacade;
		
		// Archive Controller
		ArchiveControllerFacadeFactory archiveControllerFacadeFactory = new ArchiveControllerFacadeFactory();
		archiveControllerFacade = archiveControllerFacadeFactory.createFacade(components);
		unitControllerFacade.addBranchEventListener(archiveControllerFacade);
		archiveControllerFacade.addBranchConflictEventListener(new CLIBranchConflictEventListener());
		
		// Flux Capacitor
		FluxCapacitorFacadeFactory fluxCapacitorFacadeFactory = new FluxCapacitorFacadeFactory();
		fluxCapacitorFacade = fluxCapacitorFacadeFactory.createFacade(archiveControllerFacade, archiveControllerFacade, transferControllerFacade, loadingDockFacade, fluxCapacitorListenHost, fluxCapacitorListenPort);
	}
	
	public UnitPointer createBinaryObject() throws IOException {
		int[] dummyBinaryData = new int[10];
		for (int i = 0; i < dummyBinaryData.length; i++) {
			dummyBinaryData[i] = (int) (Math.random() * 127);
		}
		
		String binaryObjectId = BinaryObjectCreator.createBinaryObject(ioControllerFacade, dummyBinaryData);
		
		return new UnitPointer(ioControllerFacade.getUuid(), binaryObjectId);
	}
	
	public String createUnit(UnitPointer[] unitPointers) {
		String unitId = unitControllerFacade.createUnit();
		
		for (UnitPointer unitPointer : unitPointers) {
			unitControllerFacade.addPointer(unitId, unitPointer.getComponentId(), unitPointer.getObjectId());
		}
		
		return unitId;
	}
	
	public String createBranch(String unitId) {
		return unitControllerFacade.createBranch(unitId);
	}
	
	public UnitPointer createMetaDataObject() {
		String metaDataObjectId = metaDataControllerFacade.createBlank();
		
		String[] dummyKeys = new String[5];
		String[] dummyValues = new String[10];
		for (int i = 0; i < dummyKeys.length; i++) {
			dummyKeys[i] = String.valueOf((int) (Math.random() * 127));
			dummyValues[i*2] = String.valueOf((int) (Math.random() * 127));
			dummyValues[i*2+1] = String.valueOf((int) (Math.random() * 127));
			metaDataControllerFacade.addValue(metaDataObjectId, dummyKeys[i], dummyValues[i*2]);
			metaDataControllerFacade.addValue(metaDataObjectId, dummyKeys[i], dummyValues[i*2+1]);
		}
		
		return new UnitPointer(metaDataControllerFacade.getUuid(), metaDataObjectId);
	}
	
	public UnitPointer createRevision() throws QueryException {
		String[] parentIds = {"foo", "bar"};
		
		return createRevisions(parentIds);
	}
	
	public UnitPointer createRevision(String parentId) throws QueryException {
		if (parentId == null) {
			String[] parentIds = {};
			return createRevisions(parentIds);
		}
		
		String[] parentIds = {parentId};
		
		return createRevisions(parentIds);
	}

	public UnitPointer createRevisions(String[] parentIds) throws QueryException {
		String revisionId = revisionControllerFacade.add(parentIds);
		
		return new UnitPointer(revisionControllerFacade.getUuid(), revisionId);
	}

	public FluxCapacitorFacade getFluxCapacitorFacade() {
		return fluxCapacitorFacade;
	}

	public ArchiveControllerFacade getArchiveControllerFacade() {
		return archiveControllerFacade;
	}
	
	public UnitControllerFacade getUnitControllerFacade() {
		return unitControllerFacade;
	}

	public LoadingDockFacade getLoadingDockFacade() {
		return loadingDockFacade;
	}

	public TransferControllerFacade getTransferControllerFacade() {
		return transferControllerFacade;
	}

	public IOControllerFacade getIOControllerFacade() {
		return ioControllerFacade;
	}

	public String createUnit(String branchId) throws QueryException, IOException {
		String unitId = unitControllerFacade.getBranchUnit(branchId);
		Unit unit = unitControllerFacade.getUnit(unitId);
		
		String parentId = unit.getObjectId(archiveControllerFacade.getComponentId(IRevisionControllerFacade.class));
		
		UnitPointer[] unitPointers = {createBinaryObject(), createMetaDataObject(), createRevision(parentId)};
		String newUnitId = createUnit(unitPointers);
		
		unitControllerFacade.moveBranch(branchId, newUnitId);
		
		return newUnitId;
	}
}
