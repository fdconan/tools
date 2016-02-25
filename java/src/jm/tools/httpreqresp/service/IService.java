package jm.tools.httpreqresp.service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jm.tools.minijson.JSONObject;

public interface IService {
	public void doService(JSONObject jsonObject, HttpServletRequest request, HttpServletResponse response) throws Exception;
}
