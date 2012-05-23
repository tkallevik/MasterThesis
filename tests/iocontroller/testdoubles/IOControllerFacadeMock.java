package iocontroller.testdoubles;

import java.io.FileNotFoundException;
import java.io.IOException;

import iocontroller.FileDeletionException;
import iocontroller.IIOControllerFacade;

public class IOControllerFacadeMock implements IIOControllerFacade {
	private int[] dataToReturn;
	private int nextDataToReturn;
	private int[] written;
	private int nextDataToWrite;
	
	public IOControllerFacadeMock(int[] dataToReturn, int[] written) {
		this.dataToReturn = dataToReturn;
		this.nextDataToReturn = 0;
		this.written = written;
		this.nextDataToWrite = 0;
	}
	
	@Override
	public int read(int streamId) throws IOException {
		return (nextDataToReturn < dataToReturn.length ? dataToReturn[nextDataToReturn++] : -1);
	}

	@Override
	public void closeInputStream(int streamId) throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int openInputStream(String checkoutId) throws FileNotFoundException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void deleteBinaryObject(String binaryObjectId) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int openOutputStream(String checkoutId) throws FileNotFoundException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void write(int streamId, int data) throws IOException {
		written[nextDataToWrite++] = data;
	}

	@Override
	public void closeOutputStream(int streamId) throws IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public void flush(int streamId) throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String checkOut(String binaryObjectId)
			throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String checkOut() throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deleteCheckout(String checkoutId) throws FileDeletionException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String commit(String stagedCheckoutId) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getSize(String binaryObjectId) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean hasBinaryObject(String binaryObjectId) {
		// TODO Auto-generated method stub
		return false;
	}
}