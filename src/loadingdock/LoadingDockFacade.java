package loadingdock;

import iocontroller.IIOControllerFacade;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class LoadingDockFacade implements ILoadingDockFacade {
	private ContainerStreamFactory containerStreamFactory;
	private IIOControllerFacade ioControllerFacade;
	private ArrayList<IContainerEventListener> containerEventListeners;
	private HashMap<String, ContainerHeader> transit;
	private HashMap<String, ContainerHeader> inbound;
	private HashMap<String, ContainerHeader> outbound;
	private HashMap<String, ContainerInputStream> containerInputStreams;
	private HashMap<String, ContainerOutputStream> containerOutputStreams;
	
	public LoadingDockFacade(ContainerStreamFactory containerStreamFactory, IIOControllerFacade ioControllerFacade) {
		this.containerStreamFactory = containerStreamFactory;
		this.ioControllerFacade = ioControllerFacade;
		this.containerEventListeners = new ArrayList<IContainerEventListener>();
		this.containerInputStreams = new HashMap<String, ContainerInputStream>();
		this.containerOutputStreams = new HashMap<String, ContainerOutputStream>();
		this.transit = new HashMap<String, ContainerHeader>();
		this.inbound = new HashMap<String, ContainerHeader>();
		this.outbound = new HashMap<String, ContainerHeader>();
	}

	@Override
	public void openInputStream(String containerId) throws IOException {
		ContainerHeader containerHeader = outbound.get(containerId);
		ContainerInputStream containerInputStream = containerStreamFactory.createContainerInputStream(containerHeader);
		containerInputStreams.put(containerId, containerInputStream);
	}
	
	@Override
	public int read(String containerId) throws IOException {
		ContainerInputStream containerInputStream = containerInputStreams.get(containerId);
		
		return containerInputStream.read();
	}
	
	@Override
	public void closeInputStream(String containerId) throws IOException {
		containerInputStreams.get(containerId).close();
		containerInputStreams.remove(containerId);
	}
	
	@Override
	public void openOutputStream(String containerId) throws IOException {
		ContainerHeader containerHeader = new ContainerHeader(containerId);
		transit.put(containerId, containerHeader);
		
		ContainerOutputStream containerOutputStream = containerStreamFactory.createContainerOutputStream(containerHeader);
		containerOutputStreams.put(containerId, containerOutputStream);
	}
	
	@Override
	public void write(String containerId, int b) throws IOException {
		containerOutputStreams.get(containerId).write(b);
	}

	@Override
	public void flush(String containerId) throws IOException {
		containerOutputStreams.get(containerId).flush();
	}

	@Override
	public void closeOutputStream(String containerId) throws IOException {
		// Close stream
		containerOutputStreams.get(containerId).close();
		containerOutputStreams.remove(containerId);
		
		// Move container from transit to inbound
		ContainerHeader containerHeader = transit.get(containerId);
		inbound.put(containerId, containerHeader);
		transit.remove(containerId);
		
		// Notify listeners
		for (IContainerEventListener containerEventListener : containerEventListeners) {
			containerEventListener.newInboundContainer(containerId);
		}
	}

	@Override
	public String createContainer(String componentId, String componentHeaderBinaryObjectId, String dataBinaryObjectId) throws IOException {
		String containerId = String.valueOf((int) (Math.random() * 32432432));
		
		long componentHeaderSize = ioControllerFacade.getSize(componentHeaderBinaryObjectId);
		long dataSize = ioControllerFacade.getSize(dataBinaryObjectId);
		ContainerHeader containerHeader = new ContainerHeader(containerId, componentId, componentHeaderBinaryObjectId, componentHeaderSize, dataBinaryObjectId, dataSize);
		outbound.put(containerId, containerHeader);
		
		return containerId;
	}

	@Override
	public ContainerHeader getInboundContainerHeader(String containerId) {
		return inbound.get(containerId);
	}
	
	@Override
	public void addContainerEventListener(IContainerEventListener containerEventListener) {
		containerEventListeners.add(containerEventListener);
	}
}
