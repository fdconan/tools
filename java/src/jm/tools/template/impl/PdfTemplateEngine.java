package jm.tools.template.impl;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Map;

import jm.tools.template.TemplateException;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xhtmlrenderer.pdf.ITextRenderer;

public class PdfTemplateEngine extends TextFileTemplateEngine {
	
	private static final Log LOG = LogFactory.getLog(PdfTemplateEngine.class);
	
	public PdfTemplateEngine(String templateFilePath) {
		super(templateFilePath);
	}

	ITextRenderer renderer = new ITextRenderer();
	public void transform(String srcFilename, File destFile,
			Map<String, Object> params) throws TemplateException {
		OutputStream out = null;
		try {
			out = this.getOut(null, destFile);
			this.transform(srcFilename, out, params);
		} catch (Exception e) {
			LOG.error("template engine transfer:", e);
			throw new TemplateException(e);
		} finally {
			IOUtils.closeQuietly(out);
		}
	}

	public void transform(String srcFilename, OutputStream destOut,
			Map<String, Object> params) throws TemplateException {
		renderer.setDocumentFromString(super.getTemplateContent(srcFilename, params));
		renderer.layout();
		try {
			renderer.createPDF(this.getOut(destOut, null));
		} catch (Exception e) {
			LOG.error("template engine transfer:", e);
			throw new TemplateException(e);
		}
	}

	private OutputStream getOut(OutputStream destOut, File file) throws Exception{
		if(file == null){
			return destOut;
		}
		return new BufferedOutputStream(new FileOutputStream(file));
	}
}
