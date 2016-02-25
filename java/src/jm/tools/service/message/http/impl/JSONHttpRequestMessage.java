package jm.tools.service.message.http.impl;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.http.HttpServletRequest;

import jm.tools.minijson.JSONObject;
import jm.tools.service.message.http.HttpRequestMessage;

public class JSONHttpRequestMessage implements HttpRequestMessage {
	private JSONObject jsonObject;
	private HttpServletRequest request;
	public JSONHttpRequestMessage(JSONObject jsonObject, HttpServletRequest request){
		this.jsonObject = jsonObject;
		this.request = request;
	}
	public HttpServletRequest getHttpRequest() {
		return this.request;
	}

	public boolean getBoolean(String key, boolean defaultValue) {
		return this.jsonObject.optBoolean(key, defaultValue);
	}

	public double getDouble(String key, double defaultValue) {
		return this.jsonObject.optDouble(key, defaultValue);
	}

	public InputStream getInputStream() throws IOException {
		return this.request.getInputStream();
	}

	public int getInt(String key, int defaultValue) {
		return this.jsonObject.optInt(key, defaultValue);
	}

	public long getLong(String key, long defaultValue) {
		return this.jsonObject.optLong(key, defaultValue);
	}

	public Object getObject(String key) {
		return this.jsonObject.opt(key);
	}

	public short getShort(String key, short defaultValue) {
		return (short)this.jsonObject.optInt(key, defaultValue);
	}

	public String getString(String key, String defaultValue) {
		return this.jsonObject.optString(key, defaultValue);
	}
	public String toMessageString() {
		return this.jsonObject.toString();
	}

}
