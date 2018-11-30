//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package edu.berkeley.guir.prefuse.action.animate;

import edu.berkeley.guir.prefuse.AggregateItem;
import edu.berkeley.guir.prefuse.Display;
import edu.berkeley.guir.prefuse.ItemRegistry;
import edu.berkeley.guir.prefuse.NodeItem;
import edu.berkeley.guir.prefuse.VisualItem;
import edu.berkeley.guir.prefuse.action.AbstractAction;
import edu.berkeley.guir.prefuse.event.FocusEvent;
import edu.berkeley.guir.prefuse.event.FocusListener;
import edu.berkeley.guir.prefuse.graph.Entity;
import edu.berkeley.guir.prefuse.graph.Node;
import edu.berkeley.guir.prefuse.graph.TreeNode;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class PolarLocationAnimator extends AbstractAction implements FocusListener {
	private static final double TWO_PI = 6.283185307179586D;
	private Point2D m_anchor = new Double();
	private Set m_linear = new HashSet();
	private ItemRegistry m_registry;

	public PolarLocationAnimator() {
	}

	private Point2D getAnchor(ItemRegistry var1) {
		Display var2 = var1.getDisplay(0);
		this.m_anchor.setLocation((double)(var2.getWidth() / 2), (double)(var2.getHeight() / 2));
		var2.getAbsoluteCoordinate(this.m_anchor, this.m_anchor);
		return this.m_anchor;
	}

	public void run(ItemRegistry var1, double var2) {
		if (var1 != this.m_registry) {
			if (this.m_registry != null) {
				this.m_registry.getDefaultFocusSet().removeFocusListener(this);
			}

			this.m_registry = var1;
			this.m_registry.getDefaultFocusSet().addFocusListener(this);
		}

		Point2D var4 = this.getAnchor(var1);
		double var5 = var4.getX();
		double var7 = var4.getY();
		Iterator var41 = this.m_registry.getItems();

		while(var41.hasNext()) {
			VisualItem var42 = (VisualItem)var41.next();
			Point2D var43 = var42.getStartLocation();
			Point2D var44 = var42.getEndLocation();
			double var9 = var43.getX() - var5;
			double var11 = var43.getY() - var7;
			double var13 = var44.getX() - var5;
			double var15 = var44.getY() - var7;
			double var17;
			double var19;
			double var21;
			double var23;
			double var27;
			double var31;
			double var35;
			double var37;
			double var39;
			if (this.m_linear.contains(var42)) {
				var17 = var43.getX() + var2 * (var44.getX() - var43.getX());
				var19 = var43.getY() + var2 * (var44.getY() - var43.getY());
				var42.setLocation(var17, var19);
			} else {
				double var25 = Math.sqrt(var9 * var9 + var11 * var11);
				if (var42 instanceof NodeItem && java.lang.Double.isNaN(var25)) {
					var25 = Math.sqrt(var9 * var9 + var11 * var11);
				}

				var27 = Math.atan2(var11, var9);
				double var29 = Math.sqrt(var13 * var13 + var15 * var15);
				if (var42 instanceof NodeItem && java.lang.Double.isNaN(var29)) {
					var29 = Math.sqrt(var13 * var13 + var15 * var15);
				}

				var31 = Math.atan2(var15, var13);
				var37 = this.translate(var27);
				var39 = this.translate(var31);
				var21 = var31 - var27;
				var23 = var39 - var37;
				if (Math.abs(var21) < Math.abs(var23)) {
					var35 = var27 + var2 * var21;
				} else {
					var35 = var37 + var2 * var23;
				}

				double var33 = var25 + var2 * (var29 - var25);
				var17 = (double)Math.round(var5 + var33 * Math.cos(var35));
				var19 = (double)Math.round(var7 + var33 * Math.sin(var35));
				var42.setLocation(var17, var19);
			}

			if (var42 instanceof AggregateItem) {
				AggregateItem var45 = (AggregateItem)var42;
				var27 = var45.getStartOrientation();
				var31 = var45.getEndOrientation();
				var37 = this.translate(var27);
				var39 = this.translate(var31);
				var21 = var31 - var27;
				var23 = var39 - var37;
				if (Math.abs(var21) < Math.abs(var23)) {
					var35 = var27 + var2 * var21;
				} else {
					var35 = var37 + var2 * var23;
				}

				var45.setOrientation(var35);
			}
		}

	}

	private double translate(double var1) {
		return var1 < 0.0D ? var1 + 6.283185307179586D : var1;
	}

	public void focusChanged(FocusEvent var1) {
		if (var1.getEventType() == 2) {
			this.m_linear.clear();
			Entity[] var2 = var1.getRemovedFoci();
			if (var2.length == 0) {
				return;
			}

			if (var2[0] instanceof Node) {
				for(Object var3 = this.m_registry.getNodeItem((Node)var2[0]); var3 != null; var3 = ((TreeNode)var3).getParent()) {
					this.m_linear.add(var3);
				}
			}
		}

	}
}
