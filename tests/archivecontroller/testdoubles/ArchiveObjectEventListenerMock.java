package archivecontroller.testdoubles;

import java.util.ArrayList;

import archivecontroller.ArchiveControllerFacade;
import archivecontroller.IArchiveControllerFacade;
import archivecontroller.IArchiveObjectEventListener;
import archivecontroller.ObjectReference;

public class ArchiveObjectEventListenerMock implements IArchiveObjectEventListener {
	public IArchiveControllerFacade archiveControllerFacade;
	public ArrayList<ObjectReference> objectReferences;
	
	public ArchiveObjectEventListenerMock(ArchiveControllerFacade archiveControllerFacade) {
		this.archiveControllerFacade = archiveControllerFacade;
		this.objectReferences = new ArrayList<ObjectReference>();
	}
	
	public void addObjectReferences(ObjectReference[] objectReferences) {
		for (ObjectReference objectReference : objectReferences) {
			this.objectReferences.add(objectReference);
			archiveControllerFacade.addObjectEventListener(this, objectReference);
		}
	}

	@Override
	public synchronized void objectImportCompleted(ObjectReference objectReference) {
		objectReferences.remove(objectReference);
		
		if (objectReferences.isEmpty()) {
			notifyAll();
		}
	}
	
	public void waitForImports() {
		if (objectReferences.isEmpty()) {
			return;
		}
		
		try {
			wait();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
