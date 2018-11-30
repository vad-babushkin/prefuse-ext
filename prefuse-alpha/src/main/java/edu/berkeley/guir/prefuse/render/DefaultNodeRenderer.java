//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package edu.berkeley.guir.prefuse.render;

import edu.berkeley.guir.prefuse.VisualItem;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Ellipse2D.Double;

public class DefaultNodeRenderer extends ShapeRenderer {
	private int m_radius = 5;
	private Ellipse2D m_circle;

	public DefaultNodeRenderer() {
		this.m_circle = new Double(0.0D, 0.0D, (double)(2 * this.m_radius), (double)(2 * this.m_radius));
	}

	public DefaultNodeRenderer(int var1) {
		this.m_circle = new Double(0.0D, 0.0D, (double)(2 * this.m_radius), (double)(2 * this.m_radius));
		this.setRadius(var1);
	}

	public void setRadius(int var1) {
		this.m_radius = var1;
		this.m_circle.setFrameFromCenter(0.0D, 0.0D, (double)var1, (double)var1);
	}

	public int getRadius() {
		return this.m_radius;
	}

	protected Shape getRawShape(VisualItem var1) {
		double var2 = (double)this.m_radius * var1.getSize();
		double var4 = var1.getX();
		double var6 = var1.getY();
		if (java.lang.Double.isNaN(var4)) {
			var4 = 0.0D;
		}

		if (java.lang.Double.isNaN(var6)) {
			var6 = 0.0D;
		}

		this.m_circle.setFrameFromCenter(var4, var6, var4 + var2, var6 + var2);
		return this.m_circle;
	}
}
