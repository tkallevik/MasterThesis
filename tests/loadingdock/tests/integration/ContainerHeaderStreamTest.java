package loadingdock.tests.integration;

import static org.junit.Assert.*;

import java.io.IOException;

import loadingdock.ContainerHeader;
import loadingdock.ContainerHeaderInputStream;
import loadingdock.ContainerHeaderOutputStream;

import org.junit.Test;

public class ContainerHeaderStreamTest {

	@Test
	public void test() throws IOException {
		// Set up fixture
		ContainerHeader containerHeaderA = new ContainerHeader("a", "b", "c", 1, "d", 2);
		ContainerHeader containerHeaderB = new ContainerHeader();
		ContainerHeaderInputStream containerHeaderInputStream = new ContainerHeaderInputStream(containerHeaderA);
		ContainerHeaderOutputStream containerHeaderOutputStream = new ContainerHeaderOutputStream(containerHeaderB);
		
		// Exercise SUT
		int b;
		do {
			b = containerHeaderInputStream.read();
			
			if (b != -1) {
				containerHeaderOutputStream.write(b);
			}
		} while(b != -1);
		
		containerHeaderInputStream.close();
		containerHeaderOutputStream.close();
		
		// Verify outcome
		String serializedContainerHeaderA = containerHeaderA.serialize();
		String serializedContainerHeaderB = containerHeaderB.serialize();
		assertEquals(serializedContainerHeaderA, serializedContainerHeaderB);
	}

}
