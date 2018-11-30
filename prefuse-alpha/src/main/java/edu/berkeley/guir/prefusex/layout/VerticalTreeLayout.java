package edu.berkeley.guir.prefusex.layout;

import edu.berkeley.guir.prefuse.ItemRegistry;
import edu.berkeley.guir.prefuse.NodeItem;
import edu.berkeley.guir.prefuse.action.assignment.TreeLayout;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.Iterator;

public class VerticalTreeLayout
		extends TreeLayout {
	protected HashMap m_counts;
	protected int m_heightInc = 25;
	protected ItemRegistry m_registry;

	public VerticalTreeLayout() {
		try {
			this.m_counts = new HashMap();
		} catch (Exception localException) {
			localException.printStackTrace();
		}
	}

	public Point2D getLayoutAnchor(ItemRegistry paramItemRegistry) {
		Point2D localPoint2D = super.getLayoutAnchor();
		if (localPoint2D != null) {
			return localPoint2D;
		}
		Rectangle2D localRectangle2D = getLayoutBounds(paramItemRegistry);
		double d1 = 0.0D;
		double d2 = 0.0D;
		if (localRectangle2D != null) {
			d1 = localRectangle2D.getX() + localRectangle2D.getWidth() / 2.0D;
			d2 = localRectangle2D.getY() + 20.0D;
		}
		return new Point2D.Double(d1, d2);
	}

	public void run(ItemRegistry paramItemRegistry, double paramDouble) {
		this.m_registry = paramItemRegistry;
		Rectangle2D localRectangle2D = getLayoutBounds(paramItemRegistry);
		Point2D localPoint2D = getLayoutAnchor(paramItemRegistry);
		NodeItem localNodeItem = getLayoutRoot(paramItemRegistry);
		if ((localNodeItem != null) && (localNodeItem.isVisible())) {
			countVisibleDescendants(localNodeItem);
			setLocation(localNodeItem, null, localPoint2D.getX(), localPoint2D.getY());
			layout(localNodeItem, (int) localPoint2D.getY() + this.m_heightInc, localRectangle2D.getX(), localRectangle2D.getX() + localRectangle2D.getWidth());
			this.m_counts.clear();
		} else {
			System.err.println("VerticalTreeLayout: Tree root not visible!");
		}
	}

	private int countVisibleDescendants(NodeItem paramNodeItem) {
		int i = 0;
		Iterator localIterator = paramNodeItem.getChildren();
		while (localIterator.hasNext()) {
			NodeItem localNodeItem = (NodeItem) localIterator.next();
			i += countVisibleDescendants(localNodeItem);
		}
		if (i == 0) {
			i = 1;
		}
		setVisibleDescendants(paramNodeItem, i);
		return i;
	}

	private void setVisibleDescendants(NodeItem paramNodeItem, int paramInt) {
		this.m_counts.put(paramNodeItem, new Integer(paramInt));
	}

	private int getVisibleDescendants(NodeItem paramNodeItem) {
		Integer localInteger = (Integer) this.m_counts.get(paramNodeItem);
		return localInteger == null ? 0 : localInteger.intValue();
	}

	protected void layout(NodeItem paramNodeItem, int paramInt, double paramDouble1, double paramDouble2) {
		int i = getVisibleDescendants(paramNodeItem);
		int j = 0;
		if (i == 0) {
			return;
		}
		double d1 = paramDouble2 - paramDouble1;
		double d2 = d1 / 2.0D;
		double d3 = 0.0D;
		Iterator localIterator = paramNodeItem.getChildren();
		while (localIterator.hasNext()) {
			NodeItem localNodeItem = (NodeItem) localIterator.next();
			double d4 = getVisibleDescendants(localNodeItem) / i;
			setLocation(localNodeItem, paramNodeItem, paramDouble1 + d3 * d1 + d4 * d2, paramInt);
			layout(localNodeItem, paramInt + this.m_heightInc, paramDouble1 + d3 * d1, paramDouble1 + (d3 + d4) * d1);
			d3 += d4;
		}
	}
}


/* Location:              /home/vad/work/JAVA/2018.11.30/prefuse-apps.jar!/edu/berkeley/guir/prefusex/layout/VerticalTreeLayout.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */