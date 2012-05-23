package fluxcapacitor;

import java.io.IOException;

import archivecontroller.ObjectReference;
import archivecontroller.QueryException;
import transfercontroller.handshake.HandshakeException;
import utilities.ServiceUnavailableException;

public interface IFluxCapacitorServer {

	public ObjectReference[] getObjectReferences(String unitId) throws HandshakeException, QueryException;

	public String requestTransferControllerHost() throws HandshakeException;

	public int requestTransferControllerPort() throws HandshakeException;

	public void sendUnit(String unitId, String host, int port) throws IOException, ServiceUnavailableException, HandshakeException, QueryException;

	public boolean hasObject(ObjectReference objectReference) throws HandshakeException;

	public void sendObjects(ObjectReference[] objectReferences, String serverHost, int serverPort) throws IOException, QueryException, HandshakeException, ServiceUnavailableException;

}
