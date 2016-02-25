package jm.tools.service.message;

import java.io.IOException;

public interface IRequestMessageParser {
	public IRequestMessage parse() throws IOException;
}
