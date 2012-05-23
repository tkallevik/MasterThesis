package unitcontroller;

import utilities.StringBinaryObjectMapper;
import iocontroller.IIOControllerFacade;

public class UnitControllerFacadeFactory {
	public UnitControllerFacade createFacade(IIOControllerFacade ioControllerFacade) {
		StringBinaryObjectMapper stringBinaryObjectMapper = new StringBinaryObjectMapper(ioControllerFacade);
		
		return new UnitControllerFacade(stringBinaryObjectMapper);
	}
}
