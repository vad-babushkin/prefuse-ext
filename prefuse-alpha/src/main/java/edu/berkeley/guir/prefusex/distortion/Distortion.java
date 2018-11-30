package edu.berkeley.guir.prefusex.distortion;

import edu.berkeley.guir.prefuse.ItemRegistry;
import edu.berkeley.guir.prefuse.VisualItem;
import edu.berkeley.guir.prefuse.action.assignment.Layout;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Iterator;

public abstract class Distortion
		extends Layout {
	private Point2D m_tmp = new Point2D.Double();
	private boolean m_sizeDistorted = true;
	private final boolean useFilteredGraph;

	public Distortion(boolean paramBoolean) {
		this.useFilteredGraph = paramBoolean;
	}

	public void setSizeDistorted(boolean paramBoolean) {
		this.m_sizeDistorted = paramBoolean;
	}

	public boolean isSizeDistorted() {
		return this.m_sizeDistorted;
	}

	public void run(ItemRegistry paramItemRegistry, double paramDouble) {
		Rectangle2D localRectangle2D1 = getLayoutBounds(paramItemRegistry);
		Point2D localPoint2D1 = correct(getLayoutAnchor(), localRectangle2D1);
		Iterator localIterator;
		if (this.useFilteredGraph) {
			localIterator = paramItemRegistry.getFilteredGraph().getNodes();
		} else {
			localIterator = paramItemRegistry.getNodeItems();
		}
		int i = 0;
		while (localIterator.hasNext()) {
			VisualItem localVisualItem = (VisualItem) localIterator.next();
			if (!localVisualItem.isFixed()) {
				localVisualItem.getLocation().setLocation(localVisualItem.getEndLocation());
				localVisualItem.setSize(localVisualItem.getEndSize());
				if (localPoint2D1 != null) {
					Rectangle2D localRectangle2D2 = localVisualItem.getBounds();
					Point2D localPoint2D2 = localVisualItem.getLocation();
					transformPoint(localVisualItem.getEndLocation(), localPoint2D2, localPoint2D1, localRectangle2D1);
					if (this.m_sizeDistorted) {
						double d = transformSize(localRectangle2D2, localPoint2D2, localPoint2D1, localRectangle2D1);
						localVisualItem.setSize(d * localVisualItem.getEndSize());
					}
				}
			}
		}
	}

	protected Point2D correct(Point2D paramPoint2D, Rectangle2D paramRectangle2D) {
		if (paramPoint2D == null) {
			return paramPoint2D;
		}
		double d1 = paramPoint2D.getX();
		double d2 = paramPoint2D.getY();
		double d3 = paramRectangle2D.getMinX();
		double d4 = paramRectangle2D.getMinY();
		double d5 = paramRectangle2D.getMaxX();
		double d6 = paramRectangle2D.getMaxY();
		d1 = d1 > d5 ? d5 : d1 < d3 ? d3 : d1;
		d2 = d2 > d6 ? d6 : d2 < d4 ? d4 : d2;
		this.m_tmp.setLocation(d1, d2);
		return this.m_tmp;
	}

	protected abstract void transformPoint(Point2D paramPoint2D1, Point2D paramPoint2D2, Point2D paramPoint2D3, Rectangle2D paramRectangle2D);

	protected abstract double transformSize(Rectangle2D paramRectangle2D1, Point2D paramPoint2D1, Point2D paramPoint2D2, Rectangle2D paramRectangle2D2);
}


/* Location:              /home/vad/work/JAVA/2018.11.30/prefuse-apps.jar!/edu/berkeley/guir/prefusex/distortion/Distortion.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */