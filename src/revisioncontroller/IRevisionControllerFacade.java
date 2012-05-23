package revisioncontroller;

import archivecontroller.QueryException;

public interface IRevisionControllerFacade {

	public String add(String[] parentIds) throws QueryException;

	public String[] getParents(String revisionId) throws QueryException;

	public String[] getAncestors(String revisionId) throws QueryException;

}
