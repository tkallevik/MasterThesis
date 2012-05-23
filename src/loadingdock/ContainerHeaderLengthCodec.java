package loadingdock;

import java.io.IOException;
import java.nio.ByteBuffer;

public class ContainerHeaderLengthCodec {
	private byte[] containerHeaderLength;
	private int nextByte;
	
	public ContainerHeaderLengthCodec(int numberOfBytes) {
		containerHeaderLength = new byte[numberOfBytes];
		nextByte = 0;
	}
	
	public int read() {
		if (nextByte >= containerHeaderLength.length) {
			return -1;
		}
		
		return containerHeaderLength[nextByte++];
	}
	
	public void write(int b) throws IOException {
		if (nextByte >= containerHeaderLength.length) {
			throw new IOException("Header length already reached.");
		}
		
		containerHeaderLength[nextByte++] = (byte) b;
	}
	
	/**
	 * @author http://stackoverflow.com/questions/1026761/how-to-convert-a-byte-array-to-its-numeric-value-java
	 */
	public long decodeAsLong() {
		long value = 0;
		
		for (int i = 0; i < containerHeaderLength.length; i++)
		{
		   value = (value << 8) + (containerHeaderLength[i] & 0xff);
		}
		
		return value;
	}

	public boolean readyToDecode() {
		return nextByte == containerHeaderLength.length;
	}
	
	public void encodeLong(long l) {
		containerHeaderLength = ByteBuffer.allocate(8).putLong(l).array();
	}
}
