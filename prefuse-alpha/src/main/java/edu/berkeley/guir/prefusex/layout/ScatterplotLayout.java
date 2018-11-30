//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package edu.berkeley.guir.prefusex.layout;

import edu.berkeley.guir.prefuse.ItemRegistry;
import edu.berkeley.guir.prefuse.NodeItem;
import edu.berkeley.guir.prefuse.VisualItem;
import edu.berkeley.guir.prefuse.action.assignment.Layout;

import java.awt.geom.Rectangle2D;
import java.util.Iterator;

public class ScatterplotLayout extends Layout {
	protected String xAttribute;
	protected String yAttribute;

	public ScatterplotLayout(String var1, String var2) {
		this.xAttribute = var1;
		this.yAttribute = var2;
	}

	protected double getXCoord(VisualItem var1) {
		return this.getCoord(var1, this.xAttribute);
	}

	protected double getYCoord(VisualItem var1) {
		return this.getCoord(var1, this.yAttribute);
	}

	protected double getCoord(VisualItem var1, String var2) {
		String var3 = var1.getAttribute(var2);

		try {
			return Double.parseDouble(var3);
		} catch (Exception var5) {
			System.err.println("Attribute \"" + var2 + "\" is not a valid numerical value.");
			return 0.0D / 0.0;
		}
	}

	public void run(ItemRegistry var1, double var2) {
		Rectangle2D var4 = this.getLayoutBounds(var1);
		double var5 = var4.getMinX();
		double var7 = var4.getMinY();
		Iterator var9 = var1.getNodeItems();

		while (var9.hasNext()) {
			NodeItem var10 = (NodeItem) var9.next();
			double var11 = this.getXCoord(var10);
			double var13 = this.getYCoord(var10);
			this.setLocation(var10, (VisualItem) null, var11, var13);
		}

	}
}
