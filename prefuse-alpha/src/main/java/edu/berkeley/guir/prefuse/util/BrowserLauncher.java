package edu.berkeley.guir.prefuse.util;

import java.io.IOException;
import java.net.URL;

public abstract class BrowserLauncher {
	private static final String WIN_ID = "Windows";
	private static final String WIN_PATH = "rundll32";
	private static final String WIN_FLAG = "url.dll,FileProtocolHandler";
	private static final String UNIX_PATH = "netscape";
	private static final String UNIX_FLAG = "-remote openURL";

	public static void showDocument(URL paramURL) {
		showDocument(paramURL.toString());
	}

	public static void showDocument(String paramString) {
		boolean bool = isWindowsPlatform();
		String str = null;
		try {
			Process localProcess;
			if (bool) {
				str = "rundll32 url.dll,FileProtocolHandler " + paramString;
				localProcess = Runtime.getRuntime().exec(str);
			} else {
				str = "netscape -remote openURL(" + paramString + ")";
				localProcess = Runtime.getRuntime().exec(str);
				try {
					int i = localProcess.waitFor();
					if (i != 0) {
						str = "netscape " + paramString;
						localProcess = Runtime.getRuntime().exec(str);
					}
				} catch (InterruptedException localInterruptedException) {
					System.err.println("Error bringing up browser, cmd='" + str + "'");
					System.err.println("Caught: " + localInterruptedException);
				}
			}
		} catch (IOException localIOException) {
			System.err.println("Could not invoke browser, command=" + str);
			System.err.println("Caught: " + localIOException);
		}
	}

	public static boolean isWindowsPlatform() {
		String str = System.getProperty("os.name");
		return (str != null) && (str.startsWith("Windows"));
	}

	public static void main(String[] paramArrayOfString) {
		showDocument(paramArrayOfString[0]);
	}
}


/* Location:              /home/vad/work/JAVA/2018.11.30/prefuse-apps.jar!/edu/berkeley/guir/prefuse/util/BrowserLauncher.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */