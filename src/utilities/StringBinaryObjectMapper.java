package utilities;

import iocontroller.IIOControllerFacade;

import java.io.IOException;

public class StringBinaryObjectMapper {
	private IIOControllerFacade ioControllerFacade;
	
	public StringBinaryObjectMapper(IIOControllerFacade ioControllerFacade) {
		this.ioControllerFacade = ioControllerFacade; 
	}
	
	public StringBinaryObjectMapper() {}

	public void setIOControllerFacade(IIOControllerFacade ioControllerFacade) {
		this.ioControllerFacade = ioControllerFacade;
	}

	public String writeStringToBinaryObject(String string) throws IOException {
		char[] data = string.toCharArray();
		
		String checkoutId = ioControllerFacade.checkOut();
		int streamId = ioControllerFacade.openOutputStream(checkoutId);
		
		for (int b : data) {
			ioControllerFacade.write(streamId, b);
		}
		
		ioControllerFacade.closeOutputStream(streamId);
		
		return ioControllerFacade.commit(checkoutId);
	}
	
	public String readBinaryObjectToString(String binaryObjectId) throws IOException {
		String checkoutId = ioControllerFacade.checkOut(binaryObjectId);
		int streamId = ioControllerFacade.openInputStream(checkoutId);
		
		StringBuilder stringBuilder = new StringBuilder();
		
		int b;
		do {
			b = ioControllerFacade.read(streamId);
			
			if (b != -1) {
				stringBuilder.append((char) b);
			}
		} while(b != -1);
		
		ioControllerFacade.commit(checkoutId);
		
		return stringBuilder.toString();
	}
}
