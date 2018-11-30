package edu.berkeley.guir.prefuse.util.io;

import java.io.File;

public class IOLib {
	public static String getExtension(File paramFile) {
		return paramFile != null ? getExtension(paramFile.getName()) : null;
	}

	public static String getExtension(String paramString) {
		int i = paramString.lastIndexOf('.');
		if ((i > 0) && (i < paramString.length() - 1)) {
			return paramString.substring(i + 1).toLowerCase();
		}
		return null;
	}
}


/* Location:              /home/vad/work/JAVA/2018.11.30/prefuse-apps.jar!/edu/berkeley/guir/prefuse/util/io/IOLib.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */