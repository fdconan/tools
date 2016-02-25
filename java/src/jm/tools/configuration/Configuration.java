package jm.tools.configuration;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.apache.commons.configuration.INIConfiguration;
import org.apache.commons.configuration.JNDIConfiguration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.configuration.reloading.FileChangedReloadingStrategy;
import org.apache.commons.configuration.reloading.ReloadingStrategy;
import org.apache.commons.configuration.tree.xpath.XPathExpressionEngine;

/**
 * 
 * @author yjm
 *
 */
public class Configuration implements
		org.apache.commons.configuration.Configuration {
	private org.apache.commons.configuration.Configuration config;
	
	Configuration(org.apache.commons.configuration.Configuration config){
		this.config = config;
	}
	public void addProperty(String key, Object arg1) {
		this.config.addProperty(key, arg1);
	}

	public void clear() {
		this.config.clear();
	}

	public void clearProperty(String key) {
		this.config.clearProperty(key);
	}

	public boolean containsKey(String key) {
		return this.config.containsKey(key);
	}

	public BigDecimal getBigDecimal(String key) {
		return this.config.getBigDecimal(key);
	}

	public BigDecimal getBigDecimal(String key, BigDecimal arg1) {
		return this.config.getBigDecimal(key, arg1);
	}

	public BigInteger getBigInteger(String key) {
		return this.config.getBigInteger(key);
	}

	public BigInteger getBigInteger(String key, BigInteger arg1) {
		return this.config.getBigInteger(key, arg1);
	}

	public boolean getBoolean(String key) {
		return this.config.getBoolean(key);
	}

	public boolean getBoolean(String key, boolean arg1) {
		return this.config.getBoolean(key, arg1);
	}

	public Boolean getBoolean(String key, Boolean arg1) {
		return this.config.getBoolean(key, arg1);
	}

	public byte getByte(String key) {
		return this.config.getByte(key);
	}

	public byte getByte(String key, byte arg1) {
		return this.config.getByte(key, arg1);
	}

	public Byte getByte(String key, Byte arg1) {
		return this.config.getByte(key, arg1);
	}

	public double getDouble(String key) {
		return this.config.getDouble(key);
	}

	public double getDouble(String key, double arg1) {
		return this.config.getDouble(key, arg1);
	}

	public Double getDouble(String key, Double arg1) {
		return this.config.getDouble(key, arg1);
	}

	public float getFloat(String key) {
		return this.config.getFloat(key);
	}

	public float getFloat(String key, float arg1) {
		return this.config.getFloat(key, arg1);
	}

	public Float getFloat(String key, Float arg1) {
		return this.config.getFloat(key, arg1);
	}

	public int getInt(String key) {
		return this.config.getInt(key);
	}

	public int getInt(String key, int arg1) {
		return this.config.getInt(key, arg1);
	}

	public Integer getInteger(String key, Integer arg1) {
		return this.config.getInteger(key, arg1);
	}

	public Iterator getKeys() {
		return this.config.getKeys();
	}

	public Iterator getKeys(String key) {
		return this.config.getKeys(key);
	}

	public List getList(String key) {
		return this.config.getList(key);
	}

	public List getList(String key, List arg1) {
		return this.config.getList(key, arg1);
	}

	public long getLong(String key) {
		return this.config.getLong(key);
	}

	public long getLong(String key, long arg1) {
		return this.config.getLong(key, arg1);
	}

	public Long getLong(String key, Long arg1) {
		return this.config.getLong(key, arg1);
	}

	public Properties getProperties(String key) {
		return this.config.getProperties(key);
	}

	public Object getProperty(String key) {
		return this.config.getProperty(key);
	}

	public short getShort(String key) {
		return this.config.getShort(key);
	}

	public short getShort(String key, short arg1) {
		return this.config.getShort(key, arg1);
	}

	public Short getShort(String key, Short arg1) {
		return this.config.getShort(key, arg1);
	}

	public String getString(String key) {
		return this.config.getString(key);
	}

	public String getString(String key, String arg1) {
		return this.config.getString(key, arg1);
	}

	public String[] getStringArray(String key) {
		return this.config.getStringArray(key);
	}

	public boolean isEmpty() {
		return this.config.isEmpty();
	}

	public void setProperty(String key, Object arg1) {
		this.config.setProperty(key, arg1);
	}

	public org.apache.commons.configuration.Configuration subset(String prefix) {
		return this.config.subset(prefix);
	}

	Configuration setExpressionEngine(XPathExpressionEngine engine){
		((XMLConfiguration)config).setExpressionEngine(engine);
		return this;
	}
	
	/**
	 * only be useful for jndi
	 * @param prefix
	 */
	public void setPrefix(String prefix){
		if(config instanceof JNDIConfiguration){
			((JNDIConfiguration)config).setPrefix(prefix);
		}
	}
	
	/**
	 * 对于xml,peorerties,ini文件有效
	 * @param millis
	 */
	public void setReloadable(long millis){
		if(config instanceof XMLConfiguration){
			((XMLConfiguration)config).setReloadingStrategy(this.getReloadingStrategy(millis));
		}else if(config instanceof PropertiesConfiguration){
			((PropertiesConfiguration)config).setReloadingStrategy(this.getReloadingStrategy(millis));
		}else if(config instanceof INIConfiguration){
			((INIConfiguration)config).setReloadingStrategy(this.getReloadingStrategy(millis));
		}
	}
	
	private ReloadingStrategy getReloadingStrategy(long millis){
		ReloadingStrategy strategy = new FileChangedReloadingStrategy(); 
		((FileChangedReloadingStrategy)strategy).setRefreshDelay(millis);  
		return strategy;
	}
	
}
