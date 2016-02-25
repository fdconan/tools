package jm.tools.service.validator;

import jm.tools.service.message.IRequestMessage;
import jm.tools.service.message.IResponseMessage;

/**
 * 
 * @author yjm
 *
 */
public interface IValidator {
	public boolean validate(IRequestMessage request, IResponseMessage response);
}
