//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package edu.berkeley.guir.prefuse.action.animate;

import edu.berkeley.guir.prefuse.ItemRegistry;
import edu.berkeley.guir.prefuse.VisualItem;
import edu.berkeley.guir.prefuse.action.AbstractAction;
import edu.berkeley.guir.prefuse.util.ColorLib;
import java.awt.Color;
import java.awt.Paint;
import java.util.Iterator;

public class ColorAnimator extends AbstractAction {
	public ColorAnimator() {
	}

	public void run(ItemRegistry var1, double var2) {
		Iterator var4 = var1.getItems();

		while(var4.hasNext()) {
			VisualItem var5 = (VisualItem)var4.next();
			Paint var6 = var5.getStartColor();
			Paint var7 = var5.getEndColor();
			if (!(var6 instanceof Color) || !(var7 instanceof Color)) {
				throw new IllegalStateException("Can't interpolate Paint instances that are not of type Color");
			}

			Color var8 = (Color)var6;
			Color var9 = (Color)var7;
			var5.setColor(ColorLib.getIntermediateColor(var8, var9, var2));
			Paint var12 = var5.getStartFillColor();
			Paint var13 = var5.getEndFillColor();
			if (var12 instanceof Color && var13 instanceof Color) {
				Color var10 = (Color)var12;
				Color var11 = (Color)var13;
				var5.setFillColor(ColorLib.getIntermediateColor(var10, var11, var2));
			}
		}

	}
}
