package loadingdock;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class ContainerHeaderOutputStream extends OutputStream {
	private ContainerHeader containerHeader;
	private ByteArrayOutputStream byteArrayOutputStream;

	public ContainerHeaderOutputStream(ContainerHeader containerHeader) throws IOException {
		this.containerHeader = containerHeader;
		this.byteArrayOutputStream = new ByteArrayOutputStream();
	}

	@Override
	public void write(int b) throws IOException {
		byteArrayOutputStream.write(b);
	}
	
	@Override
	public void close() throws IOException {
		byteArrayOutputStream.flush();
		String serializedContainerHeader = new String(byteArrayOutputStream.toByteArray());
		try {
			containerHeader.deserialize(serializedContainerHeader);
		} catch (Exception e) {
			IOException ioException = new IOException();
			ioException.initCause(e);
			throw ioException;
		}
	}

	public ContainerHeader getContainerHeader() {
		return containerHeader;
	}
}
