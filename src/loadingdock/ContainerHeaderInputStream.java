package loadingdock;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;

public class ContainerHeaderInputStream extends InputStream {
	private ContainerHeader containerHeader;
	private ByteArrayInputStream byteArrayInputStream;
	private long containerHeaderLength;

	public ContainerHeaderInputStream(ContainerHeader containerHeader) throws IOException {
		this.containerHeader = containerHeader;
		
		String serializedContainerHeader = containerHeader.serialize();
		
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		PrintWriter printWriter = new PrintWriter(byteArrayOutputStream);
		printWriter.print(serializedContainerHeader);
		printWriter.flush();
		byte[] byteArray = byteArrayOutputStream.toByteArray();
		byteArrayOutputStream.close();
		
		this.byteArrayInputStream = new ByteArrayInputStream(byteArray);
		this.containerHeaderLength = byteArray.length;
	}
	
	@Override
	public int read() throws IOException {
		return byteArrayInputStream.read();
	}
	
	@Override
	public void close() throws IOException {
		byteArrayInputStream.close();
	}

	public ContainerHeader getContainerHeader() {
		return containerHeader;
	}

	public long getHeaderLength() {
		return containerHeaderLength;
	}
}
