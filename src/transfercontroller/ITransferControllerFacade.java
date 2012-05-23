package transfercontroller;

import java.io.IOException;
import java.net.UnknownHostException;

import utilities.ServiceUnavailableException;

public interface ITransferControllerFacade {

	public void sendContainers(String[] containerIds, String host, int port) throws ServiceUnavailableException, UnknownHostException, IOException;

	public String getHost();

	public int getPort();

}