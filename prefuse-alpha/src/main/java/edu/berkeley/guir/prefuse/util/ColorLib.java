package edu.berkeley.guir.prefuse.util;

import java.awt.*;
import java.util.HashMap;

public class ColorLib {
	private static final HashMap colorMap = new HashMap();
	private static int misses = 0;
	private static int lookups = 0;

	public static Color getColor(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4) {
		int i = ((int) (paramFloat4 * 255.0F + 0.5D) & 0xFF) << 24 | ((int) (paramFloat1 * 255.0F + 0.5D) & 0xFF) << 16 | ((int) (paramFloat2 * 255.0F + 0.5D) & 0xFF) << 8 | (int) (paramFloat3 * 255.0F + 0.5D) & 0xFF;
		return getColor(i);
	}

	public static Color getColor(float paramFloat1, float paramFloat2, float paramFloat3) {
		return getColor(paramFloat1, paramFloat2, paramFloat3, 1.0F);
	}

	public static Color getColor(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
		int i = (paramInt4 & 0xFF) << 24 | (paramInt1 & 0xFF) << 16 | (paramInt2 & 0xFF) << 8 | (paramInt3 & 0xFF) << 0;
		return getColor(i);
	}

	public static Color getColor(int paramInt1, int paramInt2, int paramInt3) {
		return getColor(paramInt1, paramInt2, paramInt3, 255);
	}

	public static Color getColor(int paramInt) {
		Integer localInteger = new Integer(paramInt);
		Color localColor = null;
		if ((localColor = (Color) colorMap.get(localInteger)) == null) {
			localColor = new Color(paramInt);
			colorMap.put(localInteger, localColor);
			misses += 1;
		}
		lookups += 1;
		return localColor;
	}

	public static int getCacheMissCount() {
		return misses;
	}

	public static int getCacheLookupCount() {
		return lookups;
	}

	public static void clearCache() {
		colorMap.clear();
	}

	public static Color getIntermediateColor(Color paramColor1, Color paramColor2, double paramDouble) {
		return getColor((int) Math.round(paramDouble * paramColor2.getRed() + (1.0D - paramDouble) * paramColor1.getRed()), (int) Math.round(paramDouble * paramColor2.getGreen() + (1.0D - paramDouble) * paramColor1.getGreen()), (int) Math.round(paramDouble * paramColor2.getBlue() + (1.0D - paramDouble) * paramColor1.getBlue()), (int) Math.round(paramDouble * paramColor2.getAlpha() + (1.0D - paramDouble) * paramColor1.getAlpha()));
	}
}


/* Location:              /home/vad/work/JAVA/2018.11.30/prefuse-apps.jar!/edu/berkeley/guir/prefuse/util/ColorLib.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */