package service;

import javax.servlet.http.HttpServletResponse;

import jm.tools.httpreqresp.validator.IValidator;
import jm.tools.httpreqresp.validator.ValidatorPlugin;
import jm.tools.minijson.JSONObject;

public class CommonValidator extends ValidatorPlugin {

	public CommonValidator(IValidator validator) {
		super(validator);
	}

	@Override
	public boolean doValidate(JSONObject jsonObject,
			HttpServletResponse response) {
		System.out.println("CommonValidator .....");
		return true;
	}

}
