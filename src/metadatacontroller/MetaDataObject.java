package metadatacontroller;

import utilities.ArrayList;

public class MetaDataObject {
	private String id;
	private ArrayList<String> keys;
	private ArrayList<String> values;
	
	public MetaDataObject(String id) {
		this.id = id;
		this.keys = new ArrayList<String>();
		this.values = new ArrayList<String>();
	}
	
	public String getId() {
		return id;
	}
	
	public void addValue(String key, String value) {
		keys.add(key);
		values.add(value);
	}
	
	public String[] getKeys() {
		String[] keysArray = new String[keys.size()];
		
		return keys.toArray(keysArray);
	}
	
	public String[] getValues(String key) {
		Integer[] indexes = keys.indexesOf(key);
		String[] result = new String[indexes.length];
		
		return values.valuesOfIndexes(indexes, result);
	}

	@SuppressWarnings("unchecked")
	public MetaDataObject clone(String metaDataObjectId) {
		MetaDataObject metaDataObject = new MetaDataObject(metaDataObjectId);
		metaDataObject.keys = (ArrayList<String>) keys.clone();
		metaDataObject.values = (ArrayList<String>) values.clone();
		
		return metaDataObject;
	}
}
