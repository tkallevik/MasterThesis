package unitcontroller;

public class Branch {
	private String id;
	private String unitId;

	public Branch() {}
	
	public Branch(String id, String unitId) {
		this.id = id;
		this.unitId = unitId;
	}

	public String getId() {
		return id;
	}

	public void setId(String branchId) {
		this.id = branchId;
	}
	
	public String getUnitId() {
		return unitId;
	}

	public void setUnitId(String unitId) {
		this.unitId = unitId;
	}
	
	@Override
	public boolean equals(Object other) {
		if (!(other instanceof Branch)) {
			return false;
		}
		
		Branch otherBranch = (Branch) other;
		
		return id.equals(otherBranch.getId()) && unitId.equals(otherBranch.getUnitId());
	}
	
	@Override
	public String toString() {
		return String.format("branchId: %s unitId: %s", id, unitId);
	}
}
