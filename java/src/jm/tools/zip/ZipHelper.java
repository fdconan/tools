package jm.tools.zip;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Enumeration;

import jm.tools.path.Path;

import org.apache.commons.io.FileUtils;
import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;
import org.apache.tools.zip.ZipOutputStream;

/**
 * zipѹ��/��ѹ
 * @author yjm
 *
 */
public final class ZipHelper {
	private static final int BUFFEREDSIZE = 2048;
	private IZipProgressListener listener;
	
	public ZipHelper(){}
	public ZipHelper(IZipProgressListener listener){
		this.listener = listener;
	}
	
	public void unzip(String zipFilePath, String unzipDestFold)
			throws IOException {
		unzip(zipFilePath, unzipDestFold, Charset.defaultCharset()
				.displayName());
	}

	public void unzip(String zipFilePath, String unzipDestFold,
			String encoding) throws IOException {
		// ����ѹ���ļ�����
		File zipFile = new File(zipFilePath);
		ZipFile zf = new ZipFile(zipFile, encoding);
		// ��ȡѹ���ļ��е��ļ�ö��
		Enumeration en = zf.getEntries();
		int length = 0;
		byte[] b = new byte[BUFFEREDSIZE];
		// ��ȡѹ���ļ����е�����ѹ��ʵ������
		while (en.hasMoreElements()) {
			ZipEntry ze = (ZipEntry) en.nextElement();
			// System.out.println("ѹ���ļ����е����ݣ�"+ze.getName());
			// System.out.println("�Ƿ����ļ��У�"+ze.isDirectory());
			// ������ѹ������ļ�ʵ������
			File f = new File(Path.fromOSString(unzipDestFold).append(
					ze.getName()).toOSString());
			// System.out.println("��ѹ������ݣ�"+f.getPath());
			// System.out.println("�Ƿ����ļ��У�"+f.isDirectory());
			// �����ǰѹ���ļ��е�ʵ���������ļ��о��ڽ�ѹ������ļ����д������ļ���
			if (f.isDirectory()) {
				f.mkdirs();
			} else {
				// �����ǰ��ѹ���ļ��ĸ����ļ���û�д����Ļ����򴴽��ø����ļ���
				if (!f.getParentFile().exists()) {
					f.getParentFile().mkdirs();
				}
				// ����ǰ�ļ�������д���ѹ����ļ����С�
				OutputStream outputStream = new BufferedOutputStream(
						new FileOutputStream(f));
				InputStream inputStream = new BufferedInputStream(zf
						.getInputStream(ze));
				while ((length = inputStream.read(b)) > 0) {
					outputStream.write(b, 0, length);
					if(this.listener != null){
						this.listener.update(f, length, zipFile.length());
					}
				}
				outputStream.flush();
				outputStream.close();
				inputStream.close();

			}
		}
		zf.close();
	}

	public void zip(String sourcePath, OutputStream output, boolean includeParentPath,
			String encoding) throws IOException {
		ZipOutputStream zos = new ZipOutputStream(output);
		// ����ѹ����ʱ���ļ�������
		zos.setEncoding(encoding);
		zipOut(sourcePath, zos, includeParentPath);
	}

	public void zip(String sourcePath, OutputStream output, boolean includeParentPath)
			throws IOException {
		zip(sourcePath, output, includeParentPath, Charset.defaultCharset()
				.displayName());
	}

	public void zip(String sourcePath, String zipFileName, boolean includeParentPath,
			String encoding) throws IOException {
		ZipOutputStream zos = new ZipOutputStream(new File(zipFileName));
		// ����ѹ����ʱ���ļ�������
		zos.setEncoding(encoding);
		zipOut(sourcePath, zos, includeParentPath);
	}

	public void zip(String sourcePath, String zipFileName, boolean includeParentPath)
			throws IOException {
		zip(sourcePath, zipFileName, includeParentPath, Charset.defaultCharset()
				.displayName());
	}

	private void zipOut(String sourcePath, ZipOutputStream zos, boolean includeParentPath)
			throws IOException {
		File file = new File(sourcePath);
		long size = file.length();
		if(file.isDirectory()){
			size = FileUtils.sizeOfDirectory(file);
		}
		if (file.isDirectory()) {
			// �˴�ʹ��/����ʾĿ¼,���ʹ��\\����ʾĿ¼�Ļ�,�п��ܵ���ѹ������ļ�Ŀ¼��֯��ʽ�ڽ�ѹ����ʱ������ȷʶ��
			if(includeParentPath){
				zipDir(sourcePath, zos, file.getName() + "/", size);
			}else {
				zipDir(sourcePath, zos, "", size);
			}
		} else {
			// ���ֱ��ѹ���ļ�
			/*
			 * zipDir(file.getPath(), zos, new File(file.getParent()).getName()
			 * + "/");
			 * 
			 * zipFile(file.getPath(), zos, new File(file.getParent()).getName()
			 * + "/" + file.getName());
			 */
			if(includeParentPath){
				zipFile(file.getPath(), zos, new File(file.getParent()).getName()
						  + "/" + file.getName(), size);
			}else {
				zipFile(file.getPath(), zos, file.getName(), size);
			}
		}
		zos.flush();
		zos.closeEntry();
		zos.close();
	}

	private void zipDir(String sourceDir, ZipOutputStream zos,
			String tager, long totalBytes) throws IOException {
		// System.out.println(tager);
		ZipEntry ze = new ZipEntry(tager);
		zos.putNextEntry(ze);
		// ��ȡҪѹ�����ļ����е������ļ�
		File f = new File(sourceDir);
		File[] flist = f.listFiles();
		if (flist != null) {
			// ������ļ��������ļ�����ȡ���е��ļ�����ѹ��
			for (File fsub : flist) {
				if (fsub.isDirectory()) {
					// �����Ŀ¼�����Ŀ¼ѹ��
					zipDir(fsub.getPath(), zos, tager + fsub.getName() + "/", totalBytes);
				} else {
					// ������ļ���������ļ�ѹ��
					zipFile(fsub.getPath(), zos, tager + fsub.getName(), totalBytes);
				}
			}
		}
	}

	private void zipFile(String sourceFileName, ZipOutputStream zos,
			String tager, long totalBytes) throws IOException {
		// System.out.println(tager);
		ZipEntry ze = new ZipEntry(tager);
		zos.putNextEntry(ze);
		// ��ȡҪѹ���ļ���������ӵ�ѹ���ļ���
		File file = new File(sourceFileName);
		InputStream fis = new BufferedInputStream(new FileInputStream(file));
		byte[] bf = new byte[BUFFEREDSIZE];
		int location = 0;
		while ((location = fis.read(bf)) != -1) {
			zos.write(bf, 0, location);
			if(this.listener != null){
				this.listener.update(file, location, totalBytes);
			}
		}
		zos.flush();
		fis.close();
	}
}
