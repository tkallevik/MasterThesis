package metadatacontroller.tests.integration;

import static org.junit.Assert.*;

import metadatacontroller.MetaDataControllerFacade;

import org.junit.Test;

public class MetaDataControllerFacadeTest {

	@Test
	public void createTest() {
		// Set up fixture
		String key = "key";
		String[] values = {"1", "2", "3"};
		MetaDataControllerFacade metaDataControllerFacade = new MetaDataControllerFacade(null);
		
		// Exercise SUT (createBlank)
		String metaDataObjectId = metaDataControllerFacade.createBlank();
		metaDataControllerFacade.addValue(metaDataObjectId, key, values[0]);
		metaDataControllerFacade.addValue(metaDataObjectId, key, values[1]);
		metaDataControllerFacade.addValue(metaDataObjectId, key, values[2]);
		metaDataControllerFacade.addValue(metaDataObjectId, "differentKey", "unwantedValue");
		String[] returnedValues = metaDataControllerFacade.getValues(metaDataObjectId, key);
		
		// Verify outcome (createBlank)
		assertArrayEquals(values, returnedValues);
		
		// Set up fixture (createCopy)
		String key2 = "key2";
		String[] values2 = {"a", "b"};
		
		// Exercise SUT (createCopy)
		String copyMetaDataObjectId = metaDataControllerFacade.createCopy(metaDataObjectId);
		returnedValues = metaDataControllerFacade.getValues(copyMetaDataObjectId, key);
		metaDataControllerFacade.addValue(copyMetaDataObjectId, key2, values2[0]);
		metaDataControllerFacade.addValue(copyMetaDataObjectId, key2, values2[1]);
		String[] returnedValues2 = metaDataControllerFacade.getValues(copyMetaDataObjectId, key2);
		
		// Verify outcome (createCopy)
		assertArrayEquals(values, returnedValues);
		assertArrayEquals(values2, returnedValues2);
		
		// Verify that the new values was not written to the original object
		returnedValues2 = metaDataControllerFacade.getValues(metaDataObjectId, key2);
		assertArrayEquals(new String[0], returnedValues2);
	}

}
