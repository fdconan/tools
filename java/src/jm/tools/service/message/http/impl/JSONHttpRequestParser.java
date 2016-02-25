package jm.tools.service.message.http.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.servlet.http.HttpServletRequest;

import jm.tools.minijson.JSONObject;
import jm.tools.service.message.IRequestMessage;
import jm.tools.service.message.IRequestMessageParser;

public class JSONHttpRequestParser implements IRequestMessageParser {
	private HttpServletRequest request;
	private JSONObject jsonObject;
	public JSONHttpRequestParser(HttpServletRequest request){
		this.request = request;
	}

	public IRequestMessage parse() throws IOException {
		String requestBody = this.getRequestBody(request);
		if(requestBody.length() > 0){
			try {
				jsonObject = JSONObject.fromString(requestBody);
			}catch(Exception e){
				throw new IOException(e.getMessage());
			}
		}else {
			jsonObject = JSONObject.fromString("{}");
		}
		return new JSONHttpRequestMessage(jsonObject, request);
	}
	
	private String getRequestBody(HttpServletRequest request) throws IOException{
		StringBuilder body = new StringBuilder(100);
		String data = null;
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				request.getInputStream(), "utf-8"));
		while ((data = reader.readLine()) != null) {
			body.append(data);
		}
		if (reader != null) {
			reader.close();
		}
		return body.toString();
	}
}
