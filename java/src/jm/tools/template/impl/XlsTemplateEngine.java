package jm.tools.template.impl;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.util.Map;

import jm.tools.identity.Timestamper;
import jm.tools.path.Path;
import jm.tools.template.ITemplateEngine;
import jm.tools.template.TemplateException;
import net.sf.jxls.transformer.XLSTransformer;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


public class XlsTemplateEngine implements ITemplateEngine {
	private static final Log LOG = LogFactory.getLog(XlsTemplateEngine.class);
	private XLSTransformer transformer = new XLSTransformer();
	private Path templateFilePath;
	public XlsTemplateEngine(String templateFilePath){
		this.templateFilePath = new Path(templateFilePath);
	}
	public void transform(String srcFilename, File destFile,
			Map<String, Object> params) throws TemplateException {
		try {
			transformer.transformXLS(this.templateFilePath.append(srcFilename).toOSString(), params, destFile.getAbsolutePath());
		} catch (Exception e) {
			LOG.error("template engine transfer:", e);
			throw new TemplateException(e);
		}
	}

	public void transform(String srcFilename, OutputStream destOut,
			Map<String, Object> params) throws TemplateException {
		File destFile = new File(new Path(System.getProperty("java.io.tmpdir")).append(Timestamper.next()+".xls").toOSString());
		
		BufferedInputStream input = null;
		try {
			this.transform(srcFilename, destFile, params);
			input = new BufferedInputStream(new FileInputStream(destFile));
			IOUtils.copy(input, destOut);
		} catch (Exception e) {
			LOG.error("template engine transfer:", e);
			throw new TemplateException(e);
		} finally {
			IOUtils.closeQuietly(input);
			destFile.delete();
		}
	}

}
