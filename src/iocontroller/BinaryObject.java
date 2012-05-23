package iocontroller;

public class BinaryObject {
	private String id;
	private String hash;
	private String filename;
	
	public BinaryObject(String id, String hash, String filename) {
		this.id = id;
		this.hash = hash;
		this.filename = filename;
	}

	public String getId() {
		return id;
	}

	public String getHash() {
		return hash;
	}

	public String getFilename() {
		return filename;
	}
}
