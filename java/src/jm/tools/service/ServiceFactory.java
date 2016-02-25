package jm.tools.service;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author yjm
 *
 */
public abstract class ServiceFactory {
	private static Map<String, IService> serviceMap = new HashMap<String, IService>();
	public static IService createService(String msgId) throws Exception{
		IService service = null;
		ServiceBuilder sb = ServiceContainer.getServiceBuilder(msgId);
		if(sb == null){
			return null;
		}
		if(sb.isSingleton()){
			if(serviceMap.containsKey(msgId)){
				return (IService)serviceMap.get(msgId);
			}
			service = sb.build();
			serviceMap.put(msgId, service);
		}else {
			service = sb.build();
		}
		return service;
	}
}
