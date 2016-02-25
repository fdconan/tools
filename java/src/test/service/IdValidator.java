package service;

import javax.servlet.http.HttpServletResponse;

import jm.tools.httpreqresp.validator.IValidator;
import jm.tools.httpreqresp.validator.ValidatorPlugin;
import jm.tools.minijson.JSONObject;

public class IdValidator extends ValidatorPlugin {

	public IdValidator(IValidator validator) {
		super(validator);
	}

	@Override
	public boolean doValidate(JSONObject jsonObject,
			HttpServletResponse response) {
		System.out.println("IdValidator .....");
		return true;
	}

}
