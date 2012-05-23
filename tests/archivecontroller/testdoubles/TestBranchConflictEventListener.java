package archivecontroller.testdoubles;

import java.util.ArrayList;

import unitcontroller.Branch;
import archivecontroller.IBranchConflictEventListener;

public class TestBranchConflictEventListener implements IBranchConflictEventListener {
	private ArrayList<String> conflictingBranches = new ArrayList<String>();

	@Override
	public void unresolvableConflict(Branch branchA, Branch branchB) {
		conflictingBranches.add(branchA.getId());
	}

	public boolean hasConflict(String branchId) {
		return conflictingBranches.contains(branchId);
	}
}
