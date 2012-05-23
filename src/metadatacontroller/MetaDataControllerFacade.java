package metadatacontroller;

import java.io.IOException;
import java.util.HashMap;

import utilities.StringBinaryObjectMapper;
import utilities.StringHasher;

import archivecontroller.ExportReference;
import archivecontroller.IArchiveComponent;
import archivecontroller.ObjectReference;

public class MetaDataControllerFacade implements IMetaDataControllerFacade, IArchiveComponent {
	private final String componentId = "metaDataController";
	private StringBinaryObjectMapper stringBinaryObjectMapper;
	private HashMap<String, MetaDataObject> metaDataObjects;
	private final String SERIALIZATION_FIELD_SEPARATOR = ";";
	private final String SERIALIZATION_PAIR_SEPARATOR = ",";
	private final String SERIALIZATION_PAIR_FIELD_SEPARATOR = " ";
	
	public MetaDataControllerFacade(StringBinaryObjectMapper stringBinaryObjectMapper) {
		this.stringBinaryObjectMapper = stringBinaryObjectMapper;
		metaDataObjects = new HashMap<String, MetaDataObject>();
	}
	
	@Override
	public String createBlank() {
		String metaDataObjectId = generateMetaDataObjectId();
		MetaDataObject metaDataObject = new MetaDataObject(metaDataObjectId);
		metaDataObjects.put(metaDataObjectId, metaDataObject);
		
		return metaDataObjectId;
	}
	
	@Override
	public String createCopy(String originalMetaDataObjectId) {
		String metaDataObjectId = generateMetaDataObjectId();
		MetaDataObject metaDataObject = metaDataObjects.get(originalMetaDataObjectId).clone(metaDataObjectId);
		metaDataObjects.put(metaDataObjectId, metaDataObject);
		
		return metaDataObjectId;
	}
	
	@Override
	public void addValue(String metaDataObjectId, String key, String value) {
		metaDataObjects.get(metaDataObjectId).addValue(key, value);
	}
	
	@Override
	public String[] getKeys(String metaDataObjectId) {
		return metaDataObjects.get(metaDataObjectId).getKeys();
	}
	
	@Override
	public String[] getValues(String metaDataObjectId, String key) {
		return metaDataObjects.get(metaDataObjectId).getValues(key);
	}
	
	private String generateMetaDataObjectId() {
		// Decide on meta data object id
		String metaDataObjectId;
		do {
			metaDataObjectId = String.valueOf((int) (Math.random() * 10000));
		} while(metaDataObjects.containsKey(metaDataObjectId));
		
		return metaDataObjectId;
	}

	@Override
	public String getUuid() {
		return componentId;
	}

	@Override
	public ObjectReference importObject(ExportReference exportReference) throws IOException {
		@SuppressWarnings("unused")
		String componentHeader = stringBinaryObjectMapper.readBinaryObjectToString(exportReference.getComponentHeaderBinaryObjectId());
		String serializedMetaDataObject = stringBinaryObjectMapper.readBinaryObjectToString(exportReference.getDataBinaryObjectId());
		
		MetaDataObject metaDataObject = deserialize(serializedMetaDataObject);
		metaDataObjects.put(metaDataObject.getId(), metaDataObject);
		
		return new ObjectReference(getUuid(), metaDataObject.getId());
	}

	@Override
	public ExportReference exportObject(String metaDataObjectId) throws IOException {
		String componentHeader = "";
		String componentHeaderBinaryObjectId = stringBinaryObjectMapper.writeStringToBinaryObject(componentHeader);
		
		String serializedMetaDataObject = serialize(metaDataObjectId);
		String dataBinaryObjectId = stringBinaryObjectMapper.writeStringToBinaryObject(serializedMetaDataObject);
		
		return new ExportReference(componentHeaderBinaryObjectId, dataBinaryObjectId);
	}

	private String serialize(String metaDataObjectId) {
		MetaDataObject metaDataObject = metaDataObjects.get(metaDataObjectId);
		String[] keys = metaDataObject.getKeys();
		
		String serializedMetaDataObject = metaDataObject.getId() + SERIALIZATION_FIELD_SEPARATOR;
		
		for(String key : keys) {
			for (String value : metaDataObject.getValues(key)) {
				serializedMetaDataObject += key + SERIALIZATION_PAIR_FIELD_SEPARATOR +
											value + SERIALIZATION_PAIR_SEPARATOR;
			}
		}
		serializedMetaDataObject.substring(0, serializedMetaDataObject.length() - 1 - SERIALIZATION_PAIR_SEPARATOR.length());
		
		return serializedMetaDataObject;
	}

	private MetaDataObject deserialize(String serializedMetaDataObject) {
		String[] fields = serializedMetaDataObject.split(SERIALIZATION_FIELD_SEPARATOR);
		
		MetaDataObject metaDataObject = new MetaDataObject(fields[0]);
		
		if (fields.length > 1) {
			String[] serializedKeyValuePairs = fields[1].split(SERIALIZATION_PAIR_SEPARATOR);
			for (String serializedKeyValuePair : serializedKeyValuePairs) {
				String[] keyValuePair = serializedKeyValuePair.split(SERIALIZATION_PAIR_FIELD_SEPARATOR);
				metaDataObject.addValue(keyValuePair[0], keyValuePair[1]);
			}
		}
		
		return metaDataObject;
	}

	@Override
	public boolean hasObject(String metaDataObjectId) {
		return metaDataObjects.containsKey(metaDataObjectId);
	}

	@Override
	public void deleteObject(String metaDataObjectId) throws IOException {
		metaDataObjects.remove(metaDataObjectId);
	}

	@Override
	public String getHash(String metaDataObjectId) throws Exception {
		return StringHasher.md5Sum(serialize(metaDataObjectId));
	}
}
