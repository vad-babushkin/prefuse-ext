package edu.berkeley.guir.prefuse.util;

import java.awt.*;
import java.util.HashMap;

public class FontLib {
	private static final HashMap fontMap = new HashMap();
	private static int misses = 0;
	private static int lookups = 0;

	public static Font getFont(String paramString, int paramInt1, int paramInt2) {
		Integer localInteger = new Integer((paramString.hashCode() << 8) + (paramInt2 << 2) + paramInt1);
		Font localFont = null;
		if ((localFont = (Font) fontMap.get(localInteger)) == null) {
			localFont = new Font(paramString, paramInt1, paramInt2);
			fontMap.put(localInteger, localFont);
			misses += 1;
		}
		lookups += 1;
		return localFont;
	}

	public static int getCacheMissCount() {
		return misses;
	}

	public static int getCacheLookupCount() {
		return lookups;
	}

	public static void clearCache() {
		fontMap.clear();
	}

	public static Font getIntermediateFont(Font paramFont1, Font paramFont2, double paramDouble) {
		String str;
		int j;
		if (paramDouble < 0.5D) {
			str = paramFont1.getName();
			j = paramFont1.getStyle();
		} else {
			str = paramFont2.getName();
			j = paramFont2.getStyle();
		}
		int i = (int) Math.round(paramDouble * paramFont2.getSize() + (1.0D - paramDouble) * paramFont1.getSize());
		return getFont(str, j, i);
	}
}


/* Location:              /home/vad/work/JAVA/2018.11.30/prefuse-apps.jar!/edu/berkeley/guir/prefuse/util/FontLib.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */