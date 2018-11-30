package edu.berkeley.guir.prefuse.util.display;

import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

public class Clip {
	private double[] clip = new double[8];
	private double[] tmp = new double[8];

	public void setClip(double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4) {
		this.clip[0] = paramDouble1;
		this.clip[1] = paramDouble2;
		this.clip[2] = paramDouble1;
		this.clip[3] = paramDouble4;
		this.clip[4] = paramDouble3;
		this.clip[5] = paramDouble2;
		this.clip[6] = paramDouble3;
		this.clip[7] = paramDouble4;
	}

	public void setClip(Clip paramClip) {
		System.arraycopy(paramClip.clip, 0, this.clip, 0, 4);
	}

	public void setClip(Rectangle2D paramRectangle2D) {
		this.clip[0] = paramRectangle2D.getX();
		this.clip[1] = paramRectangle2D.getY();
		this.clip[6] = (this.clip[0] + paramRectangle2D.getWidth());
		this.clip[7] = (this.clip[1] + paramRectangle2D.getHeight());
		this.clip[2] = this.clip[0];
		this.clip[3] = this.clip[7];
		this.clip[4] = this.clip[6];
		this.clip[5] = this.clip[1];
	}

	public void transform(AffineTransform paramAffineTransform) {
		paramAffineTransform.transform(this.clip, 0, this.tmp, 0, 4);
		double[] arrayOfDouble = this.tmp;
		this.tmp = this.clip;
		this.clip = arrayOfDouble;
		double d1 = this.clip[0];
		double d2 = this.clip[1];
		double d3 = this.clip[6];
		double d4 = this.clip[7];
		for (int i = 0; i < 7; i += 2) {
			if (this.clip[i] < d1) {
				d1 = this.clip[i];
			}
			if (this.clip[i] > d3) {
				d3 = this.clip[i];
			}
			if (this.clip[(i + 1)] < d2) {
				d2 = this.clip[(i + 1)];
			}
			if (this.clip[(i + 1)] > d4) {
				d4 = this.clip[(i + 1)];
			}
		}
		this.clip[0] = d1;
		this.clip[1] = d2;
		this.clip[2] = d1;
		this.clip[3] = d4;
		this.clip[4] = d3;
		this.clip[5] = d2;
		this.clip[6] = d3;
		this.clip[7] = d4;
	}

	public void limit(double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4) {
		this.clip[0] = Math.max(this.clip[0], paramDouble1);
		this.clip[1] = Math.max(this.clip[1], paramDouble2);
		this.clip[6] = Math.min(this.clip[6], paramDouble3);
		this.clip[7] = Math.min(this.clip[7], paramDouble4);
		this.clip[2] = this.clip[0];
		this.clip[3] = this.clip[7];
		this.clip[4] = this.clip[6];
		this.clip[5] = this.clip[1];
	}

	public boolean intersects(Rectangle2D paramRectangle2D) {
		double d1 = this.clip[6] - this.clip[0];
		double d2 = this.clip[7] - this.clip[1];
		double d3 = paramRectangle2D.getWidth();
		double d4 = paramRectangle2D.getHeight();
		if ((d3 < 0.0D) || (d4 < 0.0D) || (d1 < 0.0D) || (d2 < 0.0D)) {
			return false;
		}
		double d5 = this.clip[0];
		double d6 = this.clip[1];
		double d7 = paramRectangle2D.getX();
		double d8 = paramRectangle2D.getY();
		d3 += d7;
		d4 += d8;
		d1 += d5;
		d2 += d6;
		return ((d3 < d7) || (d3 > d5)) && ((d4 < d8) || (d4 > d6)) && ((d1 < d5) || (d1 > d7)) && ((d2 < d6) || (d2 > d8));
	}

	public void union(Clip paramClip) {
		this.clip[0] = Math.min(this.clip[0], paramClip.clip[0]);
		this.clip[1] = Math.min(this.clip[1], paramClip.clip[1]);
		this.clip[2] = Math.max(this.clip[6], paramClip.clip[6]);
		this.clip[3] = Math.max(this.clip[7], paramClip.clip[7]);
	}

	public void union(Rectangle2D paramRectangle2D) {
		this.clip[0] = Math.min(this.clip[0], paramRectangle2D.getX() - 1.0D);
		this.clip[1] = Math.min(this.clip[1], paramRectangle2D.getY() - 1.0D);
		this.clip[2] = Math.max(this.clip[6], paramRectangle2D.getX() + paramRectangle2D.getWidth() + 1.0D);
		this.clip[3] = Math.max(this.clip[7], paramRectangle2D.getX() + paramRectangle2D.getHeight() + 1.0D);
	}

	public double getX() {
		return this.clip[0];
	}

	public double getY() {
		return this.clip[1];
	}

	public double getWidth() {
		return this.clip[6] - this.clip[0];
	}

	public double getHeight() {
		return this.clip[7] - this.clip[1];
	}
}


/* Location:              /home/vad/work/JAVA/2018.11.30/prefuse-apps.jar!/edu/berkeley/guir/prefuse/util/display/Clip.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */