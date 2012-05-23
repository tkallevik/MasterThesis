package archivecontroller;

import java.io.IOException;

public interface IArchiveComponent {
	
	public String getUuid();
	
	public ObjectReference importObject(ExportReference exportReference) throws IOException;
	
	public ExportReference exportObject(String objectId) throws IOException;
	
	public boolean hasObject(String objectId);
	
	public void deleteObject(String objectId) throws IOException;
	
	public String getHash(String objectId) throws Exception;
}
