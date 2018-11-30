package edu.berkeley.guir.prefusex.layout;

import edu.berkeley.guir.prefuse.ItemRegistry;
import edu.berkeley.guir.prefuse.NodeItem;
import edu.berkeley.guir.prefuse.action.assignment.Layout;
import edu.berkeley.guir.prefuse.graph.Graph;

import java.awt.geom.Rectangle2D;
import java.util.Iterator;

public class CircleLayout
		extends Layout {
	private double m_radius;

	public CircleLayout() {
	}

	public CircleLayout(double paramDouble) {
		this.m_radius = paramDouble;
	}

	public double getRadius() {
		return this.m_radius;
	}

	public void setRadius(double paramDouble) {
		this.m_radius = paramDouble;
	}

	public void run(ItemRegistry paramItemRegistry, double paramDouble) {
		Graph localGraph = paramItemRegistry.getFilteredGraph();
		int i = localGraph.getNodeCount();
		Rectangle2D localRectangle2D = super.getLayoutBounds(paramItemRegistry);
		double d1 = localRectangle2D.getHeight();
		double d2 = localRectangle2D.getWidth();
		double d3 = localRectangle2D.getCenterX();
		double d4 = localRectangle2D.getCenterY();
		double d5 = this.m_radius;
		if (d5 <= 0.0D) {
			d5 = 0.45D * (d1 < d2 ? d1 : d2);
		}
		Iterator localIterator = localGraph.getNodes();
		for (int j = 0; localIterator.hasNext(); j++) {
			NodeItem localNodeItem = (NodeItem) localIterator.next();
			double d6 = 6.283185307179586D * j / i;
			double d7 = Math.cos(d6) * d5 + d3;
			double d8 = Math.sin(d6) * d5 + d4;
			setLocation(localNodeItem, null, d7, d8);
		}
	}
}


/* Location:              /home/vad/work/JAVA/2018.11.30/prefuse-apps.jar!/edu/berkeley/guir/prefusex/layout/CircleLayout.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */