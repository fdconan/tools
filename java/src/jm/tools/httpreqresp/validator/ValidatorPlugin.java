package jm.tools.httpreqresp.validator;

import javax.servlet.http.HttpServletResponse;

import jm.tools.minijson.JSONObject;

/**
 * 
 * @author yjm
 *
 */
public abstract class ValidatorPlugin implements IValidator {
	private IValidator validator;
	
	public ValidatorPlugin(IValidator validator){
		this.validator = validator;
	}
	public boolean validate(JSONObject jsonObject, HttpServletResponse response) {
		if(this.validator != null){
			if(this.validator.validate(jsonObject, response)){
				return doValidate(jsonObject, response);
			}else {
				return false;
			}
		}
		return doValidate(jsonObject, response);
	}

	public abstract boolean doValidate(JSONObject jsonObject, HttpServletResponse response);
}
