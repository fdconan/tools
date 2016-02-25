package jm.tools.service.message;

import java.io.IOException;
import java.io.InputStream;

public interface IRequestMessage {
	public String getString(String key, String defaultValue);
	public int getInt(String key, int defaultValue);
	public double getDouble(String key, double defaultValue);
	public short getShort(String key, short defaultValue);
	public long getLong(String key, long defaultValue);
	public boolean getBoolean(String key, boolean defaultValue);
	public Object getObject(String key);
	public InputStream getInputStream() throws IOException;
	public String toMessageString();
}
