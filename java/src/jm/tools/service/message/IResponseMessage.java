package jm.tools.service.message;

import java.io.IOException;
import java.io.OutputStream;

public interface IResponseMessage {
	public OutputStream getOutputStream() throws IOException;
}
