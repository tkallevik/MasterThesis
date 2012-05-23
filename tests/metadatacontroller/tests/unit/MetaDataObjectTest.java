package metadatacontroller.tests.unit;

import static org.junit.Assert.*;

import metadatacontroller.MetaDataObject;

import org.junit.Test;

public class MetaDataObjectTest {

	@Test
	public void addGetTest() {
		// Set up fixture
		String id = "abc";
		String[] values = {"value1", "value2", "value3"}; 
		MetaDataObject metaDataObject = new MetaDataObject(id);
		
		// Exercise SUT
		metaDataObject.addValue("key", values[0]);
		metaDataObject.addValue("key", values[1]);
		metaDataObject.addValue("key", values[2]);
		String[] returnedValues = metaDataObject.getValues("key");
		
		// Verify outcome
		assertArrayEquals(values, returnedValues);
	}
}
