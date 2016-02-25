package jm.tools.service.http.servlet;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jm.tools.service.IService;
import jm.tools.service.ServiceBuilder;
import jm.tools.service.ServiceContainer;
import jm.tools.service.ServiceFactory;
import jm.tools.service.annotations.AnnotatedClassScanner;
import jm.tools.service.annotations.IAnnotationCallback;
import jm.tools.service.annotations.Service;
import jm.tools.service.message.IRequestMessageParser;
import jm.tools.service.message.http.HttpRequestMessage;
import jm.tools.service.message.http.HttpResponseMessage;
import jm.tools.service.message.http.impl.DefaultHttpResponseMessage;
import jm.tools.service.message.http.impl.JSONHttpRequestParser;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ServiceDispatcherServlet extends HttpServlet {
	private static final Log LOG = LogFactory.getLog(ServiceDispatcherServlet.class);
	
	private static final long serialVersionUID = -6938530646631863073L;

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		String packages = config.getInitParameter("packages");
		String async = config.getInitParameter("async");
		if("true".equals(async)){
			Thread thread = new Thread(new ServiceLoaderThread(packages));
			thread.start();
		}else {
			this.initialize(packages);
		}
	}

	@Override
	public void destroy() {
		super.destroy();
		ServiceContainer.destoryService();
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		this.doPost(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		String path = request.getRequestURI();
		String serviceId = path.substring(path.lastIndexOf("/") + 1);
		
		IRequestMessageParser parser = new JSONHttpRequestParser(request);
		HttpRequestMessage req = (HttpRequestMessage)parser.parse();
		HttpResponseMessage resp = new DefaultHttpResponseMessage(response);
		
		IService service = null;
		try {
			service = ServiceFactory.createService(serviceId);
			if(service == null) throw new ClassNotFoundException("resources[" + serviceId + "] is not supported");
			service.doService(req, resp);
			
		} catch (Exception e) {
			if(e instanceof ClassNotFoundException){
				LOG.error("create service error:resources="+serviceId, e);
				response.sendError(HttpServletResponse.SC_NOT_FOUND, e.getMessage());
				return;
			}
			LOG.error(e.getMessage(), e);
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "server error");
		}	
	}
	
	class ServiceLoaderThread implements Runnable{
		String packages;
		ServiceLoaderThread(String packages){
			this.packages = packages;
		}
		public void run() {
			initialize(packages);
		}
		
	}
	
	private void initialize(String packages){
		AnnotatedClassScanner scanner = new AnnotatedClassScanner(new Class<?>[]{Service.class});
		scanner.scan(packages.split("[,;]"), new IAnnotationCallback(){
			public void processAnnotation(Class<?> annotatedClass) {
				if(annotatedClass.isAnnotationPresent(Service.class)){
					LOG.info("mapping service:" + annotatedClass.getName());
					Service service = (Service)annotatedClass.getAnnotation(Service.class);
					ServiceBuilder sm = new ServiceBuilder();
					sm.setService(annotatedClass);
					sm.setValidators(service.validators());
					ServiceContainer.registerService(service.id(), sm);
				}
			}
		});
	}
}
