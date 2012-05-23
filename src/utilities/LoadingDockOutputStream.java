package utilities;

import java.io.IOException;
import java.io.OutputStream;

import loadingdock.ILoadingDockFacade;

public class LoadingDockOutputStream extends OutputStream {
	private ILoadingDockFacade loadingDockFacade;
	private String containerId;
	
	public LoadingDockOutputStream(ILoadingDockFacade loadingDockFacade, String containerId) throws IOException {
		this.loadingDockFacade = loadingDockFacade;
		this.containerId = containerId; 
		loadingDockFacade.openOutputStream(containerId);
	}
	
	@Override
	public void close() throws IOException {
		loadingDockFacade.closeOutputStream(containerId);
	}

	@Override
	public void write(int b) throws IOException {
		loadingDockFacade.write(containerId, b);
	}

	@Override
	public void flush() throws IOException {
		loadingDockFacade.flush(containerId);
	}
}
