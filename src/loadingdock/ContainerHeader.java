package loadingdock;

public class ContainerHeader {
	private static final String DATA_SIZE_KEY = "DATA_SIZE";
	private static final String DATA_BINARY_OBJECT_ID_KEY = "DATA_BINARY_OBJECT_ID";
	private static final String COMPONENT_HEADER_SIZE_KEY = "COMPONENT_HEADER_SIZE";
	private static final String COMPONENT_HEADER_BINARY_OBJECT_ID_KEY = "COMPONENT_HEADER_BINARY_OBJECT_ID";
	private static final String COMPONENT_ID_KEY = "COMPONENT_ID";
	private static final String CONTAINER_ID_KEY = "CONTAINER_ID";
	private String containerId;
	private String componentId;
	private String componentHeaderBinaryObjectId;
	private long componentHeaderSize;
	private String dataBinaryObjectId;
	private long dataSize;
	private final String KEY_VALUE_PAIR_DIVIDER = "\n";
	private final String KEY_VALUE_DIVIDER = " ";
	
	public ContainerHeader() {}

	public ContainerHeader(String containerId) {
		this.containerId = containerId;
	}
	
	public ContainerHeader(String containerId, String componentId,
			String componentHeaderBinaryObjectId, long componentHeaderSize,
			String dataBinaryObjectId, long dataSize) {
		this.containerId = containerId;
		this.componentId = componentId;
		this.componentHeaderBinaryObjectId = componentHeaderBinaryObjectId;
		this.componentHeaderSize = componentHeaderSize;
		this.dataBinaryObjectId = dataBinaryObjectId;
		this.dataSize = dataSize;
	}

	public String getContainerId() {
		return containerId;
	}

	public String getComponentId() {
		return componentId;
	}

	public String getComponentHeaderBinaryObjectId() {
		return componentHeaderBinaryObjectId;
	}

	public long getComponentHeaderLength() {
		return componentHeaderSize;
	}

	public String getDataBinaryObjectId() {
		return dataBinaryObjectId;
	}

	public long getDataLength() {
		return dataSize;
	}

	public String serialize() {
		return String.format(
					CONTAINER_ID_KEY + KEY_VALUE_DIVIDER + "%s" + KEY_VALUE_PAIR_DIVIDER +
				    COMPONENT_ID_KEY + KEY_VALUE_DIVIDER + "%s" + KEY_VALUE_PAIR_DIVIDER +
				    COMPONENT_HEADER_BINARY_OBJECT_ID_KEY + KEY_VALUE_DIVIDER + "%s" + KEY_VALUE_PAIR_DIVIDER +
				    COMPONENT_HEADER_SIZE_KEY + KEY_VALUE_DIVIDER + "%d" + KEY_VALUE_PAIR_DIVIDER +
				    DATA_BINARY_OBJECT_ID_KEY + KEY_VALUE_DIVIDER + "%s" + KEY_VALUE_PAIR_DIVIDER +
				    DATA_SIZE_KEY + KEY_VALUE_DIVIDER + "%d" + KEY_VALUE_PAIR_DIVIDER,
				    containerId,
				    componentId,
				    componentHeaderBinaryObjectId,
				    componentHeaderSize,
				    dataBinaryObjectId,
				    dataSize
				  	);
	}

	public void deserialize(String serializedContainerHeader) throws Exception {
		String[] keyValuePairs = serializedContainerHeader.split(KEY_VALUE_PAIR_DIVIDER);

		for (String keyValuePair : keyValuePairs) {
			int indexOfKeyValueDivider = keyValuePair.indexOf(KEY_VALUE_DIVIDER);
			
			if (indexOfKeyValueDivider == -1) {
				throw new Exception("Parse error: Missing key value divider");
			}
			
			String key = keyValuePair.substring(0, indexOfKeyValueDivider);
			String value = keyValuePair.substring(indexOfKeyValueDivider + 1);
			
			if (key.equals(CONTAINER_ID_KEY)) {
				containerId = value;
			} else if (key.equals(COMPONENT_ID_KEY)) {
				componentId = value;
			} else if (key.equals(COMPONENT_HEADER_BINARY_OBJECT_ID_KEY)) {
				componentHeaderBinaryObjectId = value;
			} else if (key.equals(COMPONENT_HEADER_SIZE_KEY)) {
				componentHeaderSize = Long.parseLong(value);
			} else if (key.equals(DATA_BINARY_OBJECT_ID_KEY)) {
				dataBinaryObjectId = value;
			} else if (key.equals(DATA_SIZE_KEY)) {
				dataSize = Long.parseLong(value);
			}
		}
	}
}
