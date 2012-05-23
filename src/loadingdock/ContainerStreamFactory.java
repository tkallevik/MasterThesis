package loadingdock;

import java.io.IOException;

import iocontroller.IIOControllerFacade;

public class ContainerStreamFactory {
	private IIOControllerFacade ioControllerFacade;
	private int containerHeaderLengthNumberOfBytes;
	
	public ContainerStreamFactory(IIOControllerFacade ioControllerFacade, int containerHeaderLengthNumberOfBytes) {
		this.ioControllerFacade = ioControllerFacade;
		this.containerHeaderLengthNumberOfBytes = containerHeaderLengthNumberOfBytes;
	}
	
	public ContainerOutputStream createContainerOutputStream(ContainerHeader containerHeader) throws IOException {
		ContainerHeaderOutputStream containerHeaderOutputStream = new ContainerHeaderOutputStream(containerHeader);
		ContainerHeaderLengthCodec containerHeaderLengthCodec = new ContainerHeaderLengthCodec(containerHeaderLengthNumberOfBytes);
		
		return new ContainerOutputStream(ioControllerFacade, containerHeaderOutputStream, containerHeaderLengthCodec);
	}

	public ContainerInputStream createContainerInputStream(ContainerHeader containerHeader) throws IOException {
		ContainerHeaderInputStream containerHeaderInputStream = new ContainerHeaderInputStream(containerHeader);
		ContainerHeaderLengthCodec containerHeaderLengthCodec = new ContainerHeaderLengthCodec(containerHeaderLengthNumberOfBytes);
		
		return new ContainerInputStream(ioControllerFacade, containerHeaderInputStream, containerHeaderLengthCodec);
	}
}
