package edu.berkeley.guir.prefusex.layout;

import edu.berkeley.guir.prefuse.ItemRegistry;
import edu.berkeley.guir.prefuse.VisualItem;
import edu.berkeley.guir.prefuse.action.assignment.Layout;

import java.awt.geom.Rectangle2D;
import java.util.Iterator;

public class RandomLayout
		extends Layout {
	public void run(ItemRegistry paramItemRegistry, double paramDouble) {
		Rectangle2D localRectangle2D = getLayoutBounds(paramItemRegistry);
		double d3 = localRectangle2D.getWidth();
		double d4 = localRectangle2D.getHeight();
		Iterator localIterator = paramItemRegistry.getNodeItems();
		while (localIterator.hasNext()) {
			VisualItem localVisualItem = (VisualItem) localIterator.next();
			double d1 = localRectangle2D.getX() + Math.random() * d3;
			double d2 = localRectangle2D.getY() + Math.random() * d4;
			setLocation(localVisualItem, null, d1, d2);
		}
	}
}


/* Location:              /home/vad/work/JAVA/2018.11.30/prefuse-apps.jar!/edu/berkeley/guir/prefusex/layout/RandomLayout.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */