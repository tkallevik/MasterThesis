package fluxcapacitor;

import archivecontroller.ObjectReference;
import utilities.rpc.ObjectSerializer;

/**
 * This class is used for (de)serializing ObjectReferences.
 *
 */
public class ObjectReferenceSerializer extends ObjectSerializer {
	private ObjectReference objectReference;
	
	public String serialize(ObjectReference objectReference) {
		if (objectReference == null) {
			return nullValue;
		}
		
		String serializedObjectReference = "componentId" + keyValueDelimiter + objectReference.getComponentId() + keyValuePairDelimiter;
		serializedObjectReference += "objectId" + keyValueDelimiter + objectReference.getObjectId();
		
		return serializedObjectReference;
	}

	public String[] serialize(ObjectReference[] objectReferences) {
		String[] serializedObjectReferences = new String[objectReferences.length];
		
		for (int i = 0; i < serializedObjectReferences.length; i++) {
			serializedObjectReferences[i] = serialize(objectReferences[i]);
		}
		
		return serializedObjectReferences;
	}
	
	public ObjectReference deSerialize(String serializedObjectReference) {
		if (serializedObjectReference.equals(nullValue)) {
			return null;
	}
		
		objectReference = new ObjectReference();
		deSerializeAndCallSetValue(serializedObjectReference);
		
		return objectReference;
	}

	public ObjectReference[] deSerialize(String[] serializedObjectReferences) {
		ObjectReference[] objectReferences = new ObjectReference[serializedObjectReferences.length];
		
		for (int i = 0; i < objectReferences.length; i++) {
			objectReferences[i] = deSerialize(serializedObjectReferences[i]);
		}
		
		return objectReferences;
	}

	@Override
	protected void setValue(String key, String value) {
		if(key.equals("componentId")) {
			objectReference.setComponentId(value);
		} else if (key.equals("objectId")) {
			objectReference.setObjectId(value);
		}
	}
}