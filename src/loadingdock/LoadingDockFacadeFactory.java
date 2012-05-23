package loadingdock;

import iocontroller.IIOControllerFacade;

public class LoadingDockFacadeFactory {
	public LoadingDockFacade createFacade(IIOControllerFacade ioControllerFacade) {
		ContainerStreamFactory containerStreamFactory = new ContainerStreamFactory(ioControllerFacade, 8);
		
		return new LoadingDockFacade(containerStreamFactory, ioControllerFacade);
	}
}
