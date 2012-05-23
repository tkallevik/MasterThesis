package utilities.tests;

import static org.junit.Assert.*;

import org.junit.Test;

import utilities.rpc.Serializer;

public class SerializerTest {

	/**
	 * Use the Serializer to serialize and de-serialize a String array, and verify that it is the same after as before.
	 */
	@Test
	public void stringArrayTest() {
		// Set up fixture
		String[] array = {"foo", "bar"};
		
		// Exercise SUT
		String serializedArray = Serializer.serializeArray(array);
		String[] deSerializedArray = Serializer.deSerializeArray(serializedArray);
		
		// Verify outcome
		assertArrayEquals(array, deSerializedArray);
	}
	
	/**
	 * Use the Serializer to serialize and de-serialize a int array, and verify that it is the same after as before.
	 */
	@Test
	public void intArrayTest() {
		// Set up fixture
		int[] array = {1, 5};
		
		// Exercise SUT
		String serializedArray = Serializer.serializeIntArray(array);
		int[] deSerializedArray = Serializer.deSerializeIntArray(serializedArray);
		
		// Verify outcome
		assertArrayEquals(array, deSerializedArray);
	}

}
