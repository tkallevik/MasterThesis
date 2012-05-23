package utilities;

import java.io.File;
import java.io.IOException;

/**
 * This class finds and reserves available filenames.
 *
 */
public class FileNameGenerator {
	private String path;
	
	/**
	 * Create a new FileNameGenerator.
	 * 
	 * @param path the to check for available fileNames in
	 */
	public FileNameGenerator(String path) {
		this.path = path;
	}

	/**
	 * Find an available fileName in the given path,
	 * and reserve it by creating an empty file.
	 * 
	 * @return an available fileName
	 * @throws IOException 
	 */
	public String createFileName() throws IOException {
		File file;
		String fileName;
		do {
			fileName = "" + Math.random();
			file = new File(path + fileName);
		} while(file.exists());
		
		file.createNewFile();
		
		return fileName;
	}

}
