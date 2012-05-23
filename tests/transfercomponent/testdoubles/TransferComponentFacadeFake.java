package transfercomponent.testdoubles;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import loadingdock.LoadingDockFacade;

import transfercomponent.ITransferComponentFacade;
import transfercomponent.StreamCopier;
import transfercontroller.Ticket;
import utilities.LoadingDockInputStream;
import utilities.LoadingDockOutputStream;
import utilities.LoopRunnable;
import utilities.ServiceUnavailableException;

public class TransferComponentFacadeFake implements ITransferComponentFacade {
	private LoadingDockFacade loadingDockSource;
	private LoadingDockFacade loadingDockDestination;
	private TestCase testCase;
	
	public enum TestCase {
		STREAM_COPIER_NO_SOCKET, NO_STREAM_COPIER_NO_SOCKET, STREAM_COPIER_WITH_SOCKET
	}
	
	public TransferComponentFacadeFake(TestCase testCase) {
		this.testCase = testCase;
	}
	
	public void setLoadingDockFacades(LoadingDockFacade loadingDockSource, LoadingDockFacade loadingDockDestination) {
		this.loadingDockDestination = loadingDockDestination;
		this.loadingDockSource = loadingDockSource;
	}
	
	@Override
	public void stop(String ticketId) throws ServiceUnavailableException, IOException {}

	@Override
	public void pushFile(Ticket ticket) throws UnknownHostException, IOException, ServiceUnavailableException {
		String[] conainerIds = ticket.getContainerIds();
		
		for (String containerId : conainerIds) {			
			if (testCase == TestCase.STREAM_COPIER_NO_SOCKET) {
				streamCopierNoSocket(containerId);
			} else if (testCase == TestCase.NO_STREAM_COPIER_NO_SOCKET) {
				noStreamCopierNoSocket(containerId);
			} else if (testCase == TestCase.STREAM_COPIER_WITH_SOCKET) {
				streamCopierWithSocket(containerId);
			}
		}
	}

	private void streamCopierWithSocket(final String containerId) throws IOException {
		final int port = 54565;
		
		final Object objectToWaitOn = new Object();
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					ServerSocket serverSocket = new ServerSocket(port);
					
					synchronized (objectToWaitOn) {
						objectToWaitOn.notifyAll();
					}
					
					Socket socket = serverSocket.accept();
					InputStream inputStreamDestination = socket.getInputStream();
					
					// Receive the containerId, before receiving the data
					DataInputStream dataInputStream = new DataInputStream(inputStreamDestination);
					String containerId = dataInputStream.readUTF();
					
					LoadingDockOutputStream outputStreamDestination = new LoadingDockOutputStream(loadingDockDestination, containerId);
					
					StreamCopier streamCopierDestination = new StreamCopier(inputStreamDestination, outputStreamDestination);
					LoopRunnable loopRunnable = new LoopRunnable(streamCopierDestination, null);
					loopRunnable.run();
					
					serverSocket.close();
					
					synchronized (objectToWaitOn) {
						objectToWaitOn.notifyAll();
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}).start();
		
		try {
			synchronized (objectToWaitOn) {
				objectToWaitOn.wait();
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Socket socket = new Socket("localhost", port);
		OutputStream outputStreamSource = socket.getOutputStream();
		
		// Send the containerId to the other side, before sending the actual data
		DataOutputStream dataOutputStream = new DataOutputStream(outputStreamSource);
		dataOutputStream.writeUTF(containerId);
		
		LoadingDockInputStream inputStreamSource = new LoadingDockInputStream(loadingDockSource, containerId);
		StreamCopier streamCopierSource = new StreamCopier(inputStreamSource, outputStreamSource);
		LoopRunnable loopRunnable = new LoopRunnable(streamCopierSource, null);
		loopRunnable.run();
		
		try {
			synchronized (objectToWaitOn) {
				objectToWaitOn.wait();
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void noStreamCopierNoSocket(String containerId) throws IOException {
		loadingDockSource.openInputStream(containerId);
		loadingDockDestination.openOutputStream(containerId);
		
		int b;
		
		do {
			b = loadingDockSource.read(containerId);
			
			if (b != -1) {
				loadingDockDestination.write(containerId, b);
			}
		} while (b != -1);
		
		loadingDockSource.closeInputStream(containerId);
		loadingDockDestination.closeOutputStream(containerId);
	}

	private void streamCopierNoSocket(String containerId) throws IOException {
		LoadingDockInputStream inputStream = new LoadingDockInputStream(loadingDockSource, containerId);
		LoadingDockOutputStream outputStream = new LoadingDockOutputStream(loadingDockDestination, containerId);
		
		StreamCopier streamCopier = new StreamCopier(inputStream, outputStream);
		LoopRunnable loopRunnable = new LoopRunnable(streamCopier, null);
		loopRunnable.run();
	}

	@Override
	public boolean hasActiveTicket(String ticketId) throws ServiceUnavailableException {
		return false;
	}

	@Override
	public void listenForFiles(int transferPort) throws IOException {
		// TODO Auto-generated method stub
		
	}

}
