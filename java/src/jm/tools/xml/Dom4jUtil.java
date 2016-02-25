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
	 * ��ȡ�ڵ��ı�����
	 * @param node
	 * @return
	 */
	public static String getNodeText(Node node) {
		return ((Element)node).getTextTrim();
	}
	
	/**
	 * ��ȡ�ڵ������ֵ
	 * @param node �ڵ�
	 * @param attributeName ������
	 * @return
	 */
	public static String getNodeAttribute(Node node, String attributeName) {
		Element elem = (Element)node;
		return elem.attributeValue(attributeName);
	}
	
	/**
	 * ��ȡ�ڵ������ֵ
	 * @param node �ڵ�
	 * @param attributeName ������
	 * @return
	 */
	public static int getNodeIntAttribute(Node node, String attributeName) {
		String value = getNodeAttribute(node, attributeName);
		return Integer.parseInt(value);
	}
	
	/**
	 * ��ȡ�ڵ������ֵ
	 * @param node �ڵ�
	 * @param attributeName ������
	 * @return
	 */
	public static boolean getNodeBooleanAttribute(Node node, String attributeName) {
		String value = getNodeAttribute(node, attributeName);
		return Boolean.valueOf(value).booleanValue();
	}
	
	/**
	 * ����һ���µ�xml�ĵ�
	 * @return
	 */
	public static Document createDocument(){
		return DocumentHelper.createDocument();
	}
	
	/**
	 * ��xml�����д����ĵ�
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
	 * ��xml���л�ȡxml�ĵ�,��ȡxml��ʱ����֤xml�ĵ��Ƿ��ʽ���õ�
	 * @param xmlstream xmlԴ
	 * @return
	 */
	public static Document getDocument(InputStream xmlstream){
		return getDocument(xmlstream, false);
	}
	
	/**
	 * ��xml���л�ȡxml�ĵ�
	 * @param xmlstream xmlԴ
	 * @param validating ��ȡxml��ʱ�Ƿ���Ҫ��֤xml�ĵ��Ƿ��ʽ���õ�
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
	 * ���ļ��л�ȡxml�ĵ�,��ȡ�ļ�ʱ����֤xml�ĵ��Ƿ��ʽ���õ�
	 * @param file xmlԴ
	 * @return
	 * @throws Exception
	 */
	public static Document getDocument(File file)
			throws Exception {
		return getDocument(file, false);
	}

	/**
	 * ���ļ��л�ȡxml�ĵ�
	 * @param file xmlԴ
	 * @param validating ��ȡ�ļ�ʱ�Ƿ���Ҫ��֤xml�ĵ��Ƿ��ʽ���õ�
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
	 * дxml���ļ�
	 * @param doc xml�ĵ�
	 * @param file Ŀ���ļ�
	 * @param encoding �ַ���
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
	 * дxml���ļ�,Ĭ���ַ�����GBK
	 * @param doc xml�ĵ�
	 * @param file Ŀ���ļ�
	 * @throws Exception
	 */
	public static void toXMLFile(Document doc, File file) throws Exception {
		toXMLFile(doc, file, "GBK");
	}

	/**
	 * ����xpath���ʽ���ҵ����ڵ�
	 * @param source �ڵ�Դ,������Document,Node,Element֮һ
	 * @param expression xpath���ʽ
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
	 * ����xpath���ʽ���Ҷ���ڵ�
	 * @param source �ڵ�Դ,������Document,Node,Element֮һ
	 * @param expression xpath���ʽ
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
