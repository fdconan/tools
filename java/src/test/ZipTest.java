import java.io.File;

import jm.tools.zip.IZipProgressListener;
import jm.tools.zip.ZipHelper;


public class ZipTest implements IZipProgressListener {

	public void update(File currentFile, long bytesRead, long totalBytes) {
		System.out.println("正在处理:" + currentFile.getAbsolutePath() + ",已处理:" + bytesRead + ",字节,总共:" + totalBytes + "字节");
	}

	public static void main(String[] args) throws Exception{
		ZipTest l = new ZipTest();
		ZipHelper zh = new ZipHelper(l);
		//zh.unzip("H:\\yjm_tmp\\月周天统计.zip", "H:\\yjm_tmp\\ziptest");
		zh.zip("H:\\yjm_tmp\\2013\\2013580300200 广州市号百合作运营平台建项目提前执行\\工单打印模板\\eaptag2.war", "H:\\yjm_tmp\\ziptest.zip", false);
		//zip("H:\\yjm_tmp\\加班申报单2.xls", "H:\\yjm_tmp\\加班申报单2.xls.zip");
		//unzip("H:\\yjm_tmp\\加班申报单2.xls.zip", "H:\\yjm_tmp\\");
	}
}
