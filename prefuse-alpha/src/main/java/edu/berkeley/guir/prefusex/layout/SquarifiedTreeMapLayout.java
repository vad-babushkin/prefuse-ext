package edu.berkeley.guir.prefusex.layout;

import edu.berkeley.guir.prefuse.ItemRegistry;
import edu.berkeley.guir.prefuse.NodeItem;
import edu.berkeley.guir.prefuse.VisualItem;
import edu.berkeley.guir.prefuse.action.assignment.TreeLayout;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.*;

public class SquarifiedTreeMapLayout
		extends TreeLayout {
	private static Comparator s_cmp = new Comparator() {
		public int compare(Object paramAnonymousObject1, Object paramAnonymousObject2) {
			double d1 = ((VisualItem) paramAnonymousObject1).getSize();
			double d2 = ((VisualItem) paramAnonymousObject2).getSize();
			return d1 < d2 ? -1 : d1 > d2 ? 1 : 0;
		}
	};
	private ArrayList m_kids = new ArrayList();
	private ArrayList m_row = new ArrayList();
	private Rectangle2D m_r = new Rectangle2D.Double();
	private double m_frame;

	public SquarifiedTreeMapLayout() {
		this(0.0D);
	}

	public SquarifiedTreeMapLayout(double paramDouble) {
		this.m_frame = paramDouble;
	}

	public void run(ItemRegistry paramItemRegistry, double paramDouble) {
		NodeItem localNodeItem = getLayoutRoot(paramItemRegistry);
		this.m_r.setRect(getLayoutBounds(paramItemRegistry));
		localNodeItem.setLocation(0.0D, 0.0D);
		Point2D.Double localDouble = new Point2D.Double(this.m_r.getWidth(), this.m_r.getHeight());
		localNodeItem.setVizAttribute("dimension", localDouble);
		updateArea(localNodeItem, this.m_r);
		layout(localNodeItem, this.m_r);
	}

	private void layout(NodeItem paramNodeItem, Rectangle2D paramRectangle2D) {
		Iterator localIterator = paramNodeItem.getChildren();
		while (localIterator.hasNext()) {
			this.m_kids.add(localIterator.next());
		}
		Collections.sort(this.m_kids, s_cmp);
		double d = Math.min(paramRectangle2D.getWidth(), paramRectangle2D.getHeight());
		squarify(this.m_kids, this.m_row, d, paramRectangle2D);
		this.m_kids.clear();
		localIterator = paramNodeItem.getChildren();
		while (localIterator.hasNext()) {
			NodeItem localNodeItem = (NodeItem) localIterator.next();
			if (localNodeItem.getChildCount() > 0) {
				updateArea(localNodeItem, paramRectangle2D);
				layout(localNodeItem, paramRectangle2D);
			}
		}
	}

	private void updateArea(NodeItem paramNodeItem, Rectangle2D paramRectangle2D) {
		Point2D localPoint2D = (Point2D) paramNodeItem.getVizAttribute("dimension");
		if (this.m_frame == 0.0D) {
			paramRectangle2D.setRect(paramNodeItem.getX(), paramNodeItem.getY(), localPoint2D.getX(), localPoint2D.getY());
			return;
		}
		double d1 = 2.0D * this.m_frame * (localPoint2D.getX() + localPoint2D.getY() - 2.0D * this.m_frame);
		double d2 = paramNodeItem.getSize() - d1;
		double d3 = 0.0D;
		Iterator localIterator = paramNodeItem.getChildren();
		while (localIterator.hasNext()) {
			d3 += ((NodeItem) localIterator.next()).getSize();
		}
		double d4 = d2 / d3;
		localIterator = paramNodeItem.getChildren();
		while (localIterator.hasNext()) {
			NodeItem localNodeItem = (NodeItem) localIterator.next();
			localNodeItem.setSize(localNodeItem.getSize() * d4);
		}
		paramRectangle2D.setRect(paramNodeItem.getX() + this.m_frame, paramNodeItem.getY() + this.m_frame, localPoint2D.getX() - 2.0D * this.m_frame, localPoint2D.getY() - 2.0D * this.m_frame);
	}

	private void squarify(List paramList1, List paramList2, double paramDouble, Rectangle2D paramRectangle2D) {
		double d1 = Double.MAX_VALUE;
		int i;
		while ((i = paramList1.size()) > 0) {
			paramList2.add(paramList1.get(i - 1));
			double d2 = worst(paramList2, paramDouble);
			if (d2 <= d1) {
				paramList1.remove(i - 1);
				d1 = d2;
			} else {
				paramList2.remove(paramList2.size() - 1);
				paramRectangle2D = layoutRow(paramList2, paramDouble, paramRectangle2D);
				paramDouble = Math.min(paramRectangle2D.getWidth(), paramRectangle2D.getHeight());
				paramList2.clear();
				d1 = Double.MAX_VALUE;
			}
		}
		if (paramList2.size() > 0) {
			paramRectangle2D = layoutRow(paramList2, paramDouble, paramRectangle2D);
			paramList2.clear();
		}
	}

	private double worst(List paramList, double paramDouble) {
		double d1 = Double.MIN_VALUE;
		double d2 = Double.MAX_VALUE;
		double d3 = 0.0D;
		Iterator localIterator = paramList.iterator();
		while (localIterator.hasNext()) {
			double d4 = ((VisualItem) localIterator.next()).getSize();
			d2 = Math.min(d2, d4);
			d1 = Math.max(d1, d4);
			d3 += d4;
		}
		d3 *= d3;
		paramDouble *= paramDouble;
		return Math.max(paramDouble * d1 / d3, d3 / (paramDouble * d2));
	}

	private Rectangle2D layoutRow(List paramList, double paramDouble, Rectangle2D paramRectangle2D) {
		double d1 = 0.0D;
		Iterator localIterator = paramList.iterator();
		while (localIterator.hasNext()) {
			d1 += ((VisualItem) localIterator.next()).getSize();
		}
		double d2 = paramRectangle2D.getX();
		double d3 = paramRectangle2D.getY();
		double d4 = 0.0D;
		double d5 = d1 / paramDouble;
		int i = paramDouble == paramRectangle2D.getWidth() ? 1 : 0;
		localIterator = paramList.iterator();
		while (localIterator.hasNext()) {
			NodeItem localNodeItem = (NodeItem) localIterator.next();
			if (i != 0) {
				setLocation(localNodeItem, (NodeItem) localNodeItem.getParent(), d2 + d4, d3);
			} else {
				setLocation(localNodeItem, (NodeItem) localNodeItem.getParent(), d2, d3 + d4);
			}
			double d6 = localNodeItem.getSize() / d5;
			if (i != 0) {
				setNodeDimensions(localNodeItem, d6, d5);
				d4 += d6;
			} else {
				setNodeDimensions(localNodeItem, d5, d6);
				d4 += d6;
			}
		}
		if (i != 0) {
			paramRectangle2D.setRect(d2, d3 + d5, paramRectangle2D.getWidth(), paramRectangle2D.getHeight() - d5);
		} else {
			paramRectangle2D.setRect(d2 + d5, d3, paramRectangle2D.getWidth() - d5, paramRectangle2D.getHeight());
		}
		return paramRectangle2D;
	}

	private void setNodeDimensions(NodeItem paramNodeItem, double paramDouble1, double paramDouble2) {
		Object localObject = (Point2D) paramNodeItem.getVizAttribute("dimension");
		if (localObject == null) {
			localObject = new Point2D.Double();
			paramNodeItem.setVizAttribute("dimension", localObject);
		}
		((Point2D) localObject).setLocation(paramDouble1, paramDouble2);
	}
}


/* Location:              /home/vad/work/JAVA/2018.11.30/prefuse-apps.jar!/edu/berkeley/guir/prefusex/layout/SquarifiedTreeMapLayout.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */