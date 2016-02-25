package jm.tools.service.message.http.impl;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.http.HttpServletResponse;

import jm.tools.service.message.http.HttpResponseMessage;

public class DefaultHttpResponseMessage implements HttpResponseMessage {
	private HttpServletResponse response;
	
	public DefaultHttpResponseMessage(HttpServletResponse response){
		this.response = response;
	}
	
	public HttpServletResponse getHttpResponse() {
		return this.response;
	}

	public OutputStream getOutputStream() throws IOException {
		return this.response.getOutputStream();
	}

}
