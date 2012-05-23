package revisioncontroller;

import java.util.ArrayList;

import archivecontroller.QueryException;

public class Revision {
	private String id;
	private ArrayList<String> parentIds;
	
	public Revision(String id) {
		this.id = id;
		this.parentIds = new ArrayList<String>();
	}
	
	public String getId() {
		return id;
	}

	public void addParent(String parentId) {
		parentIds.add(parentId);
	}
	
	public void addParents(String[] parentIds) throws QueryException {
		if (parentIds != null) {
			for(String parentId : parentIds) {
				if (parentId == null) {
					throw new QueryException("Cannot add null as parent.");
				}
				
				this.addParent(parentId);
			}
		}
	}
	
	public String[] getParents() {		
		return parentIds.toArray(new String[parentIds.size()]);
	}
}
