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
 * zip压缩/解压
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
		// 创建压缩文件对象
		File zipFile = new File(zipFilePath);
		ZipFile zf = new ZipFile(zipFile, encoding);
		// 获取压缩文件中的文件枚举
		Enumeration en = zf.getEntries();
		int length = 0;
		byte[] b = new byte[BUFFEREDSIZE];
		// 提取压缩文件夹中的所有压缩实例对象
		while (en.hasMoreElements()) {
			ZipEntry ze = (ZipEntry) en.nextElement();
			// System.out.println("压缩文件夹中的内容："+ze.getName());
			// System.out.println("是否是文件夹："+ze.isDirectory());
			// 创建解压缩后的文件实例对象
			File f = new File(Path.fromOSString(unzipDestFold).append(
					ze.getName()).toOSString());
			// System.out.println("解压后的内容："+f.getPath());
			// System.out.println("是否是文件夹："+f.isDirectory());
			// 如果当前压缩文件中的实例对象是文件夹就在解压缩后的文件夹中创建该文件夹
			if (f.isDirectory()) {
				f.mkdirs();
			} else {
				// 如果当前解压缩文件的父级文件夹没有创建的话，则创建好父级文件夹
				if (!f.getParentFile().exists()) {
					f.getParentFile().mkdirs();
				}
				// 将当前文件的内容写入解压后的文件夹中。
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
		// 设置压缩的时候文件名编码
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
		// 设置压缩的时候文件名编码
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
			// 此处使用/来表示目录,如果使用\\来表示目录的话,有可能导致压缩后的文件目录组织形式在解压缩的时候不能正确识别。
			if(includeParentPath){
				zipDir(sourcePath, zos, file.getName() + "/", size);
			}else {
				zipDir(sourcePath, zos, "", size);
			}
		} else {
			// 如果直接压缩文件
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
		// 提取要压缩的文件夹中的所有文件
		File f = new File(sourceDir);
		File[] flist = f.listFiles();
		if (flist != null) {
			// 如果该文件夹下有文件则提取所有的文件进行压缩
			for (File fsub : flist) {
				if (fsub.isDirectory()) {
					// 如果是目录则进行目录压缩
					zipDir(fsub.getPath(), zos, tager + fsub.getName() + "/", totalBytes);
				} else {
					// 如果是文件，则进行文件压缩
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
		// 读取要压缩文件并将其添加到压缩文件中
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
