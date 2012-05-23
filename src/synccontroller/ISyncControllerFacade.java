package synccontroller;

import java.io.IOException;

import transfercontroller.handshake.HandshakeException;
import utilities.ServiceUnavailableException;
import archivecontroller.QueryException;

public interface ISyncControllerFacade {
	
	public void sync(String host, int port) throws IOException, ServiceUnavailableException, HandshakeException, QueryException;
	
}
