package unitcontroller;

import archivecontroller.QueryException;

public interface IUnitControllerFacade {

	public String createUnit();

	public void addPointer(String unitId, String componentId, String objectId);

	public void removePointer(String unitId, String componentId, String objectId);

	public String createBranch(String unitId);

	public String getBranchUnit(String branchId);

	public void setBranchUnit(String branchId, String unitId);

	public UnitPointer[] getUnitPointers(String unitId);

	public Branch[] getAllBranches();

	public Unit getUnit(String unitId);

	public Unit getUnit(String componentId, String objectId) throws QueryException;

	public Branch getBranch(String id);

	public void resolveConflict(String branchId, String unitId);

	public void moveBranch(String branchId, String unitId);

	public boolean hasConflicts();

	public boolean isBranch(String objectId);

	public String createUnit(UnitPointer[] unitPointers);

	public void addBranchEventListener(IBranchEventListener branchEventListener);

}
