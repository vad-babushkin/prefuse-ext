//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package edu.berkeley.guir.prefuse.action.assignment;

import edu.berkeley.guir.prefuse.Display;
import edu.berkeley.guir.prefuse.ItemRegistry;
import edu.berkeley.guir.prefuse.VisualItem;
import edu.berkeley.guir.prefuse.action.AbstractAction;
import java.awt.Insets;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Rectangle2D.Double;

public abstract class Layout extends AbstractAction {
	protected Rectangle2D m_bounds = null;
	protected Point2D m_anchor = null;
	private Insets m_insets = new Insets(0, 0, 0, 0);
	private double[] m_bpts = new double[4];
	private Rectangle2D m_tmp = new Double();

	public Layout() {
	}

	public Rectangle2D getLayoutBounds() {
		return this.m_bounds;
	}

	public Rectangle2D getLayoutBounds(ItemRegistry var1) {
		if (this.m_bounds != null) {
			return this.m_bounds;
		} else if (var1 != null && var1.getDisplayCount() > 0) {
			Display var2 = var1.getDisplay(0);
			Insets var3 = var2.getInsets(this.m_insets);
			this.m_bpts[0] = (double)var3.left;
			this.m_bpts[1] = (double)var3.top;
			this.m_bpts[2] = (double)(var2.getWidth() - var3.right);
			this.m_bpts[3] = (double)(var2.getHeight() - var3.bottom);
			var2.getInverseTransform().transform(this.m_bpts, 0, this.m_bpts, 0, 2);
			this.m_tmp.setRect(this.m_bpts[0], this.m_bpts[1], this.m_bpts[2] - this.m_bpts[0], this.m_bpts[3] - this.m_bpts[1]);
			return this.m_tmp;
		} else {
			return null;
		}
	}

	public void setLayoutBounds(Rectangle2D var1) {
		this.m_bounds = var1;
	}

	public Point2D getLayoutAnchor() {
		return this.m_anchor;
	}

	public Point2D getLayoutAnchor(ItemRegistry var1) {
		if (this.m_anchor != null) {
			return this.m_anchor;
		} else {
			java.awt.geom.Point2D.Double var2 = new java.awt.geom.Point2D.Double(0.0D, 0.0D);
			if (var1 != null) {
				Display var3 = var1.getDisplay(0);
				var2.setLocation((double)var3.getWidth() / 2.0D, (double)var3.getHeight() / 2.0D);
				var3.getInverseTransform().transform(var2, var2);
			}

			return var2;
		}
	}

	public void setLayoutAnchor(Point2D var1) {
		this.m_anchor = var1;
	}

	protected void setLocation(VisualItem var1, VisualItem var2, double var3, double var5) {
		if (java.lang.Double.isNaN(var1.getX())) {
			if (var2 != null) {
				var1.setLocation(var2.getStartLocation());
			} else {
				var1.setLocation(0.0D, 0.0D);
			}
		}

		var1.updateLocation(var3, var5);
		var1.setLocation(var3, var5);
	}

	public abstract void run(ItemRegistry var1, double var2);
}
