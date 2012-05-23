package archivecontroller;

import unitcontroller.Branch;

public class CLIBranchConflictEventListener implements IBranchConflictEventListener {

	@Override
	public void unresolvableConflict(Branch branchA, Branch branchB) {
		System.out.println("Could not resolve conflict between (" + branchA + ") and (" + branchB + ")");
	}

}
