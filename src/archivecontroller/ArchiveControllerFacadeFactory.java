package archivecontroller;

import java.util.HashMap;

public class ArchiveControllerFacadeFactory {
	public ArchiveControllerFacade createFacade(IArchiveComponent[] components) {
		HashMap<String, IArchiveComponent> componentMap = new HashMap<String, IArchiveComponent>();
		for (IArchiveComponent component : components) {
			componentMap.put(component.getUuid(), component);
		}
		
		ArchiveControllerFacade archiveControllerFacade = new ArchiveControllerFacade(componentMap);
		
		return archiveControllerFacade;
	}
}
