package transfercomponent.testdoubles;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import testutilities.InvocationLog;
import transfercomponent.StreamCopier;

/**
 * The responsibility of this class is to mock the StreamCopier, and simply log the method invocations.
 *
 */
public class StreamCopierMock extends StreamCopier {
	public InvocationLog invocationLog;
	
	public StreamCopierMock(InputStream inputStream, OutputStream outputStream) {
		super(inputStream, outputStream);
		invocationLog = new InvocationLog();
	}
	
	@Override
	public void copyByte() throws IOException {
		invocationLog.addInvocation("copyByte");
	}
	
	@Override
	public void terminateAction() throws IOException {
		invocationLog.addInvocation("terminateAction");
	}
}
