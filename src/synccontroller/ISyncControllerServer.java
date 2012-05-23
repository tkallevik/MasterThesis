package synccontroller;

import archivecontroller.QueryException;
import transfercontroller.handshake.HandshakeException;
import unitcontroller.Branch;

public interface ISyncControllerServer {

	public Branch[] getBranches() throws HandshakeException;

	public String requestFluxCapacitorFacadeHost() throws HandshakeException;
	
	public int requestFluxCapacitorFacadePort() throws HandshakeException;

	public String[] getUnitIds(Branch[] branches) throws QueryException, HandshakeException;

}
