package revisioncontroller;

import utilities.StringBinaryObjectMapper;
import iocontroller.IIOControllerFacade;

public class RevisionControllerFacadeFactory {
	public RevisionControllerFacade createFacade(IIOControllerFacade ioControllerFacade) {
		StringBinaryObjectMapper stringBinaryObjectMapper = new StringBinaryObjectMapper(ioControllerFacade);
		
		return new RevisionControllerFacade(stringBinaryObjectMapper);
	}
}
