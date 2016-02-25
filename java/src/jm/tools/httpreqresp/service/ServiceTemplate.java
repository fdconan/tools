package jm.tools.httpreqresp.service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jm.tools.httpreqresp.validator.IValidator;
import jm.tools.minijson.JSONObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * 
 * @author yjm
 *
 */
public abstract class ServiceTemplate implements IService{
	private static final Log LOG = LogFactory.getLog(ServiceTemplate.class);
	private IValidator validator = null;
	
	public ServiceTemplate(IValidator validator){
		this.validator = validator;
	}
	
	
	public final void doService(JSONObject jsonObject, HttpServletRequest request, HttpServletResponse response) throws Exception{
		
		if(this.validator != null){
			if(!this.validator.validate(jsonObject, response)){
				LOG.debug("["+this.validator.getClass().getName()+"] request data validate fail->"+jsonObject.toString());
				return ;
			}
		}
		try {
			this.doAction(jsonObject, request, response);
		}catch(Exception e){
			throw e;
		}
	}
	
	
	protected abstract void doAction(JSONObject jsonObject, HttpServletRequest request, HttpServletResponse response) throws Exception;
}
