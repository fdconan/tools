package jm.tools.service.http;

import jm.tools.service.IService;
import jm.tools.service.message.http.HttpRequestMessage;
import jm.tools.service.message.http.HttpResponseMessage;
import jm.tools.service.util.ServiceException;
import jm.tools.service.validator.IValidator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public abstract class HttpServiceTemplate implements IService {

	private static final Log LOG = LogFactory.getLog(HttpServiceTemplate.class);
	private IValidator validator = null;
	
	public HttpServiceTemplate(IValidator validator){
		this.validator = validator;
	}
	
	public final void doService(HttpRequestMessage request, HttpResponseMessage response) throws ServiceException{
		
		if(this.validator != null){
			if(!this.validator.validate(request, response)){
				LOG.debug("["+this.validator.getClass().getName()+"] request data validate fail->" + request.toMessageString());
				return ;
			}
		}
		this.doAction(request, response);
	}
	
	
	protected abstract void doAction(HttpRequestMessage request, HttpResponseMessage response) throws ServiceException;
	

}
