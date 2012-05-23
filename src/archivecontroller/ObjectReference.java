package archivecontroller;

public class ObjectReference {
	private String componentId;
	private String objectId;

	public ObjectReference() {}
	
	public ObjectReference(String componentId, String objectId) {
		this.componentId = componentId;
		this.objectId = objectId;
	}

	public void setComponentId(String componentId) {
		this.componentId = componentId;
	}

	public void setObjectId(String objectId) {
		this.objectId = objectId;
	}

	public String getComponentId() {
		return componentId;
	}

	public String getObjectId() {
		return objectId;
	}
	
	@Override
	public boolean equals(Object other) {
		if (!(other instanceof ObjectReference)) {
			return false;
		}
		
		ObjectReference otherObjectReference = (ObjectReference) other;
		
		return componentId.equals(otherObjectReference.getComponentId()) && objectId.equals(otherObjectReference.getObjectId());
	}
	
	@Override
	public String toString() {
		return String.format("componentId: %s objectId: %s", componentId, objectId);
	}
}
