package iocontroller;

public class Checkout {
	private String id;
	private String filename;
	private String binaryObjectId;
	
	public Checkout(String id, String filename, String binaryObjectId) {
		this.id = id;
		this.filename = filename;
		this.binaryObjectId = binaryObjectId;
	}

	public String getId() {
		return id;
	}

	public String getFilename() {
		return filename;
	}

	public String getBinaryObjectId() {
		return binaryObjectId;
	}
}
