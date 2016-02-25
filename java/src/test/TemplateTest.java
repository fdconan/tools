import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jm.tools.template.ITemplateEngine;
import jm.tools.template.TemplateException;
import jm.tools.template.impl.PdfTemplateEngine;
import jm.tools.template.impl.TextFileTemplateEngine;
import jm.tools.template.impl.XlsTemplateEngine;


public class TemplateTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception{
		/*
		String path = "c://";
		ITemplateEngine engine = new TextFileTemplateEngine(path);
		File destFile = new File(path + "���߹���.doc");
		BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(destFile));
		Map<String, Object> params = new HashMap<String, Object>();
		
		params.put("INST_ID", "201402211612");
		params.put("RY", "2013");
		params.put("RM", "07");
		params.put("RD", "08");
		
		params.put("CY", "2013");
		params.put("CM", "09");
		params.put("CD", "09");
		params.put("CH", "07");
		
		params.put("CONTACT_SECRET", "������");
		params.put("ORDER_TYPE", "����");
		params.put("CONTACT_TEL", "13430243921");
		params.put("REQUEST_CONTENT", "����");
		params.put("CONTACT_NAME", "yjm");
		params.put("CONTACT_TITLE", "����");
		params.put("CONTACT_ADDRESS", "���");
		
		params.put("REQUEST_SUBJECT", "���̾�");
		params.put("SUBJECT_TEL", "85521717");
		params.put("SUBJECT_ADDR", "����");
		
		params.put("PY", "2014");
		params.put("PM", "01");
		params.put("PD", "10");
		//engine.transform("textfiletemplate.txt", destFile, params);
		engine.transform("���߹���.html", out, params);
		out.close();
		*/
		xlsTemplateEngineTest();
		
		//textTemplateEngineTest();
	}

	private static void xlsTemplateEngineTest() throws FileNotFoundException,
			TemplateException, IOException {
		String path = "c://";
		ITemplateEngine engine = new XlsTemplateEngine(path);
		File destFile = new File(path + "test2.xls");
		BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(destFile));
		Map<String, Object> params = new HashMap<String, Object>();
		
		List<ImportResult> list = new ArrayList<ImportResult>();  
		list.add(new ImportResult("13430243921","�ɹ�"));  
		list.add(new ImportResult("13580480336","�ɹ�"));  
		
		params.put("results", list);
		params.put("len", list.size());
		params.put("name", "yjm3");
		params.put("CONTACT_SECRET", "����");
		params.put("CONTACT_NAME", "capp");
		engine.transform("test.xls", out, params);
		//engine.transform("���߹���.xls", destFile, params);
		out.close();
	}

	private static void textTemplateEngineTest() throws FileNotFoundException,
			TemplateException, IOException {
		String path = "c://";
		ITemplateEngine engine = new TextFileTemplateEngine(path);
		File destFile = new File(path + "���߹���.doc");
		BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(destFile));
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("PY", "2013");
		params.put("PM", "12");
		params.put("PD", "18");
		
		params.put("RY", "2013");
		params.put("RM", "12");
		params.put("RD", "10");
		
		params.put("ORDER_TYPE", "����");
		params.put("CONTACT_TEL", "13430243921");
		params.put("REQUEST_CONTENT", "����");
		params.put("CONTACT_NAME", "yjm");
		params.put("CONTACT_TITLE", "����");
		params.put("CONTACT_ADDRESS", "���");
		//engine.transform("textfiletemplate.txt", destFile, params);
		engine.transform("���߹���.html", out, params);
		out.close();
	}

}

