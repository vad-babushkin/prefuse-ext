package edu.berkeley.guir.prefusex.controls;

import edu.berkeley.guir.prefuse.EdgeItem;
import edu.berkeley.guir.prefuse.ItemRegistry;
import edu.berkeley.guir.prefuse.NodeItem;
import edu.berkeley.guir.prefuse.VisualItem;
import edu.berkeley.guir.prefuse.activity.Activity;
import edu.berkeley.guir.prefuse.event.ControlAdapter;

import java.awt.event.MouseEvent;
import java.util.Iterator;

public class NeighborHighlightControl
		extends ControlAdapter {
	private Activity update = null;
	private boolean highlightWithInvisibleEdge = false;

	public NeighborHighlightControl() {
		this(null);
	}

	public NeighborHighlightControl(Activity paramActivity) {
		this.update = paramActivity;
	}

	public void itemEntered(VisualItem paramVisualItem, MouseEvent paramMouseEvent) {
		if ((paramVisualItem instanceof NodeItem)) {
			setNeighborHighlight((NodeItem) paramVisualItem, true);
		}
	}

	public void itemExited(VisualItem paramVisualItem, MouseEvent paramMouseEvent) {
		if ((paramVisualItem instanceof NodeItem)) {
			setNeighborHighlight((NodeItem) paramVisualItem, false);
		}
	}

	public void setNeighborHighlight(NodeItem paramNodeItem, boolean paramBoolean) {
		ItemRegistry localItemRegistry = paramNodeItem.getItemRegistry();
		synchronized (localItemRegistry) {
			Iterator localIterator = paramNodeItem.getEdges();
			while (localIterator.hasNext()) {
				EdgeItem localEdgeItem = (EdgeItem) localIterator.next();
				NodeItem localNodeItem = (NodeItem) localEdgeItem.getAdjacentNode(paramNodeItem);
				if ((localEdgeItem.isVisible()) || (this.highlightWithInvisibleEdge)) {
					localEdgeItem.setHighlighted(paramBoolean);
					localItemRegistry.touch(localEdgeItem.getItemClass());
					localNodeItem.setHighlighted(paramBoolean);
					localItemRegistry.touch(localNodeItem.getItemClass());
				}
			}
		}
		if (this.update != null) {
			this.update.runNow();
		}
	}

	public boolean isHighlightWithInvisibleEdge() {
		return this.highlightWithInvisibleEdge;
	}

	public void setHighlightWithInvisibleEdge(boolean paramBoolean) {
		this.highlightWithInvisibleEdge = paramBoolean;
	}
}


/* Location:              /home/vad/work/JAVA/2018.11.30/prefuse-apps.jar!/edu/berkeley/guir/prefusex/controls/NeighborHighlightControl.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */