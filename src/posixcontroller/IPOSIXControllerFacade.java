package posixcontroller;

import java.io.IOException;

import archivecontroller.QueryException;

public interface IPOSIXControllerFacade {

	public String create();

	public int openInputStream(String branchId) throws QueryException, IOException;

	public int openOutputStream(String branchId) throws QueryException, IOException;

	public int read(int streamId) throws IOException;

	public void write(int streamId, int b) throws IOException;

	public void flush(int streamId) throws IOException;

	public void closeInputStream(int streamId) throws IOException;

	public void closeOutputStream(int streamId) throws IOException;

	public void commit(String branchId) throws IOException, QueryException;

	public void commitClean(String branchId) throws IOException, QueryException;

}
