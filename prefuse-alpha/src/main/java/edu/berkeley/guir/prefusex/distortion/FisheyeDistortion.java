//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package edu.berkeley.guir.prefusex.distortion;

import edu.berkeley.guir.prefuse.ItemRegistry;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public class FisheyeDistortion extends Distortion {
	private double dx;
	private double dy;
	private boolean bx;
	private boolean by;
	private double sz;

	public FisheyeDistortion() {
		this(4.0D);
	}

	public FisheyeDistortion(double var1) {
		this(var1, var1);
	}

	public FisheyeDistortion(double var1, double var3) {
		this(var1, var3, false);
	}

	public FisheyeDistortion(double var1, double var3, boolean var5) {
		super(var5);
		this.sz = 3.0D;
		this.dx = var1;
		this.dy = var3;
		this.bx = this.dx > 0.0D;
		this.by = this.dy > 0.0D;
	}

	public double getXDistortionFactor() {
		return this.dx;
	}

	public void setXDistortionFactor(double var1) {
		this.dx = var1;
	}

	public double getYDistortionFactor() {
		return this.dy;
	}

	public void setYDistortionFactor(double var1) {
		this.dy = var1;
	}

	protected void transformPoint(Point2D var1, Point2D var2, Point2D var3, Rectangle2D var4) {
		double var5 = var1.getX();
		double var7 = var1.getY();
		if (this.bx) {
			var5 = this.fisheye(var5, var3.getX(), this.dx, var4.getMinX(), var4.getMaxX());
		}

		if (this.by) {
			var7 = this.fisheye(var7, var3.getY(), this.dy, var4.getMinY(), var4.getMaxY());
		}

		var2.setLocation(var5, var7);
	}

	protected double transformSize(Rectangle2D var1, Point2D var2, Point2D var3, Rectangle2D var4) {
		if (!this.bx && !this.by) {
			return 1.0D;
		} else {
			double var5 = 1.0D;
			double var7 = 1.0D;
			double var9;
			double var11;
			double var13;
			double var15;
			if (this.bx) {
				var9 = var3.getX();
				var11 = var1.getX();
				var13 = var1.getMaxX();
				var15 = Math.abs(var11 - var9) > Math.abs(var13 - var9) ? var11 : var13;
				if (var15 < var4.getMinX() || var15 > var4.getMaxX()) {
					var15 = var15 == var11 ? var13 : var11;
				}

				var5 = this.fisheye(var15, var9, this.dx, var4.getMinX(), var4.getMaxX());
				var5 = Math.abs(var2.getX() - var5) / var1.getWidth();
			}

			if (this.by) {
				var9 = var3.getY();
				var11 = var1.getY();
				var13 = var1.getMaxY();
				var15 = Math.abs(var11 - var9) > Math.abs(var13 - var9) ? var11 : var13;
				if (var15 < var4.getMinY() || var15 > var4.getMaxY()) {
					var15 = var15 == var11 ? var13 : var11;
				}

				var7 = this.fisheye(var15, var9, this.dy, var4.getMinY(), var4.getMaxY());
				var7 = Math.abs(var2.getY() - var7) / var1.getHeight();
			}

			var9 = !this.by ? var5 : (!this.bx ? var7 : Math.min(var5, var7));
			return !Double.isInfinite(var9) && !Double.isNaN(var9) ? this.sz * var9 : 1.0D;
		}
	}

	private double fisheye(double var1, double var3, double var5, double var7, double var9) {
		if (var5 != 0.0D) {
			boolean var11 = var1 < var3;
			double var14 = var11 ? var3 - var7 : var9 - var3;
			if (var14 == 0.0D) {
				var14 = var9 - var7;
			}

			double var12 = Math.abs(var1 - var3) / var14;
			var12 = (var5 + 1.0D) / (var5 + 1.0D / var12);
			return (double)(var11 ? -1 : 1) * var14 * var12 + var3;
		} else {
			return var1;
		}
	}

	public double fisheyeMove(double var1, ItemRegistry var3) {
		Rectangle2D var4 = this.getLayoutBounds(var3);
		Point2D var5 = this.correct(this.getLayoutAnchor(), var4);
		return var5 != null ? this.fisheye(var1, var5.getX(), this.dx, var4.getMinX(), var4.getMaxX()) : var1;
	}
}
