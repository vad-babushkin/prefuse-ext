package edu.berkeley.guir.prefuse.util.io;

import javax.swing.filechooser.FileFilter;
import java.io.File;

public class SimpleFileFilter
		extends FileFilter {
	private String ext;
	private String desc;

	public SimpleFileFilter(String paramString1, String paramString2) {
		this.ext = paramString1;
		this.desc = paramString2;
	}

	public boolean accept(File paramFile) {
		if (paramFile == null) {
			return false;
		}
		if (paramFile.isDirectory()) {
			return true;
		}
		String str = IOLib.getExtension(paramFile);
		return (str != null) && (str.equals(this.ext));
	}

	public String getDescription() {
		return this.desc;
	}

	public String getExtension() {
		return this.ext;
	}
}


/* Location:              /home/vad/work/JAVA/2018.11.30/prefuse-apps.jar!/edu/berkeley/guir/prefuse/util/io/SimpleFileFilter.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */