package jm.tools.template.impl;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Map;

import jm.tools.template.TemplateException;
import jm.tools.template.VelocityTemplateEngine;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.velocity.app.VelocityEngine;

/**
 * 文本文件模板引擎
 * @author yjm
 *
 */
public class TextFileTemplateEngine extends VelocityTemplateEngine {
	private static final Log LOG = LogFactory.getLog(TextFileTemplateEngine.class);
	public TextFileTemplateEngine(String templateFilePath){
		this(templateFilePath, "GBK");
	}
	
	public TextFileTemplateEngine(String templateFilePath, String encoding){
		super.getEngine().setProperty(VelocityEngine.INPUT_ENCODING, encoding);
		super.getEngine().setProperty(VelocityEngine.OUTPUT_ENCODING, encoding);
		super.getEngine().setProperty(VelocityEngine.FILE_RESOURCE_LOADER_PATH, templateFilePath);
		try {
			super.getEngine().init();
		} catch (Exception e) {
			LOG.error("template engine init error:", e);
		}
	}
	
	public void transform(String srcFilename, File destFile, Map<String, Object> params) throws TemplateException{
		PrintWriter out = null;
		try {
			String templateContent = this.getTemplateContent(srcFilename, params);
			out = new PrintWriter(destFile.getAbsoluteFile());
			out.write(templateContent);
			out.flush();
		} catch (Exception e) {
			LOG.error("template engine transfer:", e);
			throw new TemplateException(e);
		} finally {
			IOUtils.closeQuietly(out);
		}
	}

	public void transform(String srcFilename, OutputStream destOut,
			Map<String, Object> params) throws TemplateException {
		BufferedOutputStream out = null;
		try {
			out = new BufferedOutputStream(destOut);
			String templateContent = this.getTemplateContent(srcFilename, params);
			out.write(templateContent.getBytes());
		} catch(Exception e) {
			LOG.error("template engine transfer:", e);
			throw new TemplateException(e);
		} finally {
			IOUtils.closeQuietly(out);
		}
	}
}
