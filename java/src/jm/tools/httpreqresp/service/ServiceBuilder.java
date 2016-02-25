package jm.tools.httpreqresp.service;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;

import jm.tools.httpreqresp.validator.IValidator;

public class ServiceBuilder {
	private Class<?>[] validators;
	private Class<?> service;
	private boolean singleton = true;
	
	public IService build() throws Exception{
		LinkedList<IValidator> validatorList = new LinkedList<IValidator>();
		IService serviceImpl = null;
		if(validators != null && validators.length > 0){
			for(int i=0; i<validators.length; ++i){
				if(i == 0){
					createValidator(validatorList, validators[i], null);
				}else {
					createValidator(validatorList, validators[i], validatorList.getFirst());
				}
			}
		}else {
			validatorList.addFirst(null);
		}
		Constructor<?> constructor = service.getConstructor(new Class[]{IValidator.class});
		serviceImpl = (IService)constructor.newInstance(new Object[]{validatorList.getFirst()});
		return serviceImpl;
	}

	private void createValidator(LinkedList<IValidator> validatorList,
			Class<?> clazz, IValidator v) throws NoSuchMethodException,
			InstantiationException, IllegalAccessException,
			InvocationTargetException {
		Constructor<?> constructor = clazz.getConstructor(new Class[]{IValidator.class});
		IValidator validator = (IValidator)constructor.newInstance(new Object[]{v});
		validatorList.addFirst(validator);
		if(v != null){
			validatorList.removeLast();
		}
	}
	
	public void setValidators(Class<?>[] validators) {
		this.validators = validators;
	}
	/*
	public Class<?> getService() {
		return service;
	}
	*/
	public void setService(Class<?> service) {
		this.service = service;
	}

	public boolean isSingleton() {
		return singleton;
	}

	public void setSingleton(boolean singleton) {
		this.singleton = singleton;
	}
	
}
