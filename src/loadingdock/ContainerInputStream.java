package loadingdock;

import iocontroller.IIOControllerFacade;

import java.io.IOException;
import java.io.InputStream;

public class ContainerInputStream extends InputStream {
	private IIOControllerFacade ioControllerFacade;
	private ContainerHeaderInputStream containerHeaderInputStream;
	private String componentHeaderCheckoutId;
	private int componentHeaderStreamId;
	private String dataCheckoutId;
	private int dataStreamId;
	private Sources currentSource;
	private ContainerHeaderLengthCodec containerHeaderLengthCodec;
	
	private enum Sources {
		CONTAINER_HEADER_LENGTH, CONTAINER_HEADER, COMPONENT_HEADER, DATA
	}
	
	public ContainerInputStream(IIOControllerFacade ioControllerFacade, ContainerHeaderInputStream containerHeaderInputStream, ContainerHeaderLengthCodec containerHeaderLengthCodec) throws IOException {
		this.ioControllerFacade = ioControllerFacade;
		
		this.containerHeaderInputStream = containerHeaderInputStream;
		
		ContainerHeader containerHeader = containerHeaderInputStream.getContainerHeader();
		this.componentHeaderCheckoutId = ioControllerFacade.checkOut(containerHeader.getComponentHeaderBinaryObjectId());
		this.componentHeaderStreamId = ioControllerFacade.openInputStream(componentHeaderCheckoutId);
		
		this.dataCheckoutId = ioControllerFacade.checkOut(containerHeader.getDataBinaryObjectId());
		this.dataStreamId = ioControllerFacade.openInputStream(dataCheckoutId);
		
		this.containerHeaderLengthCodec = containerHeaderLengthCodec;
		long containerHeaderLength = containerHeaderInputStream.getHeaderLength();
		containerHeaderLengthCodec.encodeLong(containerHeaderLength);
		
		this.currentSource = Sources.CONTAINER_HEADER_LENGTH;
	}

	@Override
	public int read() throws IOException {
		int result;
		
		if (currentSource == Sources.CONTAINER_HEADER_LENGTH) {
			result = containerHeaderLengthCodec.read();
			
			if (result == -1) {
				currentSource = Sources.CONTAINER_HEADER;
				result = read();
			}
			
			return result;
		} else if (currentSource == Sources.CONTAINER_HEADER) {
			result = containerHeaderInputStream.read();
			
			if (result == -1) {
				currentSource = Sources.COMPONENT_HEADER;
				result = read();
			}
			
			return result;
		} else if (currentSource == Sources.COMPONENT_HEADER) {
			result = ioControllerFacade.read(componentHeaderStreamId);
			
			if (result == -1) {
				currentSource = Sources.DATA;
				result = read();
			}
			
			return result;
		} else if (currentSource == Sources.DATA) {
			result = ioControllerFacade.read(dataStreamId);
			
			return result;
		}
		
		throw new IOException("Internal state error.");
	}
	
	@Override
	public void close() throws IOException {
		containerHeaderInputStream.close();
		ioControllerFacade.closeInputStream(componentHeaderStreamId);
		ioControllerFacade.closeInputStream(dataStreamId);
		
		ioControllerFacade.commit(componentHeaderCheckoutId);
		ioControllerFacade.commit(dataCheckoutId);
	}
	
	public ContainerHeader getContainerHeader() {
		return containerHeaderInputStream.getContainerHeader();
	}
}
