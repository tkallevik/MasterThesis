package iocontroller.utilities;

import iocontroller.IIOControllerFacade;

import java.io.IOException;

public class BinaryObjectCreator {
	
	public static String createBinaryObject(IIOControllerFacade ioControllerFacade, int[] dataToWrite) throws IOException {
		String componentHeaderCheckoutId = ioControllerFacade.checkOut();
		
		int componentHeaderStreamId = ioControllerFacade.openOutputStream(componentHeaderCheckoutId);
		for (int b : dataToWrite) {
			ioControllerFacade.write(componentHeaderStreamId, b);
		}
		
		ioControllerFacade.closeOutputStream(componentHeaderStreamId);
		
		return ioControllerFacade.commit(componentHeaderCheckoutId);
	}
}
