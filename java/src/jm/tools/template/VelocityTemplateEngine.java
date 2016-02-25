package jm.tools.template;

import java.io.StringWriter;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;


public abstract class VelocityTemplateEngine implements ITemplateEngine {
	private static final Log LOG = LogFactory.getLog(VelocityTemplateEngine.class);
	private VelocityEngine engine = new VelocityEngine();
	protected String getTemplateContent(String filename, Map<String, Object> params) throws TemplateException{
		StringWriter writer = new StringWriter();
		VelocityContext context = this.getContext(params);
		try {
			Template t = engine.getTemplate(filename);
			t.merge(context, writer);
			writer.flush();
		}catch (Exception e) {
			LOG.error("template engine get content error:", e);
			throw new TemplateException(e);
		}finally {
			IOUtils.closeQuietly(writer);
		}
		return writer.toString();
	}
	
	protected VelocityContext getContext(Map<String, Object> params){
		VelocityContext context = new VelocityContext();
		Iterator<String> iter = params.keySet().iterator();
        while (iter.hasNext()) {
          String key = (String)iter.next();
          context.put(key, params.get(key) == null ? "" : params.get(key));
        }
        return context;
	}
	
	protected VelocityEngine getEngine(){
		return this.engine;
	}
}
