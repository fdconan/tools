package jm.tools.httpreqresp.servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jm.tools.httpreqresp.result.BaseMsgResponse;
import jm.tools.httpreqresp.result.MsgResponseUtil;
import jm.tools.httpreqresp.service.IService;
import jm.tools.httpreqresp.service.ServiceBuilder;
import jm.tools.httpreqresp.service.ServiceContainer;
import jm.tools.httpreqresp.service.ServiceFactory;
import jm.tools.httpreqresp.service.annotations.AnnotatedClassScanner;
import jm.tools.httpreqresp.service.annotations.IAnnotationCallback;
import jm.tools.httpreqresp.service.annotations.Service;
import jm.tools.minijson.JSONObject;

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
		String requestBody = this.getRequestContent(request, response);
		if(requestBody == null || "".equals(requestBody)){
			requestBody = this.getRequestBody(request);
		}
		
		if(requestBody.length() == 0){
			MsgResponseUtil.msgResp(new BaseMsgResponse(), null, "-1", "Request content is empty", response);
			return ;
		}
		
		JSONObject jsonObject = null;
		try{
			jsonObject = JSONObject.fromObject(requestBody);
		}catch(Exception e){
			LOG.error("Request content format is not correct:"+e.getMessage(), e);
			MsgResponseUtil.msgResp(new BaseMsgResponse(), null, "-1", "Request content format is not correct", response);
		}
		
		String path = request.getRequestURI();
		String serviceId = path.substring(path.lastIndexOf("/") + 1);
		//String msgId = jsonObject.getString("MSG_ID");
		
		IService service = null;
		try {
			service = ServiceFactory.createService(serviceId);
			if(service == null) throw new ClassNotFoundException("resources[" + serviceId + "] is not supported");
			service.doService(jsonObject, request, response);
			
		} catch (Exception e) {
			if(e instanceof ClassNotFoundException){
				LOG.error("create service error:resources="+serviceId, e);
				MsgResponseUtil.msgResp(new BaseMsgResponse(), null, "-1", e.getMessage(), response);
				return;
			}
			LOG.error(e.getMessage(), e);
			MsgResponseUtil.msgResp(new BaseMsgResponse(), jsonObject, "-1", "server error", response);
		}	
	}

	private String getRequestBody(HttpServletRequest request)throws IOException {
		StringBuilder body = new StringBuilder(100);
		String data = null;
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				request.getInputStream(), "utf-8"));
		while ((data = reader.readLine()) != null) {
			body.append(data);
		}
		if (reader != null) {
			reader.close();
		}
		return body.toString();
	}
	
	/**
	 * 返回json格式的字符串
	 * @param request
	 * @param response
	 * @return
	 */
	protected String getRequestContent(HttpServletRequest request, HttpServletResponse response){
		return null;
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
