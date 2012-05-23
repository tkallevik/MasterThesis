package metadatacontroller;

import iocontroller.IIOControllerFacade;
import utilities.StringBinaryObjectMapper;

public class MetaDataControllerFacadeFactory {
	public MetaDataControllerFacade createFacade(IIOControllerFacade ioControllerFacade) {
		StringBinaryObjectMapper stringBinaryObjectMapper = new StringBinaryObjectMapper(ioControllerFacade);
		
		return new MetaDataControllerFacade(stringBinaryObjectMapper);
	}
}
