package archivecontroller.tests.integration;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.HashMap;
import iocontroller.IOControllerFacade;
import iocontroller.IOControllerFacadeFactory;
import iocontroller.utilities.BinaryObjectCreator;

import metadatacontroller.MetaDataControllerFacade;
import metadatacontroller.MetaDataControllerFacadeFactory;

import org.junit.Test;

import revisioncontroller.RevisionControllerFacade;
import revisioncontroller.RevisionControllerFacadeFactory;
import unitcontroller.UnitControllerFacade;
import unitcontroller.UnitControllerFacadeFactory;

import archivecontroller.ArchiveControllerFacadeFactory;
import archivecontroller.ExportReference;
import archivecontroller.IArchiveComponent;
import archivecontroller.IArchiveControllerFacade;
import archivecontroller.QueryException;

public class ArchiveComponentTest {
	private IOControllerFacade ioControllerFacade;
	private IArchiveControllerFacade archiveControllerFacade;
	private HashMap<String, String[]> componentToTestObjectIdsMapping = new HashMap<String, String[]>();
	
	@Test
	public void test() throws Exception {
		// Set up fixture
		int numberOfExpectedTestRuns = 5;
		int numberOfActualTestRuns = 0;
		ioControllerFacade = setUpIOController();
		IArchiveComponent[] components = new IArchiveComponent[4];
		components[0] = setUpRevisionController();
		components[1] = setUpUnitController();
		components[2] = ioControllerFacade;
		components[3] = setUpMetaDataController();
		archiveControllerFacade = setUpArchiveController(components);
		
		// Exercise SUT
		for (IArchiveComponent component : components) {
			// Look up the objects for test
			String[] objectIds = componentToTestObjectIdsMapping.get(component.getUuid());
			
			for (String objectId : objectIds) {
				// Hash the object
				String originalHash = component.getHash(objectId);
				
				// Export the object
				ExportReference exportReference = archiveControllerFacade.exportObject(component.getUuid(), objectId);
				
				// Delete the object
				component.deleteObject(objectId);
				
				// Verify that the object was in fact deleted
				if (!(component instanceof IOControllerFacade)) {
					assertFalse(component.hasObject(objectId));
				}
				
				// Import object back into the component
				archiveControllerFacade.importObject(component.getUuid(), exportReference);
				
				// Verify that the object now exists again
				assertTrue(component.hasObject(objectId));
				
				// Verify the imported object
				String newHash = component.getHash(objectId);
				assertNotNull(newHash);
				assertFalse(newHash.equals(""));
				assertEquals(originalHash, newHash);
				
				// Tear down
				ioControllerFacade.deleteBinaryObject(exportReference.getComponentHeaderBinaryObjectId());
				ioControllerFacade.deleteBinaryObject(exportReference.getDataBinaryObjectId());
				
				numberOfActualTestRuns++;
			}
		}
		
		assertEquals(numberOfExpectedTestRuns, numberOfActualTestRuns);
	}

	private IOControllerFacade setUpIOController() throws IOException {
		String repositoryPath = "tests/dummy/ioControllerRepository/A/";
		IOControllerFacadeFactory ioControllerFacadeFactory = new IOControllerFacadeFactory();
		ioControllerFacade = ioControllerFacadeFactory.createFacade(repositoryPath);
		
		int[] dataToWrite = {1,55,104};
		String binaryObjectId = BinaryObjectCreator.createBinaryObject(ioControllerFacade, dataToWrite);
		String[] objectIds = {binaryObjectId};
		componentToTestObjectIdsMapping.put(ioControllerFacade.getUuid(), objectIds);
		
		return ioControllerFacade;
	}
	
	private IArchiveControllerFacade setUpArchiveController(IArchiveComponent[] components) {
		ArchiveControllerFacadeFactory archiveControllerFacadeFactory = new ArchiveControllerFacadeFactory();
		IArchiveControllerFacade archiveControllerFacade = archiveControllerFacadeFactory.createFacade(components);
		
		return archiveControllerFacade;
	}
	
	private RevisionControllerFacade setUpRevisionController() throws QueryException {
		RevisionControllerFacadeFactory revisionControllerFacadeFactory = new RevisionControllerFacadeFactory();
		RevisionControllerFacade revisionControllerFacade = revisionControllerFacadeFactory.createFacade(ioControllerFacade);
		
		String[] parentIds = {"foo", "bar"};
		String revisionId = revisionControllerFacade.add(parentIds);
		String[] objectIds = {revisionId};
		componentToTestObjectIdsMapping.put(revisionControllerFacade.getUuid(), objectIds);
		
		return revisionControllerFacade;
	}
	
	private IArchiveComponent setUpUnitController() {
		UnitControllerFacadeFactory unitControllerFacadeFactory = new UnitControllerFacadeFactory();
		UnitControllerFacade unitControllerFacade = unitControllerFacadeFactory.createFacade(ioControllerFacade);
		
		String unitId = unitControllerFacade.createUnit();
		unitControllerFacade.addPointer(unitId, "foo1", "bar1");
		unitControllerFacade.addPointer(unitId, "foo2", "bar2");
		unitControllerFacade.addPointer(unitId, "foo3", "bar3");
		
		String branchId = unitControllerFacade.createBranch(unitId);
		
		String[] objectIds = {unitId, branchId};
		componentToTestObjectIdsMapping.put(unitControllerFacade.getUuid(), objectIds);
		
		return unitControllerFacade;
	}
	
	private MetaDataControllerFacade setUpMetaDataController() {
		MetaDataControllerFacadeFactory metaDataControllerFacadeFactory = new MetaDataControllerFacadeFactory();
		MetaDataControllerFacade metaDataControllerFacade = metaDataControllerFacadeFactory.createFacade(ioControllerFacade);
		
		String metaDataObjectId = metaDataControllerFacade.createBlank();
		metaDataControllerFacade.addValue(metaDataObjectId, "metaFoo1", "metaBar1");
		metaDataControllerFacade.addValue(metaDataObjectId, "metaFoo2", "metaBar2");
		metaDataControllerFacade.addValue(metaDataObjectId, "metaFoo3", "metaBar3");
		
		String[] objectIds = {metaDataObjectId};
		componentToTestObjectIdsMapping.put(metaDataControllerFacade.getUuid(), objectIds);
		
		return metaDataControllerFacade;
	}

}
