package utilities.rpc;

/**
 * This class is meant to be extended by various specialized object serializers.
 *
 */
public abstract class ObjectSerializer {
	/**
	 * The encoding for the delimiter between key-value pairs.
	 */
	protected String keyValuePairDelimiter = ";";
	
	/**
	 * The encoding for the delimiter between keys and values in a key-value pair
	 */
	protected String keyValueDelimiter = "=";
	
	/**
	 * The encoding for null values
	 */
	protected String nullValue = "NULL";
	
	/**
	 * De-serialize into key-value pairs, and call the setValue method with these as arguments.
	 * 
	 * @param serializedObject a String representing the serialized object
	 */
	protected void deSerializeAndCallSetValue(String serializedObject) {
		try {
			String[] keyValuePairs = serializedObject.split(keyValuePairDelimiter);
			for(String encodedKeyValuePair : keyValuePairs) {
				String[] keyValuePair = encodedKeyValuePair.split(keyValueDelimiter);
				String key = keyValuePair[0];
				String value = keyValuePair[1];
				setValue(key, value);
			}
		} catch(ArrayIndexOutOfBoundsException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Set a value on the model stored as an attribute, given the key.
	 * 
	 * @param key the key to use
	 * @param value the value to set
	 */
	protected abstract void setValue(String key, String value); 
}
