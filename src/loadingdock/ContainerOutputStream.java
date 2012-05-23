package loadingdock;

import iocontroller.FileDeletionException;
import iocontroller.IIOControllerFacade;

import java.io.IOException;
import java.io.OutputStream;

public class ContainerOutputStream extends OutputStream {
	private IIOControllerFacade ioControllerFacade;
	private ContainerHeaderOutputStream containerHeaderOutputStream;
	private ContainerHeaderLengthCodec containerHeaderLengthCodec;
	private long containerHeaderLength;
	private String componentHeaderCheckoutId;
	private int componentHeaderStreamId;
	private String dataCheckoutId;
	private int dataStreamId;
	private Destinations currentDestination;
	private long numberOfBytesWrittenToContainerHeader, numberOfBytesWrittenToComponentHeader, numberOfBytesWrittenToData;
	private ContainerHeader containerHeader;
	
	private enum Destinations {
		CONTAINER_HEADER_LENGTH, CONTAINER_HEADER, COMPONENT_HEADER, DATA
	}
	
	public ContainerOutputStream(IIOControllerFacade ioControllerFacade, ContainerHeaderOutputStream containerHeaderOutputStream, 
			ContainerHeaderLengthCodec containerHeaderLengthCodec) throws IOException {
		this.ioControllerFacade = ioControllerFacade;
		
		this.containerHeaderOutputStream = containerHeaderOutputStream;
		this.containerHeaderLengthCodec = containerHeaderLengthCodec;
		
		this.componentHeaderCheckoutId = ioControllerFacade.checkOut();
		this.componentHeaderStreamId = ioControllerFacade.openOutputStream(componentHeaderCheckoutId);
		
		this.dataCheckoutId = ioControllerFacade.checkOut();
		this.dataStreamId = ioControllerFacade.openOutputStream(dataCheckoutId);
		
		this.currentDestination = Destinations.CONTAINER_HEADER_LENGTH;
		
		this.numberOfBytesWrittenToContainerHeader = 0;
		this.numberOfBytesWrittenToComponentHeader = 0;
		this.numberOfBytesWrittenToData = 0;
	}

	@Override
	public void write(int b) throws IOException {
		if (currentDestination == Destinations.CONTAINER_HEADER_LENGTH) {
			writeContainerHeaderLength(b);
		} else if (currentDestination == Destinations.CONTAINER_HEADER) {
			writeContainerHeader(b);
		} else if (currentDestination == Destinations.COMPONENT_HEADER) {
			writeComponentHeader(b);
		} else if (currentDestination == Destinations.DATA) {
			writeData(b);
		} else {
			throw new IOException("Internal state error.");
		}
	}
	
	private void writeContainerHeaderLength(int b) throws IOException {
		containerHeaderLengthCodec.write(b);
		
		if (containerHeaderLengthCodec.readyToDecode()) {
			containerHeaderLength = containerHeaderLengthCodec.decodeAsLong();
			currentDestination = Destinations.CONTAINER_HEADER;
		}
	}
	
	private void writeContainerHeader(int b) throws IOException {
		containerHeaderOutputStream.write(b);
		numberOfBytesWrittenToContainerHeader++;
		
		if (numberOfBytesWrittenToContainerHeader == containerHeaderLength) {
			containerHeaderOutputStream.close();
			containerHeader = containerHeaderOutputStream.getContainerHeader();
			
			if (containerHeader.getComponentHeaderLength() > 0) {
				currentDestination = Destinations.COMPONENT_HEADER;
			} else {
				currentDestination = Destinations.DATA;
			}
		} else if (numberOfBytesWrittenToContainerHeader > containerHeaderLength) {
			throw new IOException("Internal state error: Tried to write past expected container length");
		}
	}

	private void writeComponentHeader(int b) throws IOException {
		ioControllerFacade.write(componentHeaderStreamId, b);
		numberOfBytesWrittenToComponentHeader++;
		
		if (numberOfBytesWrittenToComponentHeader == containerHeader.getComponentHeaderLength()) {
			currentDestination = Destinations.DATA;
		}
	}

	private void writeData(int b) throws IOException {
		if (numberOfBytesWrittenToData >= containerHeader.getDataLength()) {
			throw new IOException("Trying to write past expected data length.");
		}
		
		ioControllerFacade.write(dataStreamId, b);
		numberOfBytesWrittenToData++;
	}
	
	@Override
	public void close() throws IOException {
		ioControllerFacade.closeOutputStream(componentHeaderStreamId);
		ioControllerFacade.closeOutputStream(dataStreamId);
		
		String componentHeaderBinaryObjectId = ioControllerFacade.commit(componentHeaderCheckoutId);
		String dataBinaryObjectId = ioControllerFacade.commit(dataCheckoutId);
		
		boolean containerContentIsConsistentWithHeader = false;
		
		if (containerHeader != null) {
			// Verify that the component header is consistent
			containerContentIsConsistentWithHeader = componentHeaderBinaryObjectId.equals(containerHeader.getComponentHeaderBinaryObjectId());
			
			// Verify that the data is consistent
			containerContentIsConsistentWithHeader &= dataBinaryObjectId.equals(containerHeader.getDataBinaryObjectId());
		}
		
		if (!containerContentIsConsistentWithHeader) {
			try {
				ioControllerFacade.deleteBinaryObject(componentHeaderBinaryObjectId);
				ioControllerFacade.deleteBinaryObject(dataBinaryObjectId);
			} catch (FileDeletionException e) {
				IOException ioException = new IOException("Container was inconsistent, but could not be deleted.");
				ioException.initCause(e);
				throw ioException;
			}
			
			if (containerHeader == null) {
				throw new IOException("Container header was null.");
			}
			
			throw new IOException("Container was inconsistent, and was deleted.");
		}
	}
	
	public ContainerHeader getContainerHeader() {
		return containerHeaderOutputStream.getContainerHeader();
	}
}
