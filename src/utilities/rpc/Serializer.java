package utilities.rpc;

/**
 * A utility class for serializing various types of objects.
 *
 */
public class Serializer {
	/**
	 * Encoding for the delimiter to use between array elements.
	 */
	protected static final String arrayElementDelimiter = "|";
	
	/**
	 * Serialize a String array.
	 * 
	 * @param array the array to serialize
	 * @return a String representing the array
	 */
	public static String serializeArray(String[] array) {
		String serializedArray = "";
		for (String element : array) {
			serializedArray += element + arrayElementDelimiter;
		}
		serializedArray = serializedArray.substring(0, serializedArray.length() - arrayElementDelimiter.length());
		
		return serializedArray;
	}
	
	/**
	 * De-serialize a String array.
	 * 
	 * @param serializedArray a String representing the array
	 * @return the de-serialized array
	 */
	public static String[] deSerializeArray(String serializedArray) {
		String[] array = serializedArray.split("\\" + arrayElementDelimiter);
		
		return array;
	}
	
	/**
	 * Serialize an int array.
	 * 
	 * @param array the array to serialize
	 * @return a String representing the array
	 */
	public static String serializeIntArray(int[] array) {
		String[] stringArray = new String[array.length];
		for(int i = 0; i < array.length; i++) {
			stringArray[i] = "" + array[i];
		}
		
		return serializeArray(stringArray);
	}

	/**
	 * De-serialize an int array.
	 * 
	 * @param serializedArray a String representing the array
	 * @return the de-serialized array
	 */
	public static int[] deSerializeIntArray(String serializedArray) {
		String[] stringArray = deSerializeArray(serializedArray);
		int[] intArray = new int[stringArray.length];
		for(int i = 0; i < intArray.length; i++) {
			intArray[i] = Integer.parseInt(stringArray[i]);
		}
		
		return intArray;
	}
}
