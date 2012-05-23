package loadingdock.testdoubles;

import java.util.ArrayList;

import loadingdock.IContainerEventListener;

public class ContainerEventListenerMock implements IContainerEventListener {
	public ArrayList<String> containerIds;
	
	public ContainerEventListenerMock(String[] containerIds) {
		this.containerIds = new ArrayList<String>();
		
		for (String containerId : containerIds) {
			this.containerIds.add(containerId);
		}
	}

	@Override
	public synchronized void newInboundContainer(String containerId) {
		containerIds.remove(containerId);
		
		if (containerIds.isEmpty()) {
			notifyAll();
		}
	}
	
	public void waitForContainers() {
		if (containerIds.isEmpty()) {
			return;
		}
		
		try {
			wait();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
