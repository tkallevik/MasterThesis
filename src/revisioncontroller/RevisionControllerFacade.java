package revisioncontroller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import utilities.StringBinaryObjectMapper;
import utilities.StringHasher;

import archivecontroller.ExportReference;
import archivecontroller.IArchiveComponent;
import archivecontroller.ObjectReference;
import archivecontroller.QueryException;

public class RevisionControllerFacade implements IRevisionControllerFacade, IArchiveComponent {
	private final String componentId = "revisionController";
	private StringBinaryObjectMapper stringBinaryObjectMapper;
	private HashMap<String, Revision> revisions;
	private final String SERIALIZATION_FIELD_SEPARATOR = ";";
	private final String SERIALIZATION_LIST_ITEM_SEPARATOR = ",";
	
	public RevisionControllerFacade(StringBinaryObjectMapper stringBinaryObjectMapper) {
		this.stringBinaryObjectMapper = stringBinaryObjectMapper;
		this.revisions = new HashMap<String, Revision>();
	}
	
	@Override
	public String add(String[] parentIds) throws QueryException {
		String revisionId = generateRevisionId();
		
		Revision revision = new Revision(revisionId);
		revision.addParents(parentIds);
		
		revisions.put(revisionId, revision);
		
		return revisionId;
	}
	
	@Override
	public String[] getParents(String revisionId) throws QueryException {
		if (!revisions.containsKey(revisionId)) {
			throw new QueryException("No such revision found: " + revisionId);
		}
			
		return revisions.get(revisionId).getParents();
	}
	
	private String generateRevisionId() {
		// Decide on revision id
		String revisionId;
		do {
			revisionId = String.valueOf((int) (Math.random() * 10000));
		} while(revisions.containsKey(revisionId));
		
		return revisionId;
	}

	@Override
	public String getUuid() {
		return componentId;
	}

	@Override
	public ObjectReference importObject(ExportReference exportReference) throws IOException {
		@SuppressWarnings("unused")
		String componentHeader = stringBinaryObjectMapper.readBinaryObjectToString(exportReference.getComponentHeaderBinaryObjectId());
		String serializedRevision = stringBinaryObjectMapper.readBinaryObjectToString(exportReference.getDataBinaryObjectId());
		
		Revision revision = deserialize(serializedRevision);
		revisions.put(revision.getId(), revision);
		
		return new ObjectReference(getUuid(), revision.getId());
	}

	@Override
	public ExportReference exportObject(String revisionId) throws IOException {
		String componentHeader = "";
		String componentHeaderBinaryObjectId = stringBinaryObjectMapper.writeStringToBinaryObject(componentHeader);
		
		String serializedRevision = serialize(revisionId);
		String dataBinaryObjectId = stringBinaryObjectMapper.writeStringToBinaryObject(serializedRevision);
		
		return new ExportReference(componentHeaderBinaryObjectId, dataBinaryObjectId);
	}
	
	private String serialize(String revisionId) {
		Revision revision = revisions.get(revisionId);
		String[] parentIds = revision.getParents();
		
		String serializedRevision = revision.getId() + SERIALIZATION_FIELD_SEPARATOR;
		
		for(String parentId : parentIds) {
			serializedRevision += parentId + SERIALIZATION_LIST_ITEM_SEPARATOR;
		}
		serializedRevision.substring(0, serializedRevision.length() - 1 - SERIALIZATION_LIST_ITEM_SEPARATOR.length());
		
		return serializedRevision;
	}
	
	private Revision deserialize(String serializedRevision) {
		String[] fields = serializedRevision.split(SERIALIZATION_FIELD_SEPARATOR);
		
		Revision revision = new Revision(fields[0]);
		
		if (fields.length == 1) {
			return revision; 
		}
		
		String[] parentIds = fields[1].split(SERIALIZATION_LIST_ITEM_SEPARATOR);
		for (String parentId : parentIds) {
			revision.addParent(parentId);
		}
		
		return revision;
	}

	public void deleteRevision(String revisionId) {
		revisions.remove(revisionId);
	}

	@Override
	public boolean hasObject(String revisionId) {
		return revisions.containsKey(revisionId);
	}

	@Override
	public void deleteObject(String revisionId) throws IOException {
		deleteRevision(revisionId);
	}

	@Override
	public String getHash(String revisionId) throws Exception {
		return StringHasher.md5Sum(serialize(revisionId));
	}

	@Override
	public String[] getAncestors(String revisionId) throws QueryException {
		ArrayList<String> ancestors = new ArrayList<String>();
		
		// Get the revision's parent ids
		String[] parentIds = getParents(revisionId);
		
		// Add self
		ancestors.add(revisionId);
		
		// Get the ancestors for each of the parents
		for (String parentId : parentIds) {
			String[] parentAncestors = getAncestors(parentId);
			for (String ancestor : parentAncestors) {
				// Only add distinct ancestors
				if (!ancestors.contains(ancestor)) {
					ancestors.add(ancestor);
				}
			}
		}
		
		String[] stringArray = new String[ancestors.size()];
		return ancestors.toArray(stringArray);
	}
}
