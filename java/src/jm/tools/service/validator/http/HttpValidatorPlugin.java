package jm.tools.service.validator.http;

import jm.tools.service.message.http.HttpRequestMessage;
import jm.tools.service.message.http.HttpResponseMessage;
import jm.tools.service.validator.IValidator;

/**
 * 
 * @author yjm
 *
 */
public abstract class HttpValidatorPlugin implements IValidator {
	private IValidator validator;
	
	public HttpValidatorPlugin(IValidator validator){
		this.validator = validator;
	}
	public boolean validate(HttpRequestMessage request, HttpResponseMessage response) {
		if(this.validator != null){
			if(this.validator.validate(request, response)){
				return doValidate(request, response);
			}else {
				return false;
			}
		}
		return doValidate(request, response);
	}

	public abstract boolean doValidate(HttpRequestMessage request, HttpResponseMessage response);
}
