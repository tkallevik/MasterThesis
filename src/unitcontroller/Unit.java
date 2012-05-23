package unitcontroller;

import archivecontroller.QueryException;
import utilities.ArrayList;

public class Unit {
	private String id;
	private ArrayList<UnitPointer> pointers;
	
	public Unit(String id) {
		this.id = id;
		this.pointers = new ArrayList<UnitPointer>();
	}
	
	public String getId() {
		return id;
	}
	
	public UnitPointer[] getPointers() {
		UnitPointer[] result = new UnitPointer[pointers.size()];
		return pointers.toArray(result);
	}
	
	public void addPointer(String componentId, String objectId) {
		UnitPointer unitPointer = new UnitPointer(componentId, objectId);
		pointers.add(unitPointer);
	}

	public void addPointers(UnitPointer[] unitPointers) {
		for (UnitPointer unitPointer : unitPointers) {
			pointers.add(unitPointer);
		}
	}

	public void removePointer(String componentId, String objectId) {
		for(int i = 0; i < pointers.size(); i++) {
			UnitPointer pointer = pointers.get(i);
			if (pointer.getComponentId().equals(componentId) && pointer.getObjectId().equals(objectId)) {
				pointers.remove(i);
			}
		}
	}

	public String getObjectId(String componentId) throws QueryException {
		for (UnitPointer pointer : pointers) {
			if (pointer.getComponentId().equals(componentId)) {
				return pointer.getObjectId();
			}
		}
		
		throw new QueryException("Unit did not have a pointer to any objects of the specified component.");
	}

	public boolean hasPointer(String componentId, String objectId) {		
		for (UnitPointer pointer : pointers) {
			if (pointer.getComponentId().equals(componentId) && pointer.getObjectId().equals(objectId)) {
				return true;
			}
		}
		
		return false;
	}
}
