package iocontroller;

import utilities.FileHasher;
import utilities.FileNameGenerator;
import utilities.StringBinaryObjectMapper;

public class IOControllerFacadeFactory {
	
	public IOControllerFacade createFacade(String repositoryPath) {
		FileHasher fileHasher = new FileHasher(FileHasher.MD5);
		FileNameGenerator fileNameGenerator = new FileNameGenerator(repositoryPath);
		FileOperationAbstraction fileOperationAbstraction = new FileOperationAbstraction(fileHasher);
		StreamController streamController = new StreamController();
		StringBinaryObjectMapper stringBinaryObjectMapper = new StringBinaryObjectMapper();
		
		IOControllerFacade ioControllerFacade = new IOControllerFacade(streamController, repositoryPath, fileOperationAbstraction, 
				fileNameGenerator, stringBinaryObjectMapper);
		
		stringBinaryObjectMapper.setIOControllerFacade(ioControllerFacade);
		
		return ioControllerFacade;
	}
}
