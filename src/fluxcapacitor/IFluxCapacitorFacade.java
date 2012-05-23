package fluxcapacitor;

import java.io.IOException;

import transfercontroller.handshake.HandshakeException;
import utilities.ServiceUnavailableException;
import archivecontroller.ObjectReference;
import archivecontroller.QueryException;

public interface IFluxCapacitorFacade {

	public void sendUnit(String unitId, String host, int port) throws IOException, 
		ServiceUnavailableException, HandshakeException, QueryException;

	public void pullUnit(String unitId, String host, int port) throws IOException;

	public String getHost();

	public int getPort();

	public void sendObjects(ObjectReference[] objectReferences, String host, int port) throws IOException, QueryException, HandshakeException, ServiceUnavailableException;

	public void pullObjects(ObjectReference[] objectReferences, String host, int port) throws IOException;
}
