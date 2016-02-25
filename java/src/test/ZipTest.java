import java.io.File;

import jm.tools.zip.IZipProgressListener;
import jm.tools.zip.ZipHelper;


public class ZipTest implements IZipProgressListener {

	public void update(File currentFile, long bytesRead, long totalBytes) {
		System.out.println("���ڴ���:" + currentFile.getAbsolutePath() + ",�Ѵ���:" + bytesRead + ",�ֽ�,�ܹ�:" + totalBytes + "�ֽ�");
	}

	public static void main(String[] args) throws Exception{
		ZipTest l = new ZipTest();
		ZipHelper zh = new ZipHelper(l);
		//zh.unzip("H:\\yjm_tmp\\������ͳ��.zip", "H:\\yjm_tmp\\ziptest");
		zh.zip("H:\\yjm_tmp\\2013\\2013580300200 �����кŰٺ�����Ӫƽ̨����Ŀ��ǰִ��\\������ӡģ��\\eaptag2.war", "H:\\yjm_tmp\\ziptest.zip", false);
		//zip("H:\\yjm_tmp\\�Ӱ��걨��2.xls", "H:\\yjm_tmp\\�Ӱ��걨��2.xls.zip");
		//unzip("H:\\yjm_tmp\\�Ӱ��걨��2.xls.zip", "H:\\yjm_tmp\\");
	}
}
