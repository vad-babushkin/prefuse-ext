package edu.berkeley.guir.prefuse.action.assignment;

import edu.berkeley.guir.prefuse.ItemRegistry;
import edu.berkeley.guir.prefuse.VisualItem;
import edu.berkeley.guir.prefuse.action.AbstractAction;

import java.awt.*;
import java.util.Iterator;

public class FontFunction
		extends AbstractAction {
	public void run(ItemRegistry paramItemRegistry, double paramDouble) {
		Iterator localIterator = paramItemRegistry.getItems();
		while (localIterator.hasNext()) {
			VisualItem localVisualItem = (VisualItem) localIterator.next();
			Font localFont = getFont(localVisualItem);
			localVisualItem.setFont(localFont);
		}
	}

	public Font getFont(VisualItem paramVisualItem) {
		return null;
	}
}


/* Location:              /home/vad/work/JAVA/2018.11.30/prefuse-apps.jar!/edu/berkeley/guir/prefuse/action/assignment/FontFunction.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */