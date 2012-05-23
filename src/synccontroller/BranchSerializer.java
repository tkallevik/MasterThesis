package synccontroller;

import unitcontroller.Branch;
import utilities.rpc.ObjectSerializer;

public class BranchSerializer extends ObjectSerializer {
	private Branch branch;
	
	public String serialize(Branch branch) {
		if (branch == null) {
			return nullValue;
		}
		
		String serializedBranch = "branchId" + keyValueDelimiter + branch.getId() + keyValuePairDelimiter;
		serializedBranch += "unitId" + keyValueDelimiter + branch.getUnitId();
		
		return serializedBranch;
	}
	
	public Branch deSerialize(String serializedBranch) {
		if (serializedBranch.equals(nullValue)) {
			return null;
	}
		
		branch = new Branch();
		deSerializeAndCallSetValue(serializedBranch);
		
		return branch;
	}

	@Override
	protected void setValue(String key, String value) {
		if(key.equals("branchId")) {
			branch.setId(value);
		} else if (key.equals("unitId")) {
			branch.setUnitId(value);
		}
	}
}
