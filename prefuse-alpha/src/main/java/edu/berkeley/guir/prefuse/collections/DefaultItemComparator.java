//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package edu.berkeley.guir.prefuse.collections;

import edu.berkeley.guir.prefuse.AggregateItem;
import edu.berkeley.guir.prefuse.EdgeItem;
import edu.berkeley.guir.prefuse.NodeItem;
import edu.berkeley.guir.prefuse.VisualItem;
import java.util.Comparator;

public class DefaultItemComparator implements Comparator {
	public DefaultItemComparator() {
	}

	public int compare(Object var1, Object var2) {
		if (var1 instanceof VisualItem && var2 instanceof VisualItem) {
			VisualItem var3 = (VisualItem)var1;
			VisualItem var4 = (VisualItem)var2;
			boolean var5 = var3.isFocus();
			boolean var6 = var4.isFocus();
			if (var5 && !var6) {
				return 1;
			} else if (!var5 && var6) {
				return -1;
			} else {
				boolean var7 = var3.isHighlighted();
				boolean var8 = var4.isHighlighted();
				boolean var9 = var3 instanceof NodeItem;
				boolean var10 = var4 instanceof NodeItem;
				if (var9 && !var10) {
					return 1;
				} else if (!var9 && var10) {
					return -1;
				} else {
					boolean var11;
					boolean var12;
					if (var9 && var10) {
						if (var7 && !var8) {
							return 1;
						} else if (!var7 && var8) {
							return -1;
						} else {
							var11 = var3 instanceof AggregateItem;
							var12 = var4 instanceof AggregateItem;
							return var11 && !var12 ? -1 : (!var11 && var12 ? 1 : 0);
						}
					} else {
						var11 = var3 instanceof EdgeItem;
						var12 = var4 instanceof EdgeItem;
						if (var11 && !var12) {
							return 1;
						} else if (!var11 && var12) {
							return -1;
						} else if (var7 && !var8) {
							return 1;
						} else {
							return !var7 && var8 ? -1 : 0;
						}
					}
				}
			}
		} else {
			throw new IllegalArgumentException();
		}
	}
}
