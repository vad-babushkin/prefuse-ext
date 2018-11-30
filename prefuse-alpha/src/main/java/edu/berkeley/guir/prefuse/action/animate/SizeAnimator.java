package edu.berkeley.guir.prefuse.action.animate;

import edu.berkeley.guir.prefuse.ItemRegistry;
import edu.berkeley.guir.prefuse.VisualItem;
import edu.berkeley.guir.prefuse.action.AbstractAction;

import java.util.Iterator;

public class SizeAnimator
		extends AbstractAction {
	public static final String ATTR_ANIM_FRAC = "animationFrac";

	public void run(ItemRegistry paramItemRegistry, double paramDouble) {
		Iterator localIterator = paramItemRegistry.getItems();
		while (localIterator.hasNext()) {
			VisualItem localVisualItem = (VisualItem) localIterator.next();
			double d1 = localVisualItem.getStartSize();
			double d2 = localVisualItem.getEndSize();
			double d3 = d1 + paramDouble * (d2 - d1);
			localVisualItem.setSize(d3);
		}
	}
}


/* Location:              /home/vad/work/JAVA/2018.11.30/prefuse-apps.jar!/edu/berkeley/guir/prefuse/action/animate/SizeAnimator.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */