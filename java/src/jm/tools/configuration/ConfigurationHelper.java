package jm.tools.configuration;

import javax.naming.NamingException;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.EnvironmentConfiguration;
import org.apache.commons.configuration.INIConfiguration;
import org.apache.commons.configuration.JNDIConfiguration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.SystemConfiguration;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.configuration.tree.xpath.XPathExpressionEngine;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 支持读取xml,properties,ini文件，支持读取env,sys,jndi变量
 * @author yjm
 *
 */
public final class ConfigurationHelper {
	
	public static final String XML_FILE = "xml";
	public static final String PROPERTIES_FILE = "properties";
	public static final String INI_FILE = "ini";
	public static final String ENV_FILE = "env";
	public static final String SYS_FILE = "sys";
	public static final String JNDI_FILE = "jndi";
	
	private static final Log LOG = LogFactory.getLog(ConfigurationHelper.class);
	
	private ConfigurationHelper(){}
	
	public static Configuration getConfigurationInstance(String filename, String fileType){
		org.apache.commons.configuration.Configuration config = null;
		try {
			if(ConfigurationHelper.XML_FILE.equalsIgnoreCase(fileType)){
				config = new XMLConfiguration(filename);
				return new Configuration(config).setExpressionEngine(new XPathExpressionEngine());
			}
			if(ConfigurationHelper.PROPERTIES_FILE.equalsIgnoreCase(fileType)){
				config = new PropertiesConfiguration(filename);
				return new Configuration(config);
			}
			if(ConfigurationHelper.INI_FILE.equalsIgnoreCase(fileType)){
				config = new INIConfiguration(filename);
				return new Configuration(config);
			}
			if(ConfigurationHelper.SYS_FILE.equalsIgnoreCase(fileType)){
				config = new SystemConfiguration();
				return new Configuration(config);
			}
			if(ConfigurationHelper.ENV_FILE.equalsIgnoreCase(fileType)){
				config = new EnvironmentConfiguration();
				return new Configuration(config);
			}
			if(ConfigurationHelper.JNDI_FILE.equalsIgnoreCase(fileType)){
				config = new JNDIConfiguration();
				return new Configuration(config);
			}
		}catch(ConfigurationException e){
			LOG.error(e.getMessage(), e);
		} catch (NamingException e) {
			LOG.error(e.getMessage(), e);
		}
		return null;
	}
	
	public static Configuration getConfigurationInstance(String filename){
		String fileType = getFileType(filename);
		return getConfigurationInstance(filename, fileType);
	}
	
	private static String getFileType(String filename) {
		if(filename == null || "".equals(filename.trim())){
			return ENV_FILE;
		}
		String extenstion = FilenameUtils.getExtension(filename);
		if("xml".equalsIgnoreCase(extenstion)){
			return XML_FILE;
		}
		if("properties".equalsIgnoreCase(extenstion)){
			return PROPERTIES_FILE;
		}
		if("ini".equalsIgnoreCase(extenstion)){
			return INI_FILE;
		}
		return SYS_FILE;
	}
}
