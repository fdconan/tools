package service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jm.tools.httpreqresp.service.ServiceTemplate;
import jm.tools.httpreqresp.service.annotations.Service;
import jm.tools.httpreqresp.validator.IValidator;
import jm.tools.minijson.JSONObject;

@Service(id="TestService")
public class TestService extends ServiceTemplate {

	public TestService(IValidator validator) {
		super(validator);
	}

	@Override
	protected void doAction(JSONObject jsonObject, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		// TODO Auto-generated method stub

	}

}
