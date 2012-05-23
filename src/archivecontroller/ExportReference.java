package archivecontroller;

public class ExportReference {
	private String componentHeaderBinaryObjectId;
	private String dataBinaryObjectId;
	
	public ExportReference(String componentHeaderBinaryObjectId, String dataBinaryObjectId) {
		this.componentHeaderBinaryObjectId = componentHeaderBinaryObjectId;
		this.dataBinaryObjectId = dataBinaryObjectId;
	}

	public String getComponentHeaderBinaryObjectId() {
		return componentHeaderBinaryObjectId;
	}

	public String getDataBinaryObjectId() {
		return dataBinaryObjectId;
	}
}
