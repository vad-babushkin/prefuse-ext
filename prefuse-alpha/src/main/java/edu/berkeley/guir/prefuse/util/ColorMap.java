package edu.berkeley.guir.prefuse.util;

import java.awt.*;

public class ColorMap {
	public static final int DEFAULT_MAP_SIZE = 64;
	private Paint[] colorMap;
	private double minValue;
	private double maxValue;

	public ColorMap(Paint[] paramArrayOfPaint, double paramDouble1, double paramDouble2) {
		this.colorMap = paramArrayOfPaint;
		this.minValue = paramDouble1;
		this.maxValue = paramDouble2;
	}

	public Paint getColor(double paramDouble) {
		if (paramDouble < this.minValue) {
			return this.colorMap[0];
		}
		if (paramDouble > this.maxValue) {
			return this.colorMap[(this.colorMap.length - 1)];
		}
		int i = (int) Math.round((this.colorMap.length - 1) * (paramDouble - this.minValue) / (this.maxValue - this.minValue));
		return this.colorMap[i];
	}

	public Paint[] getColorMap() {
		return this.colorMap;
	}

	public void setColorMap(Paint[] paramArrayOfPaint) {
		this.colorMap = paramArrayOfPaint;
	}

	public double getMaxValue() {
		return this.maxValue;
	}

	public void setMaxValue(double paramDouble) {
		this.maxValue = paramDouble;
	}

	public double getMinValue() {
		return this.minValue;
	}

	public void setMinValue(double paramDouble) {
		this.minValue = paramDouble;
	}

	public static Paint[] getGrayscaleMap() {
		return getGrayscaleMap(64);
	}

	public static Paint[] getGrayscaleMap(int paramInt) {
		Paint[] arrayOfPaint = new Paint[paramInt];
		for (int i = 0; i < paramInt; i++) {
			float f = i / (paramInt - 1);
			arrayOfPaint[i] = ColorLib.getColor(f, f, f, 1.0F);
		}
		return arrayOfPaint;
	}

	public static Paint[] getInterpolatedMap(Color paramColor1, Color paramColor2) {
		return getInterpolatedMap(64, paramColor1, paramColor2);
	}

	public static Paint[] getInterpolatedMap(int paramInt, Color paramColor1, Color paramColor2) {
		Paint[] arrayOfPaint = new Paint[paramInt];
		for (int i = 0; i < paramInt; i++) {
			float f = i / (paramInt - 1);
			arrayOfPaint[i] = ColorLib.getIntermediateColor(paramColor1, paramColor2, f);
		}
		return arrayOfPaint;
	}

	public static Paint[] getHSBMap() {
		return getHSBMap(64, 1.0F, 1.0F);
	}

	public static Paint[] getHSBMap(int paramInt, float paramFloat1, float paramFloat2) {
		Paint[] arrayOfPaint = new Paint[paramInt];
		for (int i = 0; i < paramInt; i++) {
			float f = i / (paramInt - 1);
			arrayOfPaint[i] = ColorLib.getColor(Color.HSBtoRGB(f, paramFloat1, paramFloat2));
		}
		return arrayOfPaint;
	}

	public static Paint[] getHotMap() {
		return getHotMap(64);
	}

	public static Paint[] getHotMap(int paramInt) {
		Paint[] arrayOfPaint = new Paint[paramInt];
		for (int i = 0; i < paramInt; i++) {
			int j = 3 * paramInt / 8;
			float f1 = i < j ? (i + 1) / j : 1.0F;
			float f2 = i < 2 * j ? (i - j) / j : i < j ? 0.0F : 1.0F;
			float f3 = i < 2 * j ? 0.0F : (i - 2 * j) / (paramInt - 2 * j);
			arrayOfPaint[i] = ColorLib.getColor(f1, f2, f3, 1.0F);
		}
		return arrayOfPaint;
	}

	public static Paint[] getCoolMap() {
		return getCoolMap(64);
	}

	public static Paint[] getCoolMap(int paramInt) {
		Paint[] arrayOfPaint = new Paint[paramInt];
		for (int i = 0; i < paramInt; i++) {
			float f = i / Math.max(paramInt - 1, 1.0F);
			arrayOfPaint[i] = ColorLib.getColor(f, 1.0F - f, 1.0F, 1.0F);
		}
		return arrayOfPaint;
	}
}


/* Location:              /home/vad/work/JAVA/2018.11.30/prefuse-apps.jar!/edu/berkeley/guir/prefuse/util/ColorMap.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */