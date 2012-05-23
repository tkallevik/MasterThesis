package utilities;

import java.io.IOException;
import java.io.InputStream;

import loadingdock.ILoadingDockFacade;

public class LoadingDockInputStream extends InputStream {
	private ILoadingDockFacade loadingDockFacade;
	private String containerId;
	
	public LoadingDockInputStream(ILoadingDockFacade loadingDockFacade, String containerId) throws IOException {
		this.loadingDockFacade = loadingDockFacade;
		this.containerId = containerId;
		loadingDockFacade.openInputStream(containerId);
	}
	
	@Override
	public int read() throws IOException {
		return loadingDockFacade.read(containerId);
	}
	
	@Override
	public void close() throws IOException {
		loadingDockFacade.closeInputStream(containerId);
	}
}
