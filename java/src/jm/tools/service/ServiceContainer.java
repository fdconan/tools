package jm.tools.service;

import java.util.HashMap;
import java.util.Map;


public abstract class ServiceContainer {
	private static Map<String, ServiceBuilder> services = new HashMap<String, ServiceBuilder>();
	
	public static void registerService(String serviceId, ServiceBuilder serviceBuilder){
		services.put(serviceId, serviceBuilder);
	}
	
	public static void unregisterService(String serviceId){
		services.remove(serviceId);
	}
	
	public static ServiceBuilder getServiceBuilder(String serviceId){
		return services.get(serviceId);
	}
	/*
	public static  Map<String, ServiceBuilder> getServices(){
		return services;
	}
	*/
	public static void destoryService(){
		services.clear();
	}
}
