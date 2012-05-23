package metadatacontroller;

public interface IMetaDataControllerFacade {

	public String createBlank();

	public String createCopy(String originalMetaDataObjectId);

	public void addValue(String metaDataObjectId, String key, String value);

	public String[] getValues(String metaDataObjectId, String key);

	public String[] getKeys(String metaDataObjectId);

}
