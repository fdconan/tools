package jm.tools.service.message.http;

import javax.servlet.http.HttpServletResponse;

import jm.tools.service.message.IResponseMessage;

public interface HttpResponseMessage extends IResponseMessage{
	public HttpServletResponse getHttpResponse();
}
