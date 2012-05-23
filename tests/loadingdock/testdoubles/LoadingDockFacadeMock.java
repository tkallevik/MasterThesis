package loadingdock.testdoubles;

import iocontroller.IIOControllerFacade;

import java.io.IOException;

import loadingdock.ContainerHeader;
import loadingdock.IContainerEventListener;
import loadingdock.ILoadingDockFacade;

public class LoadingDockFacadeMock implements ILoadingDockFacade {
	private IIOControllerFacade ioControllerFacade;
	
	public LoadingDockFacadeMock(IIOControllerFacade ioControllerFacade) {
		this.ioControllerFacade = ioControllerFacade;
	}

	@Override
	public void openInputStream(String containerId) throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int read(String containerId) throws IOException {
		return ioControllerFacade.read(0);
	}

	@Override
	public void closeInputStream(String containerId) throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void openOutputStream(String containerId) throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void write(String containerId, int b) throws IOException {
		ioControllerFacade.write(0, b);
	}

	@Override
	public void flush(String containerId) throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void closeOutputStream(String containerId) throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String createContainer(String componentId,
			String componentHeaderBinaryObjectId, String dataBinaryObjectId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ContainerHeader getInboundContainerHeader(String containerId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addContainerEventListener(
			IContainerEventListener containerEventListener) {
		// TODO Auto-generated method stub
		
	}

}
