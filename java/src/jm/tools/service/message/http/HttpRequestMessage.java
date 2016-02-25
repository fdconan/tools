package jm.tools.service.message.http;

import javax.servlet.http.HttpServletRequest;

import jm.tools.service.message.IRequestMessage;


public interface HttpRequestMessage extends IRequestMessage {
	public HttpServletRequest getHttpRequest();
	
	/*
	public AbstractHttpRequestMessage setBoolean(String key, boolean defaultValue);

	public AbstractHttpRequestMessage setDouble(String key, double defaultValue);

	public AbstractHttpRequestMessage setInt(String key, int defaultValue);

	public AbstractHttpRequestMessage setLong(String key, long defaultValue);

	public AbstractHttpRequestMessage setObject(String key, Object defaultValue);

	public AbstractHttpRequestMessage setShort(String key, short defaultValue) ;
	public AbstractHttpRequestMessage setString(String key, String defaultValue);
	*/

}
