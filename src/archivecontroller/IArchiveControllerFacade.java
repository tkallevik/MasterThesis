package archivecontroller;

import java.io.IOException;

public interface IArchiveControllerFacade {
	
	public void importObject(String componentId, ExportReference exportReference) throws IOException;
	
	public ExportReference exportObject(String componentId, String objectId) throws IOException;

	public void addObjectEventListener(IArchiveObjectEventListener objectEventListener, ObjectReference objectReference);

	public void addBranchConflictEventListener(IBranchConflictEventListener branchConflictEventListener);
}
