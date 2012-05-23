package archivecontroller;

import unitcontroller.Branch;

public interface IBranchConflictEventListener {
	public void unresolvableConflict(Branch branchA, Branch branchB);
}
