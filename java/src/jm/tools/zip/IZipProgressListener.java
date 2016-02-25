package jm.tools.zip;

import java.io.File;

public interface IZipProgressListener {
	public void update(File currentFile, long pBytesRead, long pTotalBytes);
}
