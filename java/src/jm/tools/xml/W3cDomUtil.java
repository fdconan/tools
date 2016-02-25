package jm.tools.xml;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Attr;
import org.w3c.dom.CharacterData;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.EntityReference;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

/**
 * 
 * @author yjm
 *
 */
@SuppressWarnings("unchecked")
public abstract class W3cDomUtil {
	public static List getChildElementsByTagName(Element ele,
			String childEleName) {
		NodeList nl = ele.getChildNodes();
		List childEles = new ArrayList();
		for (int i = 0; i < nl.getLength(); i++) {
			Node node = nl.item(i);
			if (((node instanceof Element))
					&& (nodeNameEquals(node, childEleName))) {
				childEles.add(node);
			}
		}
		return childEles;
	}

	public static Element getChildElementByTagName(Element ele,
			String childEleName) {
		NodeList nl = ele.getChildNodes();
		for (int i = 0; i < nl.getLength(); i++) {
			Node node = nl.item(i);
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

		return (desiredName.equals(node.getNodeName()))
				|| (desiredName.equals(node.getLocalName()));
	}

	public static String getTextValue(Element valueEle) {
		StringBuffer value = new StringBuffer();
		NodeList nl = valueEle.getChildNodes();
		for (int i = 0; i < nl.getLength(); i++) {
			Node item = nl.item(i);
			if ((((item instanceof CharacterData)) && (!(item instanceof Comment)))
					|| ((item instanceof EntityReference))) {
				value.append(item.getNodeValue());
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
		String text = node.getTextContent();
		if(text == null){
			return null;
		}
		return text.trim();
	}
	
	/**
	 * 获取节点的属性值
	 * @param node 节点
	 * @param attributeName 属性名
	 * @return
	 */
	public static String getNodeAttribute(Node node, String attributeName) {
		NamedNodeMap map = node.getAttributes();
		Attr attribute = (Attr)map.getNamedItem(attributeName);
		if(attribute == null){
			return null;
		}
		return attribute.getValue();
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
		try {
			DocumentBuilder builder = getDocumentBuilder(false);
			return builder.newDocument();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * 从xml流中创建文档
	 * @param xmlcontent
	 * @return
	 */
	public static Document getDocument(String xmlcontent){
		try {
			DocumentBuilder builder = getDocumentBuilder(false);
			return builder.parse(new InputSource(new StringReader(xmlcontent)));
		} catch (Exception e) {
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
	 * @param validating 读取文件时是够需要验证xml文档是否格式良好的
	 * @return
	 * @throws Exception
	 */
	public static Document getDocument(File file, boolean validating)
			throws Exception {
		InputStream input = null;
		try {
			input = new BufferedInputStream(new FileInputStream(file));
			Document doc = getDocumentBuilder(validating).parse(input);
			return doc;
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				if (input != null)
					input.close();
				input = null;
			} catch (Exception e) {
			}
		}
	}
	
	/**
	 * 从stream获取xml文档,读取stream时不验证xml文档是否格式良好的
	 * @param xmlstream
	 * @return
	 */
	public static Document getDocument(InputStream xmlstream){
	   return getDocument(xmlstream, false);
	}
	
	/**
	 * 从stream获取xml文档
	 * @param xmlstream
	 * @param validating 读取stream时是否需要验证xml文档是否格式良好的
	 * @return
	 */
	public static Document getDocument(InputStream xmlstream, boolean validating){
	    Document doc = null;
	    if (xmlstream != null) {
	      try {
	    	return getDocumentBuilder(validating).parse(xmlstream);
	      } catch (Exception e) {
	        throw new RuntimeException(e);
	      }
	    }
	    return doc;
	}

	/**
	 * 写xml到文件
	 * @param doc xml文档
	 * @param file 目标文件
	 * @param encoding 字符集
	 * @throws Exception
	 */
	public static void toXMLFile(Document doc, File file, String encoding) throws Exception {
		
		OutputStream fileOut = null;
		try {
			fileOut = new BufferedOutputStream(new FileOutputStream(file));
			TransformerFactory factory = TransformerFactory.newInstance();
			Transformer transformer = factory.newTransformer();
			transformer.setOutputProperty("indent", "yes");
			transformer.setOutputProperty("method", "xml");
			transformer.setOutputProperty("encoding", encoding);
			
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(fileOut);
			transformer.transform(source, result);
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				if (fileOut != null)
					fileOut.close();
			} catch (Exception e) {
			}
		}
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
		XPathFactory xpathFactory = XPathFactory.newInstance();
		XPath xpath = xpathFactory.newXPath();
		try {
			Node node = (Node)xpath.evaluate(expression, source, XPathConstants.NODE);
			return node;
		} catch (XPathExpressionException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * 利用xpath表达式查找多个节点
	 * @param source 节点源,可以是Document,Node,Element之一
	 * @param expression xpath表达式
	 * @return
	 */
	public static List selectNodes(Object source, String expression){
		List list = new ArrayList();
		XPathFactory xpathFactory = XPathFactory.newInstance();
		XPath xpath = xpathFactory.newXPath();
		try {
			NodeList nodeList = (NodeList)xpath.evaluate(expression, source, XPathConstants.NODESET);
			if(nodeList == null){
				return list;
			}
			int len = nodeList.getLength();
			for(int i=0; i<len; ++i){
				list.add(nodeList.item(i));
			}
			return list;
		} catch (XPathExpressionException e) {
			throw new RuntimeException(e);
		}
	}

	private static DocumentBuilder getDocumentBuilder(boolean validating)
			throws ParserConfigurationException {
		DocumentBuilderFactory factory = DocumentBuilderFactory
				.newInstance();
		factory.setValidating(validating);
		DocumentBuilder builder = factory.newDocumentBuilder();
		return builder;
	}

}
