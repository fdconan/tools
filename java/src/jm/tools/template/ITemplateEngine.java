package jm.tools.template;

import java.io.File;
import java.io.OutputStream;
import java.util.Map;

public interface ITemplateEngine {
	public void transform(String srcFilename, File destFile, Map<String, Object> params) throws TemplateException;
	public void transform(String srcFilename, OutputStream destOut, Map<String, Object> params) throws TemplateException;
}
