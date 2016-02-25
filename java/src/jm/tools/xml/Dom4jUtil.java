package jm.tools.xml;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.dom4j.CharacterData;
import org.dom4j.Comment;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

/**
 * 
 * @author yjm
 *
 */
@SuppressWarnings("unchecked")
public abstract class Dom4jUtil {
	public static List getChildElementsByTagName(Element ele,
			String childEleName) {
		List nl = ele.elements();
		List childEles = new ArrayList();
		for (int i = 0; i < nl.size(); i++) {
			Node node = (Node)nl.get(i);
			if (((node instanceof Element))
					&& (nodeNameEquals(node, childEleName))) {
				childEles.add(node);
			}
		}
		return childEles;
	}

	public static Element getChildElementByTagName(Element ele,
			String childEleName) {
		List nl = ele.elements();
		for (int i = 0; i < nl.size(); i++) {
			Node node = (Node)nl.get(i);
			if (((node instanceof Element))
					&& (nodeNameEquals(node, childEleName))) {
				return (Element) node;
			}
		}
		return null;
	}

	public static String getChildElementValueByTagName(Element ele,
			String childEleName) {
		Element child = getChildElementByTagName(ele, childEleName);
		return child != null ? getTextValue(child) : null;
	}

	public static boolean nodeNameEquals(Node node, String desiredName) {
		if (node == null) {
			throw new IllegalArgumentException("Node must not be null");
		}
		if (desiredName == null) {
			throw new IllegalArgumentException("Desired name must not be null");
		}

		return desiredName.equals(node.getName());
	}

	public static String getTextValue(Element valueEle) {
		StringBuffer value = new StringBuffer();
		List nl = valueEle.elements();
		for (int i = 0; i < nl.size(); i++) {
			Node item = (Node)nl.get(i);
			if ((((item instanceof CharacterData)) && (!(item instanceof Comment)))
					) {
				value.append(item.getText());
			}
		}
		return value.toString();
	}
	/**
	 * 获取节点文本内容
	 * @param node
	 * @return
	 */
	public static String getNodeText(Node node) {
		return ((Element)node).getTextTrim();
	}
	
	/**
	 * 获取节点的属性值
	 * @param node 节点
	 * @param attributeName 属性名
	 * @return
	 */
	public static String getNodeAttribute(Node node, String attributeName) {
		Element elem = (Element)node;
		return elem.attributeValue(attributeName);
	}
	
	/**
	 * 获取节点的属性值
	 * @param node 节点
	 * @param attributeName 属性名
	 * @return
	 */
	public static int getNodeIntAttribute(Node node, String attributeName) {
		String value = getNodeAttribute(node, attributeName);
		return Integer.parseInt(value);
	}
	
	/**
	 * 获取节点的属性值
	 * @param node 节点
	 * @param attributeName 属性名
	 * @return
	 */
	public static boolean getNodeBooleanAttribute(Node node, String attributeName) {
		String value = getNodeAttribute(node, attributeName);
		return Boolean.valueOf(value).booleanValue();
	}
	
	/**
	 * 创建一个新的xml文档
	 * @return
	 */
	public static Document createDocument(){
		return DocumentHelper.createDocument();
	}
	
	/**
	 * 从xml内容中创建文档
	 * @param xmlcontent
	 * @return
	 */
	public static Document getDocument(String xmlcontent){
		try {
			return DocumentHelper.parseText(xmlcontent);
		} catch (DocumentException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * 从xml流中获取xml文档,读取xml流时不验证xml文档是否格式良好的
	 * @param xmlstream xml源
	 * @return
	 */
	public static Document getDocument(InputStream xmlstream){
		return getDocument(xmlstream, false);
	}
	
	/**
	 * 从xml流中获取xml文档
	 * @param xmlstream xml源
	 * @param validating 读取xml流时是否需要验证xml文档是否格式良好的
	 * @return
	 */
	public static Document getDocument(InputStream xmlstream, boolean validating){
		try {
			SAXReader reader = new SAXReader();
			reader.setValidation(validating);
			return reader.read(xmlstream);
		} catch (DocumentException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * 从文件中获取xml文档,读取文件时不验证xml文档是否格式良好的
	 * @param file xml源
	 * @return
	 * @throws Exception
	 */
	public static Document getDocument(File file)
			throws Exception {
		return getDocument(file, false);
	}

	/**
	 * 从文件中获取xml文档
	 * @param file xml源
	 * @param validating 读取文件时是否需要验证xml文档是否格式良好的
	 * @return
	 * @throws Exception
	 */
	public static Document getDocument(File file, boolean validating)
			throws Exception {
		SAXReader reader = new SAXReader();
		reader.setValidation(validating);
		return reader.read(file);
	}

	/**
	 * 写xml到文件
	 * @param doc xml文档
	 * @param file 目标文件
	 * @param encoding 字符集
	 * @throws Exception
	 */
	public static void toXMLFile(Document doc, File file, String encoding) throws Exception {
		OutputFormat format = OutputFormat.createPrettyPrint();
		format.setEncoding(encoding);
		format.setLineSeparator(getSystemLineSeparator());
		XMLWriter out = new XMLWriter(new BufferedOutputStream(new FileOutputStream(file)), format);
		out.setOutputStream(new BufferedOutputStream(new FileOutputStream(file)));
		out.write(doc);
		out.close();
	}
	
	/**
	 * 写xml到文件,默认字符集是GBK
	 * @param doc xml文档
	 * @param file 目标文件
	 * @throws Exception
	 */
	public static void toXMLFile(Document doc, File file) throws Exception {
		toXMLFile(doc, file, "GBK");
	}

	/**
	 * 利用xpath表达式查找单个节点
	 * @param source 节点源,可以是Document,Node,Element之一
	 * @param expression xpath表达式
	 * @return
	 */
	public static Node selectSingleNode(Object source, String expression){
		if(source instanceof Document){
			return ((Document)source).selectSingleNode(expression);
		}else if(source instanceof Node){
			return ((Node)source).selectSingleNode(expression);
		}else if(source instanceof Element){
			return ((Element)source).selectSingleNode(expression);
		}
		return null;
	}
	
	/**
	 * 利用xpath表达式查找多个节点
	 * @param source 节点源,可以是Document,Node,Element之一
	 * @param expression xpath表达式
	 * @return
	 */
	public static List selectNodes(Object source, String expression){
		if(source instanceof Document){
			return ((Document)source).selectNodes(expression);
		}else if(source instanceof Node){
			return ((Node)source).selectNodes(expression);
		}else if(source instanceof Element){
			return ((Element)source).selectNodes(expression);
		}
		return new ArrayList();
	}
	
	private static String getSystemLineSeparator(){
		String lineSeparator =	(String) java.security.AccessController.doPrivileged(
	               new sun.security.action.GetPropertyAction("line.separator"));
		return lineSeparator;
	}

}
