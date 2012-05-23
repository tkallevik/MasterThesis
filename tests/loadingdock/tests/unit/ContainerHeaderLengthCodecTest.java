package loadingdock.tests.unit;

import static org.junit.Assert.*;

import java.io.IOException;

import loadingdock.ContainerHeaderLengthCodec;

import org.junit.Test;

public class ContainerHeaderLengthCodecTest {

	@Test
	public void decodeTest() throws Exception {
		// Set up fixture
		/* 00000001
		 * 00101101
		 * 00111111
		 * 01111111
		 * 00000100
		 * 00001011
		 * 01100010
		 * 01000011
		 */
		int[] bytes = {1, 45, 63, 127, 4, 11, 98, 67};
		ContainerHeaderLengthCodec containerHeaderLengthCodec = new ContainerHeaderLengthCodec(8);
		
		// Exercise SUT
		for (int b : bytes) {
			containerHeaderLengthCodec.write(b);
		}
		
		long containerHeaderLength = containerHeaderLengthCodec.decodeAsLong();
		
		// Verify outcome
		long expected = 84793782751158851L;
		assertEquals(expected, containerHeaderLength);
	}
	
	@Test
	public void encodeDecodeTest() throws IOException {
		// Set up fixture
		long containerHeaderLengthA = 34598743598743L;
		ContainerHeaderLengthCodec containerHeaderLengthCodecA = new ContainerHeaderLengthCodec(8);
		ContainerHeaderLengthCodec containerHeaderLengthCodecB = new ContainerHeaderLengthCodec(8);
		
		containerHeaderLengthCodecA.encodeLong(containerHeaderLengthA);
		
		// Exercise SUT
		int b;
		do {
			b = containerHeaderLengthCodecA.read();
			
			if (b != -1) {
				containerHeaderLengthCodecB.write(b);
			}
		} while(b != -1);
		
		// Verify outcome
		assertTrue(containerHeaderLengthCodecB.readyToDecode());
		long containerHeaderLengthB = containerHeaderLengthCodecB.decodeAsLong();
		assertEquals(containerHeaderLengthA, containerHeaderLengthB);
	}

}
