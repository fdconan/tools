package jm.tools.service.validator;

import jm.tools.service.message.IRequestMessage;
import jm.tools.service.message.IResponseMessage;

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
	public boolean validate(IRequestMessage request, IResponseMessage response) {
		if(this.validator != null){
			if(this.validator.validate(request, response)){
				return doValidate(request, response);
			}else {
				return false;
			}
		}
		return doValidate(request, response);
	}

	public abstract boolean doValidate(IRequestMessage request, IResponseMessage response);
}
