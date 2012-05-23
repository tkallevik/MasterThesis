package archivecontroller;

import iocontroller.IIOControllerFacade;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import metadatacontroller.IMetaDataControllerFacade;

import revisioncontroller.IRevisionControllerFacade;

import unitcontroller.Branch;
import unitcontroller.IBranchEventListener;
import unitcontroller.IUnitControllerFacade;
import unitcontroller.Unit;
import unitcontroller.UnitPointer;

public class ArchiveControllerFacade implements IArchiveControllerFacade, IArchiveQuery, IBranchEventListener {
	private HashMap<String, IArchiveComponent> components;
	private HashMap<ObjectReference, IArchiveObjectEventListener> objectToEventListenerMapping;
	private IUnitControllerFacade unitControllerFacade;
	private IRevisionControllerFacade revisionControllerFacade;
	private IMetaDataControllerFacade metaDataControllerFacade;
	private ArrayList<IBranchConflictEventListener> branchConflictEventListeners;
	
	public ArchiveControllerFacade(HashMap<String, IArchiveComponent> components) {
		this.components = components;
		this.objectToEventListenerMapping = new HashMap<ObjectReference, IArchiveObjectEventListener>();
		this.branchConflictEventListeners = new ArrayList<IBranchConflictEventListener>();
		extractComponentReferences();
	}
	
	private void extractComponentReferences() {
		for (IArchiveComponent component : components.values()) {
			if (component instanceof IUnitControllerFacade) {
				unitControllerFacade = (IUnitControllerFacade) component;
			} else if (component instanceof IRevisionControllerFacade) {
				revisionControllerFacade = (IRevisionControllerFacade) component;
			} else if (component instanceof IMetaDataControllerFacade) {
				metaDataControllerFacade = (IMetaDataControllerFacade) component;
			}
		}
	}
	
	@Override
	public void importObject(String componentId, ExportReference exportReference) throws IOException {
		ObjectReference importedObject = components.get(componentId).importObject(exportReference);
		
		for(ObjectReference objectReference : objectToEventListenerMapping.keySet()) {
			if (objectReference.equals(importedObject)) {
				objectToEventListenerMapping.get(objectReference).objectImportCompleted(objectReference);
			}
		}
	}

	@Override
	public ExportReference exportObject(String componentId, String objectId) throws IOException {
		return components.get(componentId).exportObject(objectId);
	}

	@Override
	public ObjectReference[] getObjectReferences(String unitId) throws QueryException {
		String unitControllerComponentId = getComponentId(IUnitControllerFacade.class);
		
		if (!hasObject(unitControllerComponentId, unitId)) {
			return null;
		}
		
		UnitPointer[] unitPointers = unitControllerFacade.getUnitPointers(unitId);
		
		ObjectReference[] objectReferences;
		
		if (unitPointers == null) {
			objectReferences = new ObjectReference[1];
		} else {
			objectReferences = new ObjectReference[unitPointers.length + 1];
			
			for (int i = 0; i < unitPointers.length; i++) {
				UnitPointer unitPointer = unitPointers[i];
				objectReferences[i] = new ObjectReference(unitPointer.getComponentId(), unitPointer.getObjectId());
			}
		} 
		
		objectReferences[objectReferences.length - 1] = new ObjectReference(unitControllerComponentId, unitId);
		
		return objectReferences;
	}

	@Override
	public boolean hasObject(String componentId, String objectId) {
		return components.get(componentId).hasObject(objectId);
	}
	
	@Override
	public String getComponentId(Class<?> c) throws QueryException {
		for (IArchiveComponent component : components.values()) {
			if (c.isInstance(component)) {
				return component.getUuid();
			}
		}
		
		throw new QueryException("Could not locate component " + c);
	}
	
	@Override
	public void addObjectEventListener(IArchiveObjectEventListener objectEventListener, ObjectReference objectReference) {
		objectToEventListenerMapping.put(objectReference, objectEventListener);
	}

	@Override
	public Branch[] getAllBranches() {
		return unitControllerFacade.getAllBranches();
	}

	@Override
	public String[] getUnitIds(Branch[] branches) throws QueryException {
		ArrayList<String> unitIds = new ArrayList<String>();
		
		for (Branch branch : branches) {
			Unit unit = unitControllerFacade.getUnit(branch.getUnitId());
			String revisionControllerComponentId = getComponentId(IRevisionControllerFacade.class);
			String revisionId = unit.getObjectId(revisionControllerComponentId);
			
			String[] revisionIds = revisionControllerFacade.getAncestors(revisionId);
			for (int i = 0; i < revisionIds.length; i++) {
				Unit unitForRevision = unitControllerFacade.getUnit(revisionControllerComponentId, revisionIds[i]);
				unitIds.add(unitForRevision.getId());
			}
		}
		
		String[] stringArray = new String[unitIds.size()];
		return unitIds.toArray(stringArray);
	}

	@Override
	public void conflict(Branch conflictingBranch) {
		Branch originalBranch = unitControllerFacade.getBranch(conflictingBranch.getId());
		
		Unit unitA = unitControllerFacade.getUnit(originalBranch.getUnitId());
		Unit unitB = unitControllerFacade.getUnit(conflictingBranch.getUnitId());
		
		try {
			String revisionIdA = unitA.getObjectId(getComponentId(IRevisionControllerFacade.class));
			String revisionIdB = unitB.getObjectId(getComponentId(IRevisionControllerFacade.class));
			
			String[] ancestorsA = revisionControllerFacade.getAncestors(revisionIdA);
			String[] ancestorsB = revisionControllerFacade.getAncestors(revisionIdB);
		
			for (String ancestorA : ancestorsA) {
				if (ancestorA.equals(revisionIdB)) {
					unitControllerFacade.resolveConflict(originalBranch.getId(), unitA.getId());
					
					return;
				}
			}
			
			for (String ancestorB : ancestorsB) {
				if (ancestorB.equals(revisionIdA)) {
					unitControllerFacade.resolveConflict(originalBranch.getId(), unitB.getId());
					
					return;
				}
			}
			
			notifyBranchConflictEventListeners(conflictingBranch, originalBranch);
		} catch (QueryException e) {
			notifyBranchConflictEventListeners(conflictingBranch, originalBranch);
			e.printStackTrace();
		}
	}

	private void notifyBranchConflictEventListeners(Branch branchA, Branch branchB) {
		for(IBranchConflictEventListener branchConflictEventListener : branchConflictEventListeners) {
			branchConflictEventListener.unresolvableConflict(branchA, branchB);
		}
	}

	@Override
	public boolean isBranch(ObjectReference objectReference) throws QueryException {
		if (objectReference.getComponentId().equals(getComponentId(IUnitControllerFacade.class))) {
			return unitControllerFacade.isBranch(objectReference.getObjectId());
		}
	
		return false;
	}
	
	@Override
	public void addBranchConflictEventListener(IBranchConflictEventListener branchConflictEventListener) {
		branchConflictEventListeners.add(branchConflictEventListener);
	}

	@Override
	public String createBranch() {
		return unitControllerFacade.createBranch(null);
	}

	@Override
	public String createBranch(String unitId) {
		return unitControllerFacade.createBranch(unitId);
	}

	@Override
	public String getBranchBinaryObjectId(String branchId) throws QueryException {
		String unitId = unitControllerFacade.getBranchUnit(branchId);
		Unit unit = unitControllerFacade.getUnit(unitId);
		
		if (unit == null) {
			return null;
		}
		
		return unit.getObjectId(getComponentId(IIOControllerFacade.class));
	}

	@Override
	public void commitBranch(String branchId, String binaryObjectId) throws QueryException {
		String unitId = unitControllerFacade.getBranchUnit(branchId);
		Unit unit = unitControllerFacade.getUnit(unitId);
		
		String revisionId;
		String metaDataObjectId;
		if (unit == null) {
			String[] parentIds = {};
			revisionId = revisionControllerFacade.add(parentIds);
			metaDataObjectId = metaDataControllerFacade.createBlank();
		} else {
			String[] parentIds = {unit.getObjectId(getComponentId(IRevisionControllerFacade.class))};
			revisionId = revisionControllerFacade.add(parentIds);
			
			String oldMetaDataObjectId = unit.getObjectId(getComponentId(IMetaDataControllerFacade.class));
			metaDataObjectId = metaDataControllerFacade.createCopy(oldMetaDataObjectId);
		}
		
		UnitPointer[] unitPointers = {new UnitPointer(getComponentId(IIOControllerFacade.class), binaryObjectId),
									  new UnitPointer(getComponentId(IRevisionControllerFacade.class), revisionId),
									  new UnitPointer(getComponentId(IMetaDataControllerFacade.class), metaDataObjectId)};
		
		String newUnitId = unitControllerFacade.createUnit(unitPointers);
		
		unitControllerFacade.moveBranch(branchId, newUnitId);
	}

	@Override
	public void addMetaDataValue(String branchId, String key, String value) throws QueryException {
		commitBranch(branchId, getBranchBinaryObjectId(branchId));
		
		String unitId = unitControllerFacade.getBranchUnit(branchId);
		Unit unit = unitControllerFacade.getUnit(unitId);
		String metaDataObjectId = unit.getObjectId(getComponentId(IMetaDataControllerFacade.class));
		
		metaDataControllerFacade.addValue(metaDataObjectId, key, value);
	}

	@Override
	public String[] getMetaDataValue(String branchId, String key) throws QueryException {
		String unitId = unitControllerFacade.getBranchUnit(branchId);
		Unit unit = unitControllerFacade.getUnit(unitId);
		String metaDataObjectId = unit.getObjectId(getComponentId(IMetaDataControllerFacade.class));
		
		return metaDataControllerFacade.getValues(metaDataObjectId, key);
	}

	@Override
	public String getUnit(String branchId, int index) throws QueryException {
		String unitId = unitControllerFacade.getBranchUnit(branchId);
		Unit unit = unitControllerFacade.getUnit(unitId);
		String revisionControllerComponentId = getComponentId(IRevisionControllerFacade.class);
		String revisionId = unit.getObjectId(revisionControllerComponentId);
		
		String[] revisionIds = revisionControllerFacade.getAncestors(revisionId);
		Unit unitForRevision = unitControllerFacade.getUnit(revisionControllerComponentId, revisionIds[revisionIds.length - index - 1]);
		
		return unitForRevision.getId();
	}
}
