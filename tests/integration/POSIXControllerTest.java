package integration;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import iocontroller.IOControllerFacade;
import iocontroller.IOControllerFacadeFactory;

import metadatacontroller.MetaDataControllerFacade;
import metadatacontroller.MetaDataControllerFacadeFactory;

import org.junit.After;
import org.junit.Test;

import archivecontroller.ArchiveControllerFacade;
import archivecontroller.ArchiveControllerFacadeFactory;
import archivecontroller.IArchiveComponent;
import archivecontroller.QueryException;

import posixcontroller.IPOSIXControllerFacade;
import posixcontroller.POSIXControllerFacadeFactory;
import revisioncontroller.RevisionControllerFacade;
import revisioncontroller.RevisionControllerFacadeFactory;
import testutilities.FileOperations;
import unitcontroller.UnitControllerFacade;
import unitcontroller.UnitControllerFacadeFactory;

public class POSIXControllerTest {
	private UnitControllerFacade unitControllerFacade;
	private String repositoryPath = "tests/dummy/ioControllerRepository/A/";

	@Test
	public void test() throws IOException, QueryException {
		// Set up fixture
		IPOSIXControllerFacade posixControllerFacade = setUp();
		
		// Exercise create()
		String branchId = posixControllerFacade.create();
		
		// Exercise openOutputStream which verifies create() and set up for write()
		int outputStreamId = posixControllerFacade.openOutputStream(branchId);
		
		// Exercise write() which verifies openOutputStream()
		int data = 23;
		posixControllerFacade.write(outputStreamId, data);
		
		// Exercise openInputStream() which set up for read()
		int inputStreamId = posixControllerFacade.openInputStream(branchId);
		
		// Exercise read() which verifies openInputStream()
		int result = posixControllerFacade.read(inputStreamId);
		
		// Verify read()
		assertEquals(data, result);
		
		// Close input and output streams
		posixControllerFacade.closeInputStream(inputStreamId);
		posixControllerFacade.closeOutputStream(outputStreamId);
		
		// Exercise commit()
		posixControllerFacade.commit(branchId);
		
		// Verify commit()
		// Verifies that the branch contains the original data
		inputStreamId = posixControllerFacade.openInputStream(branchId);
		result = posixControllerFacade.read(inputStreamId);
		posixControllerFacade.closeInputStream(inputStreamId);
		assertEquals(data, result);
		assertNotNull(unitControllerFacade.getBranchUnit(branchId));
	}
	
	public IPOSIXControllerFacade setUp() {
		int componentIndex = 0;
		IArchiveComponent[] components = new IArchiveComponent[4];
		
		// IO Controller
		IOControllerFacadeFactory ioControllerFacadeFactory = new IOControllerFacadeFactory();
		IOControllerFacade ioControllerFacade = ioControllerFacadeFactory.createFacade(repositoryPath);
		components[componentIndex++] = ioControllerFacade;
		
		// Unit Controller
		UnitControllerFacadeFactory unitControllerFacadeFactory = new UnitControllerFacadeFactory();
		unitControllerFacade = unitControllerFacadeFactory.createFacade(ioControllerFacade);
		components[componentIndex++] = unitControllerFacade;
		
		// Meta Data Controller
		MetaDataControllerFacadeFactory metaDataControllerFacadeFactory = new MetaDataControllerFacadeFactory();
		MetaDataControllerFacade metaDataControllerFacade = metaDataControllerFacadeFactory.createFacade(ioControllerFacade);
		components[componentIndex++] = metaDataControllerFacade;
		
		// Revision Controller
		RevisionControllerFacadeFactory revisionControllerFacadeFactory = new RevisionControllerFacadeFactory();
		RevisionControllerFacade revisionControllerFacade = revisionControllerFacadeFactory.createFacade(ioControllerFacade);
		components[componentIndex++] = revisionControllerFacade;
		
		ArchiveControllerFacadeFactory archiveControllerFacadeFactory = new ArchiveControllerFacadeFactory();
		ArchiveControllerFacade archiveControllerFacade = archiveControllerFacadeFactory.createFacade(components);
		
		POSIXControllerFacadeFactory posixControllerFacadeFactory = new POSIXControllerFacadeFactory();
		
		return posixControllerFacadeFactory.createFacade(ioControllerFacade, archiveControllerFacade);
	}
	
	@After
	public void tearDown() {
		FileOperations.emptyFolder(new File(repositoryPath));
	}

}
