package unitcontroller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import utilities.StringBinaryObjectMapper;
import utilities.StringHasher;

import archivecontroller.ExportReference;
import archivecontroller.IArchiveComponent;
import archivecontroller.ObjectReference;
import archivecontroller.QueryException;

public class UnitControllerFacade implements IUnitControllerFacade, IArchiveComponent {
	private final String componentId = "unitController";
	private StringBinaryObjectMapper stringBinaryObjectMapper;
	private HashMap<String, Unit> units;
	private HashMap<String, Branch> branches;
	private HashMap<String, Branch> conflictingBranches;
	private final String SERIALIZATION_FIELD_SEPARATOR = ";";
	private final String SERIALIZATION_POINTER_SEPARATOR = ",";
	private final String SERIALIZATION_POINTER_FIELD_SEPARATOR = " ";
	private ArrayList<IBranchEventListener> branchEventListeners;
	
	public UnitControllerFacade(StringBinaryObjectMapper stringBinaryObjectMapper) {
		this.stringBinaryObjectMapper = stringBinaryObjectMapper;
		this.units = new HashMap<String, Unit>();
		this.branches = new HashMap<String, Branch>();
		this.conflictingBranches = new HashMap<String, Branch>();
		this.branchEventListeners = new ArrayList<IBranchEventListener>();
	}
	
	@Override
	public String createUnit() {
		String unitId = generateId();
		
		Unit unit = new Unit(unitId);
		units.put(unitId, unit);
		
		return unitId;
	}
	
	@Override
	public String createUnit(UnitPointer[] unitPointers) {
		String unitId = generateId();
		
		Unit unit = new Unit(unitId);
		units.put(unitId, unit);
		
		unit.addPointers(unitPointers);
		
		return unitId;
	}
	
	@Override
	public void addPointer(String unitId, String componentId, String objectId) {
		Unit unit = units.get(unitId);
		unit.addPointer(componentId, objectId);
	}

	@Override
	public void removePointer(String unitId, String componentId, String objectId) {
		Unit unit = units.get(unitId);
		unit.removePointer(componentId, objectId);
	}

	@Override
	public String createBranch(String unitId) {
		String branchId = generateId();
		
		Branch branch = new Branch(branchId, unitId);
		branches.put(branchId, branch);
		
		return branchId;
	}

	@Override
	public String getBranchUnit(String branchId) {
		return branches.get(branchId).getUnitId();
	}

	@Override
	public void setBranchUnit(String branchId, String unitId) {
		branches.get(branchId).setUnitId(unitId);
	}

	@Override
	public UnitPointer[] getUnitPointers(String unitId) {
		Unit unit = units.get(unitId);
		
		if (unit == null) {
			return null;
		}
		
		return unit.getPointers();
	}

	private String generateId() {
		String id;
		do {
			id = String.valueOf((int) (Math.random() * 10000));
		} while(units.containsKey(id) || branches.containsKey(id));
		
		return id;
	}

	@Override
	public String getUuid() {
		return componentId;
	}

	@Override
	public boolean hasObject(String objectId) {
		return units.containsKey(objectId) || branches.containsKey(objectId);
	}

	@Override
	public void deleteObject(String objectId) throws IOException {
		units.remove(objectId);
		branches.remove(objectId);
	}

	@Override
	public String getHash(String objectId) throws Exception {
		String serializedObject;
		
		if (units.containsKey(objectId)) {
			serializedObject = serializeUnit(objectId);
		} else if (branches.containsKey(objectId)) {
			serializedObject = serializeBranch(objectId);
		} else {
			throw new Exception("No such unit or branch found.");
		}
		
		return StringHasher.md5Sum(serializedObject);
	}

	@Override
	public ObjectReference importObject(ExportReference exportReference) throws IOException {
		String componentHeader = stringBinaryObjectMapper.readBinaryObjectToString(exportReference.getComponentHeaderBinaryObjectId());
		String serializedObject = stringBinaryObjectMapper.readBinaryObjectToString(exportReference.getDataBinaryObjectId());
		
		if (componentHeader.equals("UNIT")) {
			Unit unit = deserializeUnit(serializedObject);
			units.put(unit.getId(), unit);
			
			return new ObjectReference(getUuid(), unit.getId());
		} else if (componentHeader.equals("BRANCH")) {
			Branch branch = deserializeBranch(serializedObject);
			
			if (branches.containsKey(branch.getId())) {
				Branch existingBranch = branches.get(branch.getId());
				if (!existingBranch.equals(branch)) {
					conflictingBranches.put(branch.getId(), branch);
					
					// Notify listeners
					for (IBranchEventListener branchEventListener : branchEventListeners) {
						branchEventListener.conflict(branch);
					}
				}
			} else {
				branches.put(branch.getId(), branch);
			}
			
			return new ObjectReference(getUuid(), branch.getId());
		}
		
		throw new IOException("Parse error: Unknown object type.");
	}

	@Override
	public ExportReference exportObject(String objectId) throws IOException {
		String componentHeader;
		String serializedObject;
		
		if (units.containsKey(objectId)) {
			componentHeader = "UNIT";
			serializedObject = serializeUnit(objectId);
		} else if (branches.containsKey(objectId)) {
			componentHeader = "BRANCH";
			serializedObject = serializeBranch(objectId);
		} else {
			throw new IOException("No such unit or branch found.");
		}
		
		String componentHeaderBinaryObjectId = stringBinaryObjectMapper.writeStringToBinaryObject(componentHeader);
		String dataBinaryObjectId = stringBinaryObjectMapper.writeStringToBinaryObject(serializedObject);
		
		return new ExportReference(componentHeaderBinaryObjectId, dataBinaryObjectId);
	}

	private String serializeUnit(String unitId) {
		Unit unit = units.get(unitId);
		UnitPointer[] pointers = unit.getPointers();
		
		String serializedUnit = unit.getId() + SERIALIZATION_FIELD_SEPARATOR;
		
		for(UnitPointer pointer : pointers) {
			serializedUnit += pointer.getComponentId() + SERIALIZATION_POINTER_FIELD_SEPARATOR +
							  pointer.getObjectId() + SERIALIZATION_POINTER_SEPARATOR;
		}
		serializedUnit.substring(0, serializedUnit.length() - 1 - SERIALIZATION_POINTER_SEPARATOR.length());
		
		return serializedUnit;
	}

	private Unit deserializeUnit(String serializedUnit) {
		String[] fields = serializedUnit.split(SERIALIZATION_FIELD_SEPARATOR);
		
		Unit unit = new Unit(fields[0]);
		
		String[] serializedUnitPointers = fields[1].split(SERIALIZATION_POINTER_SEPARATOR);
		for (String serializedUnitPointer : serializedUnitPointers) {
			String[] unitPointerFields = serializedUnitPointer.split(SERIALIZATION_POINTER_FIELD_SEPARATOR);
			unit.addPointer(unitPointerFields[0], unitPointerFields[1]);
		}
		
		return unit;
	}
	
	private String serializeBranch(String branchId) {
		Branch branch = branches.get(branchId);
		
		String serializedBranch = branch.getId() + SERIALIZATION_FIELD_SEPARATOR + branch.getUnitId();
		
		return serializedBranch;
	}

	private Branch deserializeBranch(String serializedBranch) {
		String[] fields = serializedBranch.split(SERIALIZATION_FIELD_SEPARATOR);
		
		return new Branch(fields[0], fields[1]);
	}

	@Override
	public Branch[] getAllBranches() {
		Branch[] branchArray = new Branch[branches.size()];
		
		return branches.values().toArray(branchArray);
	}

	@Override
	public Unit getUnit(String unitId) {
		return units.get(unitId);
	}

	@Override
	public Unit getUnit(String componentId, String objectId) throws QueryException {
		for (Unit unit : units.values()) {
			if (unit.hasPointer(componentId, objectId)) {
				return unit;
			}
		}
		
		throw new QueryException("No units have such a pointer.");
	}
	
	@Override
	public void addBranchEventListener(IBranchEventListener branchEventListener) {
		branchEventListeners.add(branchEventListener);
	}

	@Override
	public Branch getBranch(String branchId) {
		return branches.get(branchId);
	}

	@Override
	public void resolveConflict(String branchId, String unitId) {
		conflictingBranches.remove(branchId);
		moveBranch(branchId, unitId);
	}

	@Override
	public void moveBranch(String branchId, String unitId) {
		branches.get(branchId).setUnitId(unitId);
	}

	@Override
	public boolean hasConflicts() {
		return !conflictingBranches.isEmpty();
	}

	@Override
	public boolean isBranch(String objectId) {
		return branches.containsKey(objectId);
	}
}
