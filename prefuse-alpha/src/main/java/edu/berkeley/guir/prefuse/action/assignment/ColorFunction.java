package edu.berkeley.guir.prefuse.action.assignment;

import edu.berkeley.guir.prefuse.ItemRegistry;
import edu.berkeley.guir.prefuse.VisualItem;
import edu.berkeley.guir.prefuse.action.AbstractAction;

import java.awt.*;
import java.util.Iterator;

public class ColorFunction
		extends AbstractAction {
	protected Color defaultColor = Color.BLACK;
	protected Color focusColor = Color.RED;
	protected Color highlightColor = Color.BLUE;
	protected Color defaultFillColor = Color.WHITE;
	protected Color focusFillColor = Color.WHITE;
	protected Color highlightFillColor = Color.WHITE;

	public void run(ItemRegistry paramItemRegistry, double paramDouble) {
		Iterator localIterator = paramItemRegistry.getItems();
		while (localIterator.hasNext()) {
			VisualItem localVisualItem = (VisualItem) localIterator.next();
			Paint localPaint1 = getColor(localVisualItem);
			Paint localPaint2 = localVisualItem.getColor();
			if (localPaint2 == null) {
				localVisualItem.setColor(getInitialColor(localVisualItem));
			}
			localVisualItem.updateColor(localPaint1);
			localVisualItem.setColor(localPaint1);
			localPaint1 = getFillColor(localVisualItem);
			localPaint2 = localVisualItem.getFillColor();
			if (localPaint2 == null) {
				localVisualItem.setFillColor(getInitialFillColor(localVisualItem));
			}
			localVisualItem.updateFillColor(localPaint1);
			localVisualItem.setFillColor(localPaint1);
		}
	}

	protected Paint getInitialColor(VisualItem paramVisualItem) {
		return getColor(paramVisualItem);
	}

	protected Paint getInitialFillColor(VisualItem paramVisualItem) {
		return getFillColor(paramVisualItem);
	}

	public Paint getColor(VisualItem paramVisualItem) {
		if (paramVisualItem.isFocus()) {
			return this.focusColor;
		}
		if (paramVisualItem.isHighlighted()) {
			return this.highlightColor;
		}
		return this.defaultColor;
	}

	public Paint getFillColor(VisualItem paramVisualItem) {
		if (paramVisualItem.isFocus()) {
			return this.focusFillColor;
		}
		if (paramVisualItem.isHighlighted()) {
			return this.highlightFillColor;
		}
		return this.defaultFillColor;
	}

	public Color getDefaultColor() {
		return this.defaultColor;
	}

	public void setDefaultColor(Color paramColor) {
		this.defaultColor = paramColor;
	}

	public Color getDefaultFillColor() {
		return this.defaultFillColor;
	}

	public void setDefaultFillColor(Color paramColor) {
		this.defaultFillColor = paramColor;
	}

	public Color getFocusColor() {
		return this.focusColor;
	}

	public void setFocusColor(Color paramColor) {
		this.focusColor = paramColor;
	}

	public Color getFocusFillColor() {
		return this.focusFillColor;
	}

	public void setFocusFillColor(Color paramColor) {
		this.focusFillColor = paramColor;
	}

	public Color getHighlightColor() {
		return this.highlightColor;
	}

	public void setHighlightColor(Color paramColor) {
		this.highlightColor = paramColor;
	}

	public Color getHighlightFillColor() {
		return this.highlightFillColor;
	}

	public void setHighlightFillColor(Color paramColor) {
		this.highlightFillColor = paramColor;
	}
}


/* Location:              /home/vad/work/JAVA/2018.11.30/prefuse-apps.jar!/edu/berkeley/guir/prefuse/action/assignment/ColorFunction.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */