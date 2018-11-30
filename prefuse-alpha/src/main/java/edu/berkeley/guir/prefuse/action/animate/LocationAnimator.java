package edu.berkeley.guir.prefuse.action.animate;

import edu.berkeley.guir.prefuse.ItemRegistry;
import edu.berkeley.guir.prefuse.VisualItem;
import edu.berkeley.guir.prefuse.action.AbstractAction;

import java.awt.geom.Point2D;
import java.util.Iterator;

public class LocationAnimator
		extends AbstractAction {
	public void run(ItemRegistry paramItemRegistry, double paramDouble) {
		Iterator localIterator = paramItemRegistry.getItems();
		while (localIterator.hasNext()) {
			VisualItem localVisualItem = (VisualItem) localIterator.next();
			Point2D localPoint2D1 = localVisualItem.getStartLocation();
			Point2D localPoint2D2 = localVisualItem.getEndLocation();
			double d1 = localPoint2D1.getX();
			double d2 = localPoint2D1.getY();
			double d3 = localPoint2D2.getX();
			double d4 = localPoint2D2.getY();
			double d5 = d1 + paramDouble * (d3 - d1);
			double d6 = d2 + paramDouble * (d4 - d2);
			localVisualItem.setLocation(d5, d6);
		}
	}
}


/* Location:              /home/vad/work/JAVA/2018.11.30/prefuse-apps.jar!/edu/berkeley/guir/prefuse/action/animate/LocationAnimator.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */