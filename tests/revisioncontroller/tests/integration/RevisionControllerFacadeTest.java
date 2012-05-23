package revisioncontroller.tests.integration;

import static org.junit.Assert.*;

import org.junit.Test;

import archivecontroller.QueryException;

import revisioncontroller.RevisionControllerFacade;

public class RevisionControllerFacadeTest {

	@Test
	public void addTest() throws QueryException {
		// Set up fixture
		String[] parentIds = {"a", "b"};
		RevisionControllerFacade revisionControllerFacade = new RevisionControllerFacade(null);
		
		// Exercise SUT
		String revisionId = revisionControllerFacade.add(parentIds);
		
		// Verify outcome
		assertArrayEquals(parentIds, revisionControllerFacade.getParents(revisionId));
	}

}
