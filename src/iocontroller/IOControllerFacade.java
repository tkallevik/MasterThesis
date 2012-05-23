package iocontroller;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import archivecontroller.ExportReference;
import archivecontroller.IArchiveComponent;
import archivecontroller.ObjectReference;

import utilities.FileNameGenerator;
import utilities.StringBinaryObjectMapper;

public class IOControllerFacade implements IIOControllerFacade, IArchiveComponent {
	private final String componentId = "ioController";
	private StreamController streamController;
	private HashMap<String, BinaryObject> binaryObjects;
	private HashMap<String, Checkout> checkouts;
	private HashMap<Integer, Checkout> streamToCheckoutMapping;
	private LinkedList<Checkout> dirtyCheckouts; 
	private String repositoryPath;
	private FileOperationAbstraction fileOperationAbstraction;
	private FileNameGenerator fileNameGenerator;
	private StringBinaryObjectMapper stringBinaryObjectMapper;
	
	public IOControllerFacade(StreamController streamController, String repositoryPath, 
			FileOperationAbstraction fileOperationAbstraction, FileNameGenerator fileNameGenerator, StringBinaryObjectMapper stringBinaryObjectMapper) {
		this.streamController = streamController;
		this.repositoryPath = repositoryPath;
		this.binaryObjects = new HashMap<String, BinaryObject>();
		this.checkouts = new HashMap<String, Checkout>();
		this.streamToCheckoutMapping = new HashMap<Integer, Checkout>();
		this.dirtyCheckouts = new LinkedList<Checkout>();
		this.fileOperationAbstraction = fileOperationAbstraction;
		this.fileNameGenerator = fileNameGenerator;
		this.stringBinaryObjectMapper = stringBinaryObjectMapper;
	}
	
	@Override
	public String checkOut(String binaryObjectHash) throws IOException {
		// Copy originating binary object
		BinaryObject binaryObject = getBinaryObjectByHash(binaryObjectHash);		
		String filename = fileNameGenerator.createFileName();
		fileOperationAbstraction.copy(resolvePath(binaryObject.getFilename()), resolvePath(filename));
		
		return registerCheckout(filename, binaryObjectHash);
	}
	
	private String registerCheckout(String filename, String binaryObjectHash) {
		// Register as checkout
		String checkoutId = generateId();
		Checkout checkout = new Checkout(checkoutId, filename, binaryObjectHash);
		checkouts.put(checkoutId, checkout);
		
		return checkoutId;
	}
	
	@Override
	public String checkOut() throws IOException {
		String filename = fileNameGenerator.createFileName();
		
		return registerCheckout(filename, null);
	}	
	
	@Override
	public int openInputStream(String checkoutId) throws IOException {
		Checkout checkout = checkouts.get(checkoutId);
		String checkoutPath = resolvePath(checkout.getFilename());
		
		int streamId = streamController.openInputStream(checkoutPath);
		
		streamToCheckoutMapping.put(streamId, checkout);
		
		return streamId; 
	}
	
	@Override
	public int openOutputStream(String checkoutId) throws IOException {
		Checkout checkout = checkouts.get(checkoutId);
		String checkoutPath = resolvePath(checkout.getFilename());
		
		int streamId = streamController.openOutputStream(checkoutPath);
		
		streamToCheckoutMapping.put(streamId, checkout);
		
		dirtyCheckouts.add(checkout);
		
		return streamId; 
	}

	@Override
	public int read(int streamId) throws IOException {
		return streamController.read(streamId);
	}

	@Override
	public void write(int streamId, int data) throws IOException {
		streamController.write(streamId, data);
	}
	
	@Override
	public void flush(int streamId) throws IOException {
		streamController.flush(streamId);
	}

	@Override
	public void closeInputStream(int streamId) throws IOException {
		streamController.closeInputStream(streamId);
		streamToCheckoutMapping.remove(streamId);
	}
	
	@Override
	public void closeOutputStream(int streamId) throws IOException {
		streamController.closeOutputStream(streamId);
		streamToCheckoutMapping.remove(streamId);
	}
	
	@Override
	public void deleteBinaryObject(String binaryObjectHash) throws IOException, FileDeletionException {
		Checkout[] checkouts = getCheckouts(binaryObjectHash);
		BinaryObject[] binaryObjectsWithGivenHash = getBinaryObjects(binaryObjectHash);
		
		if (checkouts.length == 0 || binaryObjectsWithGivenHash.length > checkouts.length) {
			BinaryObject binaryObject = getBinaryObjectByHash(binaryObjectHash);
			
			if (binaryObjectsWithGivenHash.length == 1) {
				fileOperationAbstraction.delete(resolvePath(binaryObject.getFilename()));
			}
			
			binaryObjects.remove(binaryObject.getId());
		} else {
			throw new IOException("The binary object is checked out, and could not be deleted.");
		}
	}
	
	@Override
	public void deleteCheckout(String checkoutId) throws IOException, FileDeletionException {
		if (streamToCheckoutMapping.containsValue(checkoutId)) {
			throw new IOException("Checkout has open streams, and cannot be deleted.");
		}
		
		if (!checkouts.containsKey(checkoutId)) {
			throw new IOException("No such checkout found.");
		}
		
		Checkout checkout = checkouts.get(checkoutId);
		fileOperationAbstraction.delete(resolvePath(checkout.getFilename()));
		
		checkouts.remove(checkoutId);
		dirtyCheckouts.remove(checkout);
	}
	
