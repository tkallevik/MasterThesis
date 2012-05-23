package archivecontroller;

import unitcontroller.Branch;

public interface IArchiveQuery {
	public ObjectReference[] getObjectReferences(String unitId) throws QueryException;

	boolean hasObject(String componentId, String objectId);

	public Branch[] getAllBranches();

	public String getComponentId(Class<?> c) throws QueryException;

	public String[] getUnitIds(Branch[] branches) throws QueryException;

	public boolean isBranch(ObjectReference objectReference) throws QueryException;

	public String createBranch();

	public String createBranch(String unitId);

	public String getBranchBinaryObjectId(String branchId) throws QueryException;

	public void commitBranch(String branchId, String binaryObjectId) throws QueryException;

	public void addMetaDataValue(String branchId, String key, String value) throws QueryException;

	public String[] getMetaDataValue(String branchId, String key) throws QueryException;

	public String getUnit(String branchId, int index) throws QueryException;
}
