package edu.berkeley.guir.prefuse.action.animate;

import edu.berkeley.guir.prefuse.ItemRegistry;
import edu.berkeley.guir.prefuse.VisualItem;
import edu.berkeley.guir.prefuse.action.AbstractAction;
import edu.berkeley.guir.prefuse.util.FontLib;

import java.awt.*;
import java.util.Iterator;

public class FontAnimator
		extends AbstractAction {
	public void run(ItemRegistry paramItemRegistry, double paramDouble) {
		Iterator localIterator = paramItemRegistry.getItems();
		while (localIterator.hasNext()) {
			VisualItem localVisualItem = (VisualItem) localIterator.next();
			Font localFont1 = localVisualItem.getStartFont();
			Font localFont2 = localVisualItem.getEndFont();
			localVisualItem.setFont(FontLib.getIntermediateFont(localFont1, localFont2, paramDouble));
		}
	}
}


/* Location:              /home/vad/work/JAVA/2018.11.30/prefuse-apps.jar!/edu/berkeley/guir/prefuse/action/animate/FontAnimator.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */