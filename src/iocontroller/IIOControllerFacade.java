package iocontroller;

import java.io.IOException;

public interface IIOControllerFacade {
	
	public String checkOut(String binaryObjectId) throws IOException;

	public String checkOut() throws IOException;

	public int openInputStream(String checkoutId) throws IOException;

	public int openOutputStream(String checkoutId) throws IOException;
	
	public int read(int streamId) throws IOException;

	public void write(int streamId, int data) throws IOException;

	public void flush(int streamId) throws IOException;

	public void closeInputStream(int streamId) throws IOException;

	public void closeOutputStream(int streamId) throws IOException;

	public void deleteBinaryObject(String binaryObjectId) throws IOException, FileDeletionException;

	public void deleteCheckout(String checkoutId) throws IOException, FileDeletionException;

	public String commit(String checkoutId) throws IOException;

	public long getSize(String binaryObjectId) throws IOException;
	
	public boolean hasBinaryObject(String binaryObjectId);

}
