package edu.berkeley.guir.prefusex.distortion;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public class BifocalDistortion
		extends Distortion {
	private double rx;
	private double ry;
	private double mx;
	private double my;
	private boolean bx;
	private boolean by;

	public BifocalDistortion() {
		this(0.1D, 3.0D);
	}

	public BifocalDistortion(double paramDouble1, double paramDouble2) {
		this(paramDouble1, paramDouble2, paramDouble1, paramDouble2);
	}

	public BifocalDistortion(double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4) {
		this(paramDouble1, paramDouble2, paramDouble3, paramDouble4, false);
	}

	public BifocalDistortion(double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4, boolean paramBoolean) {
		super(paramBoolean);
		this.rx = paramDouble1;
		this.mx = paramDouble2;
		this.ry = paramDouble3;
		this.my = paramDouble4;
		this.bx = ((this.rx != 0.0D) && (this.mx != 1.0D));
		this.by = ((this.ry != 0.0D) && (this.my != 1.0D));
	}

	protected void transformPoint(Point2D paramPoint2D1, Point2D paramPoint2D2, Point2D paramPoint2D3, Rectangle2D paramRectangle2D) {
		double d1 = paramPoint2D1.getX();
		if (this.bx) {
			d1 = bifocal(d1, paramPoint2D3.getX(), this.rx, this.mx, paramRectangle2D.getMinX(), paramRectangle2D.getMaxX());
		}
		double d2 = paramPoint2D1.getY();
		if (this.by) {
			d2 = bifocal(paramPoint2D1.getY(), paramPoint2D3.getY(), this.ry, this.my, paramRectangle2D.getMinY(), paramRectangle2D.getMaxY());
		}
		paramPoint2D2.setLocation(d1, d2);
	}

	protected double transformSize(Rectangle2D paramRectangle2D1, Point2D paramPoint2D1, Point2D paramPoint2D2, Rectangle2D paramRectangle2D2) {
		int i = 0;
		int j = 0;
		double d2;
		double d3;
		double d4;
		double d5;
		double d1;
		if (this.bx) {
			d2 = paramRectangle2D1.getCenterX();
			d3 = paramPoint2D2.getX();
			d4 = paramRectangle2D2.getMinX();
			d5 = paramRectangle2D2.getMaxX();
			d1 = d2 < d3 ? d3 - d4 : d5 - d3;
			if (d1 == 0.0D) {
				d1 = d5 - d4;
			}
			if (Math.abs(d2 - d3) <= this.rx * d1) {
				i = 1;
			}
		}
		if (this.by) {
			d2 = paramRectangle2D1.getCenterY();
			d3 = paramPoint2D2.getY();
			d4 = paramRectangle2D2.getMinY();
			d5 = paramRectangle2D2.getMaxY();
			d1 = d2 < d3 ? d3 - d4 : d5 - d3;
			if (d1 == 0.0D) {
				d1 = d5 - d4;
			}
			if (Math.abs(d2 - d3) <= this.ry * d1) {
				j = 1;
			}
		}
		if ((i != 0) && (!this.by)) {
			return this.mx;
		}
		if ((j != 0) && (!this.bx)) {
			return this.my;
		}
		if ((i != 0) && (j != 0)) {
			return Math.min(this.mx, this.my);
		}
		return Math.min((1.0D - this.rx * this.mx) / (1.0D - this.rx), (1.0D - this.ry * this.my) / (1.0D - this.ry));
	}

	private double bifocal(double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4, double paramDouble5, double paramDouble6) {
		double d1 = paramDouble1 < paramDouble2 ? paramDouble2 - paramDouble5 : paramDouble6 - paramDouble2;
		if (d1 == 0.0D) {
			d1 = paramDouble6 - paramDouble5;
		}
		double d2 = paramDouble1 - paramDouble2;
		double d3 = d1 * paramDouble3;
		if (Math.abs(d2) <= d3) {
			return paramDouble1 = d2 * paramDouble4 + paramDouble2;
		}
		double d4 = paramDouble3 * paramDouble4;
		paramDouble1 = (Math.abs(d2) - d3) / d1 * ((1.0D - d4) / (1.0D - paramDouble3));
		return (d2 < 0.0D ? -1 : 1) * d1 * (paramDouble1 + d4) + paramDouble2;
	}

	public double getXMagnification() {
		return this.mx;
	}

	public void setXMagnification(double paramDouble) {
		this.mx = paramDouble;
	}

	public double getYMagnification() {
		return this.my;
	}

	public void setYMagnification(double paramDouble) {
		this.my = paramDouble;
	}

	public double getXRange() {
		return this.rx;
	}

	public void setXRange(double paramDouble) {
		this.rx = paramDouble;
	}

	public double getYRange() {
		return this.ry;
	}

	public void setYRange(double paramDouble) {
		this.ry = paramDouble;
	}
}


/* Location:              /home/vad/work/JAVA/2018.11.30/prefuse-apps.jar!/edu/berkeley/guir/prefusex/distortion/BifocalDistortion.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */