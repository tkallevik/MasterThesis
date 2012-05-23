package fluxcapacitor;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;

import loadingdock.ContainerHeader;
import loadingdock.IContainerEventListener;
import loadingdock.ILoadingDockFacade;
import transfercontroller.ITransferControllerFacade;
import transfercontroller.handshake.HandshakeException;
import utilities.ServiceUnavailableException;
import archivecontroller.ExportReference;
import archivecontroller.IArchiveControllerFacade;
import archivecontroller.IArchiveQuery;
import archivecontroller.ObjectReference;
import archivecontroller.QueryException;

public class FluxCapacitorFacade implements IFluxCapacitorFacade, IFluxCapacitorServer, IContainerEventListener {
	private IArchiveControllerFacade archiveControllerFacade;
	private IArchiveQuery archiveQuery;
	private ITransferControllerFacade transferControllerFacade;
	private ILoadingDockFacade loadingDockFacade;
	private FluxCapacitorServerRemoteFactory fluxCapacitorServerRemoteFactory;
	private String serverHost;
	private int serverPort;
	
	public FluxCapacitorFacade(IArchiveControllerFacade archiveControllerFacade, IArchiveQuery archiveQuery,
			ITransferControllerFacade transferControllerFacade, ILoadingDockFacade loadingDockFacade, 
			FluxCapacitorServerRemoteFactory fluxCapacitorServerRemoteFactory, String serverHost, int serverPort) {
		this.archiveControllerFacade = archiveControllerFacade;
		this.archiveQuery = archiveQuery;
		this.transferControllerFacade = transferControllerFacade;
		this.loadingDockFacade = loadingDockFacade;
		this.fluxCapacitorServerRemoteFactory = fluxCapacitorServerRemoteFactory;
		this.serverHost = serverHost;
		this.serverPort = serverPort;
	}
	
	@Override
	public void sendUnit(String unitId, String host, int port) throws IOException, ServiceUnavailableException, HandshakeException, QueryException {
		// Get local objects
		ObjectReference[] objectReferences = archiveQuery.getObjectReferences(unitId);
		
		if (objectReferences == null) {
			throw new QueryException("No such unit found.");
		}
		
		// Get remote objects
		IFluxCapacitorServer fluxCapacitorServer = fluxCapacitorServerRemoteFactory.createRemoteServer(host, port);
		ObjectReference[] serverObjectReferences = fluxCapacitorServer.getObjectReferences(unitId);
		
		// Eliminate objects already existing on remote host
		ObjectReference[] nonDuplicateObjectReferences = eliminateDuplicates(objectReferences, serverObjectReferences);
		
		prepareAndTransferContainers(fluxCapacitorServer, nonDuplicateObjectReferences);
	}
	
	@Override
	public void sendObjects(ObjectReference[] objectReferences, String host, int port) throws IOException, QueryException, HandshakeException, ServiceUnavailableException {
		if (objectReferences.length == 0) {
			throw new IOException("No objects specified.");
		}
		
		IFluxCapacitorServer fluxCapacitorServer = fluxCapacitorServerRemoteFactory.createRemoteServer(host, port);
		
		ArrayList<ObjectReference> objectsToPush = new ArrayList<ObjectReference>();
		for (ObjectReference objectReference : objectReferences) {
			if (!archiveQuery.hasObject(objectReference.getComponentId(), objectReference.getObjectId())) {
				throw new QueryException(objectReference + " was not found.");
			}
			
			if (!fluxCapacitorServer.hasObject(objectReference) || archiveQuery.isBranch(objectReference)) {
				objectsToPush.add(objectReference);
			}
		}
		
		ObjectReference[] objectReferenceArray = new ObjectReference[objectsToPush.size()];
		
		prepareAndTransferContainers(fluxCapacitorServer, objectsToPush.toArray(objectReferenceArray));
	}

	private void prepareAndTransferContainers(IFluxCapacitorServer fluxCapacitorServer,	ObjectReference[] objectReferences) 
			throws IOException, HandshakeException, ServiceUnavailableException, UnknownHostException {
		
		// Create containers
		String[] containerIds = new String[objectReferences.length];
		int i = 0;
		for(ObjectReference objectReference : objectReferences) {
			ExportReference exportReference = archiveControllerFacade.exportObject(objectReference.getComponentId(), objectReference.getObjectId());
			containerIds[i++] = loadingDockFacade.createContainer(objectReference.getComponentId(), 
					exportReference.getComponentHeaderBinaryObjectId(), exportReference.getDataBinaryObjectId());
		}
		
		// Request transfer controller host and port from the remote
		String transferControllerHost = fluxCapacitorServer.requestTransferControllerHost();
		int transferControllerPort = fluxCapacitorServer.requestTransferControllerPort();
		
		// Tell the local transfer controller to send the containers
		transferControllerFacade.sendContainers(containerIds, transferControllerHost, transferControllerPort);
	}
	
	@Override
	public void pullUnit(String unitId, String host, int port) throws IOException {
		IFluxCapacitorServer fluxCapacitorServer = fluxCapacitorServerRemoteFactory.createRemoteServer(host, port);
		try {
			fluxCapacitorServer.sendUnit(unitId, serverHost, serverPort);
		} catch (Exception e) {
			IOException ioException = new IOException("The remote was unable to send the requested unit.");
			ioException.initCause(e);
			throw ioException;
		}
	}
	
	@Override
	public void pullObjects(ObjectReference[] objectReferences, String host, int port) throws IOException {
		IFluxCapacitorServer fluxCapacitorServer = fluxCapacitorServerRemoteFactory.createRemoteServer(host, port);
		try {
			fluxCapacitorServer.sendObjects(objectReferences, serverHost, serverPort);
		} catch (Exception e) {
			IOException ioException = new IOException("The remote was unable to send the requested objects.");
			ioException.initCause(e);
			throw ioException;
		}
	}

	private ObjectReference[] eliminateDuplicates(ObjectReference[] objectReferences, ObjectReference[] serverObjectReferences) {
		if (serverObjectReferences == null) {
			return objectReferences;
		}
		
		ArrayList<ObjectReference> nonDuplicateObjectReferences = new ArrayList<ObjectReference>();
		for (ObjectReference objectReference : objectReferences) {
			boolean found = false;
			for (ObjectReference serverObjectReference : serverObjectReferences) {
				if (objectReference.equals(serverObjectReference)) {
					found = true;
				}
			}
			
			if (!found) {
				nonDuplicateObjectReferences.add(objectReference);
			}
		}
		
		ObjectReference[] nonDuplicateObjectReferencesArray = new ObjectReference[nonDuplicateObjectReferences.size()];
		
		return nonDuplicateObjectReferences.toArray(nonDuplicateObjectReferencesArray);
	}

	@Override
	public ObjectReference[] getObjectReferences(String unitId) throws HandshakeException, QueryException {
		return archiveQuery.getObjectReferences(unitId);
	}

	@Override
	public String requestTransferControllerHost() throws HandshakeException {
		return transferControllerFacade.getHost();
	}

	@Override
	public int requestTransferControllerPort() throws HandshakeException {
		return transferControllerFacade.getPort();
	}

	/**
	 * Implements IContainerEventListener.
	 */
	@Override
	public void newInboundContainer(String containerId) {
		ContainerHeader containerHeader = loadingDockFacade.getInboundContainerHeader(containerId);
		ExportReference exportReference = new ExportReference(
				containerHeader.getComponentHeaderBinaryObjectId(), containerHeader.getDataBinaryObjectId());
		
		try {
			archiveControllerFacade.importObject(containerHeader.getComponentId(), exportReference);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public String getHost() {
		return serverHost;
	}

	@Override
	public int getPort() {
		return serverPort;
	}

	@Override
	public boolean hasObject(ObjectReference objectReference) {
		return archiveQuery.hasObject(objectReference.getComponentId(), objectReference.getObjectId());
	}
}
