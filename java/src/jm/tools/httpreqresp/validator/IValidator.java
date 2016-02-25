package jm.tools.httpreqresp.validator;

import javax.servlet.http.HttpServletResponse;

import jm.tools.minijson.JSONObject;

/**
 * 
 * @author yjm
 *
 */
public interface IValidator {
	public boolean validate(JSONObject jsonObject, HttpServletResponse response);
}
