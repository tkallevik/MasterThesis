package loadingdock;

import java.io.IOException;

public interface ILoadingDockFacade {

	public void openInputStream(String containerId) throws IOException;

	public int read(String containerId) throws IOException;

	public void closeInputStream(String containerId) throws IOException;

	public void openOutputStream(String containerId) throws IOException;

	public void write(String containerId, int b) throws IOException;

	public void flush(String containerId) throws IOException;

	public void closeOutputStream(String containerId) throws IOException;

	public String createContainer(String componentId, String componentHeaderBinaryObjectId, String dataBinaryObjectId) throws IOException;

	public ContainerHeader getInboundContainerHeader(String containerId);

	public void addContainerEventListener(IContainerEventListener containerEventListener);

}