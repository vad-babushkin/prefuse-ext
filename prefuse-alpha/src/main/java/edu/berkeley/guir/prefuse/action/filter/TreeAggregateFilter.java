package edu.berkeley.guir.prefuse.action.filter;

import edu.berkeley.guir.prefuse.AggregateItem;
import edu.berkeley.guir.prefuse.Display;
import edu.berkeley.guir.prefuse.ItemRegistry;
import edu.berkeley.guir.prefuse.NodeItem;
import edu.berkeley.guir.prefuse.graph.Node;

import java.awt.geom.Point2D;
import java.util.Iterator;

public class TreeAggregateFilter
		extends Filter {
	private Point2D m_anchor = null;

	public TreeAggregateFilter() {
		super("aggregate", true);
	}

	public void run(ItemRegistry paramItemRegistry, double paramDouble) {
		this.m_anchor = getLayoutAnchor(paramItemRegistry);
		Iterator localIterator = paramItemRegistry.getNodeItems();
		while (localIterator.hasNext()) {
			NodeItem localNodeItem = (NodeItem) localIterator.next();
			Node localNode = (Node) localNodeItem.getEntity();
			int i = 0;
			if ((localNodeItem.getChildCount() == 0) && ((i = localNode.getEdgeCount() - localNodeItem.getEdgeCount()) > 0)) {
				AggregateItem localAggregateItem = paramItemRegistry.getAggregateItem(localNode, true);
				Point2D localPoint2D1 = localNodeItem.getEndLocation();
				Point2D localPoint2D2 = localNodeItem.getStartLocation();
				localAggregateItem.setLocation(localPoint2D2);
				localAggregateItem.updateLocation(localPoint2D1);
				localAggregateItem.setLocation(localPoint2D1);
				setOrientation(localAggregateItem);
				localAggregateItem.setAggregateSize(i);
			}
		}
		super.run(paramItemRegistry, paramDouble);
	}

	public Point2D getLayoutAnchor(ItemRegistry paramItemRegistry) {
		Point2D.Double localDouble = new Point2D.Double(0.0D, 0.0D);
		if (paramItemRegistry != null) {
			Display localDisplay = paramItemRegistry.getDisplay(0);
			localDouble.setLocation(localDisplay.getWidth() / 2.0D, localDisplay.getHeight() / 2.0D);
			localDisplay.getInverseTransform().transform(localDouble, localDouble);
		}
		return localDouble;
	}

	protected void setOrientation(AggregateItem paramAggregateItem) {
		Point2D localPoint2D1 = paramAggregateItem.getEndLocation();
		Point2D localPoint2D2 = paramAggregateItem.getStartLocation();
		double d1 = this.m_anchor.getX();
		double d2 = this.m_anchor.getY();
		double d3 = localPoint2D2.getX() - d1;
		double d4 = localPoint2D2.getY() - d2;
		double d5 = localPoint2D1.getX() - d1;
		double d6 = localPoint2D1.getY() - d2;
		double d7 = Math.atan2(d6, d5);
		double d8 = (d3 == 0.0D) && (d4 == 0.0D) ? d7 : Math.atan2(d4, d3);
		paramAggregateItem.setStartOrientation(d8);
		paramAggregateItem.setOrientation(d7);
		paramAggregateItem.setEndOrientation(d7);
	}
}


/* Location:              /home/vad/work/JAVA/2018.11.30/prefuse-apps.jar!/edu/berkeley/guir/prefuse/action/filter/TreeAggregateFilter.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */