package iocontroller;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;

public class StreamController {
	/**
	 * A list of fileInputStreams.
	 */
	private HashMap<Integer, FileInputStream> fileInputStreams;
	
	/**
	 * A list of fileOutputStreams.
	 */
	private HashMap<Integer, FileOutputStream> fileOutputStreams;
	
	public StreamController() {
		this.fileInputStreams = new HashMap<Integer, FileInputStream>();
		this.fileOutputStreams = new HashMap<Integer, FileOutputStream>();
	}

	public int openInputStream(String path) throws FileNotFoundException {
		// Decide on stream id
		Integer streamId;
		do {
			streamId = (int) (Math.random() * 10000);
		} while(fileInputStreams.containsKey(streamId));
		
		// Create a FileInputStream, and add it to the list
		FileInputStream fileInputStream = new FileInputStream(path);
		fileInputStreams.put(streamId, fileInputStream);
		
		return streamId;
	}
	

	public int openOutputStream(String path) throws FileNotFoundException {
		// Decide on stream id
		Integer streamId;
		do {
			streamId = (int) (Math.random() * 10000);
		} while(fileOutputStreams.containsKey(streamId));
		
		// Create a FileOutputStream, and add it to the list
		FileOutputStream fileOutputStream = new FileOutputStream(path);
		fileOutputStreams.put(streamId, fileOutputStream);
		
		return streamId;
	}

	public void closeInputStream(int streamId) throws IOException {
		fileInputStreams.get(streamId).close();
		fileInputStreams.remove(streamId);
	}

	public void closeOutputStream(int streamId) throws IOException {
		fileOutputStreams.get(streamId).close();
		fileOutputStreams.remove(streamId);
	}

	public int read(int streamId) throws IOException {
		return fileInputStreams.get(streamId).read();
	}

	public void write(int streamId, int data) throws IOException {
		fileOutputStreams.get(streamId).write(data);
	}

	public void flush(int streamId) throws IOException {
		fileOutputStreams.get(streamId).flush();
	}
}
