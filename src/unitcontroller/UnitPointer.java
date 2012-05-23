package unitcontroller;

public class UnitPointer {
	private String componentId;
	private String objectId;

	public UnitPointer(String componentId, String objectId) {
		this.componentId = componentId;
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
		if (!(other instanceof UnitPointer)) {
			return false;
		}
		
		UnitPointer otherUnitPointer = (UnitPointer) other;
		
		return componentId.equals(otherUnitPointer.getComponentId()) && objectId.equals(otherUnitPointer.getObjectId());
	}
}