	@Override
	public String commit(String checkoutId) throws IOException {
		if (streamToCheckoutMapping.containsValue(checkoutId)) {
			throw new IOException("Checkout has open streams, and cannot be commited.");
		}
		
		Checkout checkout = checkouts.get(checkoutId);
		
		String binaryObjectId;
		try {
			if (dirtyCheckouts.contains(checkout)) {
				binaryObjectId = commitDirtyCheckout(checkout);
			} else {
				commitCleanCheckout(checkout);
				binaryObjectId = null;
			}
		} catch (FileDeletionException e) {
			IOException ioException = new IOException("Failed to commit checkout.");
			ioException.initCause(e);
			throw ioException;
		}
		
		checkouts.remove(checkout.getId());
		
		return binaryObjectId;
	}
	
	private void commitCleanCheckout(Checkout checkout) throws IOException, FileDeletionException {
		deleteCheckout(checkout.getId());
	}

	private String commitDirtyCheckout(Checkout checkout) throws IOException, FileDeletionException {
		// Compute hash for the checkout
		String hash;
		
		try {
			hash = fileOperationAbstraction.hash(resolvePath(checkout.getFilename()));
		} catch (NoSuchAlgorithmException e) {
			IOException ioException = new IOException("Unable to hash the checkout for commit.");
			ioException.initCause(e);
			throw ioException;
		}
		
		// Figure out which filename to refer to
		String filename;
		if (hasBinaryObject(hash)) {
			// There already exists a binary object with this hash.
			// The new binary object will therefore use the same filename for de-duplication purposes.
			// The file connected to the checkout will simply be deleted, as it is identical to the existing binary object.
			filename = getBinaryObjectByHash(hash).getFilename();
			fileOperationAbstraction.delete(resolvePath(checkout.getFilename()));
		} else {
			filename = checkout.getFilename();
		}

		// Create and register binary object
		BinaryObject binaryObject = new BinaryObject(generateId(), hash, filename);
		binaryObjects.put(binaryObject.getId(), binaryObject);
		
		// Remove checkout reference
		dirtyCheckouts.remove(checkout);

		return binaryObject.getHash();
	}
	
	private String generateId() {
		String id;
		do {
			id = String.valueOf((int) (Math.random() * 10000));
		} while(checkouts.containsKey(id) || binaryObjects.containsKey(id));
		
		return id;
	}
	
	private String resolvePath(String filename) {
		return repositoryPath + filename;
	}
	
	private BinaryObject getBinaryObjectByHash(String binaryObjectHash) throws IOException {
		for (BinaryObject binaryObject : binaryObjects.values()) {
			if (binaryObject.getHash().equals(binaryObjectHash)) {
				return binaryObject;
			}
		}
		
		throw new IOException("No such binary object found.");
	}

	@Override
	public long getSize(String binaryObjectHash) throws IOException {
		BinaryObject binaryObject = getBinaryObjectByHash(binaryObjectHash);
		File file = new File(resolvePath(binaryObject.getFilename()));
		return file.length();
	}

	@Override
	public boolean hasBinaryObject(String binaryObjectHash) {
		try {
			getBinaryObjectByHash(binaryObjectHash);
		} catch (IOException e) {
			return false;
		}
		
		return true;
	}

	@Override
	public String getUuid() {
		return componentId;
	}

	@Override
	public ObjectReference importObject(ExportReference exportReference) throws IOException {
		@SuppressWarnings("unused")
		String componentHeader = stringBinaryObjectMapper.readBinaryObjectToString(exportReference.getComponentHeaderBinaryObjectId());
		
		if (!hasBinaryObject(exportReference.getDataBinaryObjectId())) {
			throw new IOException("No such binary object found.");
		}
		
		return new ObjectReference(getUuid(), exportReference.getDataBinaryObjectId());
	}

	@Override
	public ExportReference exportObject(String binaryObjectHash) throws IOException {
		String componentHeader = "";
		String componentHeaderBinaryObjectId = stringBinaryObjectMapper.writeStringToBinaryObject(componentHeader);
		
		BinaryObject binaryObject = getBinaryObjectByHash(binaryObjectHash);
		BinaryObject dataBinaryObject = new BinaryObject(generateId(), binaryObjectHash, binaryObject.getFilename());
		binaryObjects.put(dataBinaryObject.getId(), dataBinaryObject);
		
		return new ExportReference(componentHeaderBinaryObjectId, binaryObjectHash);
	}

	@Override
	public boolean hasObject(String binaryObjectHash) {
		return hasBinaryObject(binaryObjectHash);
	}

	@Override
	public void deleteObject(String binaryObjectHash) throws IOException {
		try {
			deleteBinaryObject(binaryObjectHash);
		} catch (FileDeletionException e) {
			IOException ioException = new IOException("Unable to delete object.");
			ioException.initCause(e);
			throw ioException;
		}
	}

	@Override
	public String getHash(String binaryObjectHash) throws Exception {
		return binaryObjectHash;
	}
	
	private Checkout[] getCheckouts(String binaryObjectHash) {
		ArrayList<Checkout> results = new ArrayList<Checkout>();
		for (Checkout checkout : checkouts.values()) {
			String binaryObjectId = checkout.getBinaryObjectId();
			
			if (binaryObjectId != null && binaryObjectId.equals(binaryObjectHash)) {
				results.add(checkout);
			}
		}
		
		return results.toArray(new Checkout[results.size()]);
	}
	
	private BinaryObject[] getBinaryObjects(String binaryObjectHash) {
		ArrayList<BinaryObject> results = new ArrayList<BinaryObject>();
		for (BinaryObject binaryObject : binaryObjects.values()) {
			if (binaryObject.getHash().equals(binaryObjectHash)) {
				results.add(binaryObject);
			}
		}
		
		return results.toArray(new BinaryObject[results.size()]);
	}
}
