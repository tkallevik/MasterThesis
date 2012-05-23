package posixcontroller;

import java.io.IOException;
import java.util.HashMap;

import archivecontroller.IArchiveQuery;
import archivecontroller.QueryException;
import iocontroller.IIOControllerFacade;

public class POSIXControllerFacade implements IPOSIXControllerFacade {
	private IIOControllerFacade ioControllerFacade;
	private IArchiveQuery archiveQuery;
	private HashMap<String, String> branchToCheckoutMapping;
	
	public POSIXControllerFacade(IIOControllerFacade ioControllerFacade, IArchiveQuery archiveQuery) {
		this.ioControllerFacade = ioControllerFacade;
		this.archiveQuery = archiveQuery;
		this.branchToCheckoutMapping = new HashMap<String, String>();
	}
	
	@Override
	public String create() {
		return archiveQuery.createBranch();
	}
	
	@Override
	public int openInputStream(String branchId) throws QueryException, IOException {
		String checkoutId;
		if (branchToCheckoutMapping.containsKey(branchId)) {
			 checkoutId = branchToCheckoutMapping.get(branchId);
		} else {
			String binaryObjectId = archiveQuery.getBranchBinaryObjectId(branchId);
			checkoutId = ioControllerFacade.checkOut(binaryObjectId);
			
			branchToCheckoutMapping.put(branchId, checkoutId);
		}
		
		return ioControllerFacade.openInputStream(checkoutId);
	}
	
	@Override
	public int openOutputStream(String branchId) throws QueryException, IOException {
		String checkoutId;
		if (branchToCheckoutMapping.containsKey(branchId)) {
			 checkoutId = branchToCheckoutMapping.get(branchId);
		} else {
			String binaryObjectId = archiveQuery.getBranchBinaryObjectId(branchId);
			
			if (binaryObjectId == null) {
				checkoutId = ioControllerFacade.checkOut();
			} else {
				checkoutId = ioControllerFacade.checkOut(binaryObjectId);
			}
			
			branchToCheckoutMapping.put(branchId, checkoutId);
		}
		
		return ioControllerFacade.openOutputStream(checkoutId);
	}
	
	@Override
	public int read(int streamId) throws IOException {
		return ioControllerFacade.read(streamId);
	}
	
	@Override
	public void write(int streamId, int b) throws IOException {
		ioControllerFacade.write(streamId, b);
	}
	
	@Override
	public void flush(int streamId) throws IOException {
		ioControllerFacade.flush(streamId);
	}
	
	@Override
	public void closeInputStream(int streamId) throws IOException {
		ioControllerFacade.closeInputStream(streamId);
	}
	
	@Override
	public void closeOutputStream(int streamId) throws IOException {
		ioControllerFacade.closeOutputStream(streamId);
	}
	
	@Override
	public void commit(String branchId) throws IOException, QueryException {
		String checkoutId = branchToCheckoutMapping.get(branchId);
		
		String binaryObjectId = ioControllerFacade.commit(checkoutId);
		branchToCheckoutMapping.remove(branchId);
		
		archiveQuery.commitBranch(branchId, binaryObjectId);
	}
	
	@Override
	public void commitClean(String branchId) throws IOException, QueryException {
		String checkoutId = branchToCheckoutMapping.get(branchId);
		
		ioControllerFacade.commit(checkoutId);
		branchToCheckoutMapping.remove(branchId);
	}
}
