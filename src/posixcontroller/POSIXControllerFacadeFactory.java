package posixcontroller;

import archivecontroller.IArchiveQuery;
import iocontroller.IIOControllerFacade;

public class POSIXControllerFacadeFactory {
	public POSIXControllerFacade createFacade(IIOControllerFacade ioControllerFacade, IArchiveQuery archiveQuery) {
		return new POSIXControllerFacade(ioControllerFacade, archiveQuery);
	}
}
