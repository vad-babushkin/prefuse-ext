package edu.berkeley.guir.prefusex.layout;

import edu.berkeley.guir.prefuse.EdgeItem;
import edu.berkeley.guir.prefuse.ItemRegistry;
import edu.berkeley.guir.prefuse.NodeItem;
import edu.berkeley.guir.prefuse.VisualItem;
import edu.berkeley.guir.prefuse.action.assignment.Layout;
import edu.berkeley.guir.prefuse.graph.Graph;

import java.awt.geom.Rectangle2D;
import java.util.Iterator;
import java.util.Random;

public class FruchtermanReingoldLayout
		extends Layout {
	private double forceConstant;
	private double temp;
	private int maxIter = 700;
	private static final double EPSILON = 1.0E-6D;
	private static final double ALPHA = 0.1D;

	public FruchtermanReingoldLayout() {
		this(700);
	}

	public FruchtermanReingoldLayout(int paramInt) {
		this.maxIter = paramInt;
	}

	public int getMaxIterations() {
		return this.maxIter;
	}

	public void setMaxIterations(int paramInt) {
		this.maxIter = paramInt;
	}

	public void run(ItemRegistry paramItemRegistry, double paramDouble) {
		Graph localGraph = paramItemRegistry.getFilteredGraph();
		Rectangle2D localRectangle2D = super.getLayoutBounds(paramItemRegistry);
		init(localGraph, localRectangle2D);
		for (int i = 0; i < this.maxIter; i++) {
			Iterator localIterator1 = localGraph.getNodes();
			Object localObject;
			while (localIterator1.hasNext()) {
				localObject = (NodeItem) localIterator1.next();
				if (!((NodeItem) localObject).isFixed()) {
					calcRepulsion(localGraph, (NodeItem) localObject);
				}
			}
			localIterator1 = localGraph.getEdges();
			while (localIterator1.hasNext()) {
				localObject = (EdgeItem) localIterator1.next();
				calcAttraction((EdgeItem) localObject);
			}
			double d = 0.0D;
			Iterator localIterator2 = localGraph.getNodes();
			while (localIterator2.hasNext()) {
				NodeItem localNodeItem = (NodeItem) localIterator2.next();
				if (!localNodeItem.isFixed()) {
					calcPositions(localNodeItem, localRectangle2D);
				}
			}
			cool(i);
		}
		finish(localGraph);
	}

	private void init(Graph paramGraph, Rectangle2D paramRectangle2D) {
		this.temp = (paramRectangle2D.getWidth() / 10.0D);
		this.forceConstant = (0.75D * Math.sqrt(paramRectangle2D.getHeight() * paramRectangle2D.getWidth() / paramGraph.getNodeCount()));
		Iterator localIterator = paramGraph.getNodes();
		Random localRandom = new Random(42L);
		double d1 = 0.1D * paramRectangle2D.getWidth() / 2.0D;
		double d2 = 0.1D * paramRectangle2D.getHeight() / 2.0D;
		while (localIterator.hasNext()) {
			NodeItem localNodeItem = (NodeItem) localIterator.next();
			FRParams localFRParams = getParams(localNodeItem);
			localFRParams.loc[0] = (paramRectangle2D.getCenterX() + localRandom.nextDouble() * d1);
			localFRParams.loc[1] = (paramRectangle2D.getCenterY() + localRandom.nextDouble() * d2);
		}
	}

	private void finish(Graph paramGraph) {
		Iterator localIterator = paramGraph.getNodes();
		while (localIterator.hasNext()) {
			NodeItem localNodeItem = (NodeItem) localIterator.next();
			FRParams localFRParams = getParams(localNodeItem);
			setLocation(localNodeItem, null, localFRParams.loc[0], localFRParams.loc[1]);
		}
	}

	public void calcPositions(NodeItem paramNodeItem, Rectangle2D paramRectangle2D) {
		FRParams localFRParams = getParams(paramNodeItem);
		double d1 = Math.max(1.0E-6D, Math.sqrt(localFRParams.disp[0] * localFRParams.disp[0] + localFRParams.disp[1] * localFRParams.disp[1]));
		double d2 = localFRParams.disp[0] / d1 * Math.min(d1, this.temp);
		if (Double.isNaN(d2)) {
			System.err.println("Mathematical error... (calcPositions:xDisp)");
		}
		double d3 = localFRParams.disp[1] / d1 * Math.min(d1, this.temp);
		localFRParams.loc[0] += d2;
		localFRParams.loc[1] += d3;
		double d4 = paramRectangle2D.getWidth() / 50.0D;
		double d5 = localFRParams.loc[0];
		if (d5 < paramRectangle2D.getMinX() + d4) {
			d5 = paramRectangle2D.getMinX() + d4 + Math.random() * d4 * 2.0D;
		} else if (d5 > paramRectangle2D.getMaxX() - d4) {
			d5 = paramRectangle2D.getMaxX() - d4 - Math.random() * d4 * 2.0D;
		}
		double d6 = localFRParams.loc[1];
		if (d6 < paramRectangle2D.getMinY() + d4) {
			d6 = paramRectangle2D.getMinY() + d4 + Math.random() * d4 * 2.0D;
		} else if (d6 > paramRectangle2D.getMaxY() - d4) {
			d6 = paramRectangle2D.getMaxY() - d4 - Math.random() * d4 * 2.0D;
		}
		localFRParams.loc[0] = d5;
		localFRParams.loc[1] = d6;
	}

	public void calcAttraction(EdgeItem paramEdgeItem) {
		NodeItem localNodeItem1 = (NodeItem) paramEdgeItem.getFirstNode();
		FRParams localFRParams1 = getParams(localNodeItem1);
		NodeItem localNodeItem2 = (NodeItem) paramEdgeItem.getSecondNode();
		FRParams localFRParams2 = getParams(localNodeItem2);
		double d1 = localFRParams1.loc[0] - localFRParams2.loc[0];
		double d2 = localFRParams1.loc[1] - localFRParams2.loc[1];
		double d3 = Math.max(1.0E-6D, Math.sqrt(d1 * d1 + d2 * d2));
		double d4 = d3 * d3 / this.forceConstant;
		if (Double.isNaN(d4)) {
			System.err.println("Mathematical error...");
		}
		double d5 = d1 / d3 * d4;
		double d6 = d2 / d3 * d4;
		localFRParams1.disp[0] -= d5;
		localFRParams1.disp[1] -= d6;
		localFRParams2.disp[0] += d5;
		localFRParams2.disp[1] += d6;
	}

	public void calcRepulsion(Graph paramGraph, NodeItem paramNodeItem) {
		FRParams localFRParams1 = getParams(paramNodeItem);
		localFRParams1.disp[0] = 0.0D;
		localFRParams1.disp[1] = 0.0D;
		Iterator localIterator = paramGraph.getNodes();
		while (localIterator.hasNext()) {
			NodeItem localNodeItem = (NodeItem) localIterator.next();
			FRParams localFRParams2 = getParams(localNodeItem);
			if ((!localNodeItem.isFixed()) && (paramNodeItem != localNodeItem)) {
				double d1 = localFRParams1.loc[0] - localFRParams2.loc[0];
				double d2 = localFRParams1.loc[1] - localFRParams2.loc[1];
				double d3 = Math.max(1.0E-6D, Math.sqrt(d1 * d1 + d2 * d2));
				double d4 = this.forceConstant * this.forceConstant / d3;
				if (Double.isNaN(d4)) {
					System.err.println("Mathematical error...");
				}
				localFRParams1.disp[0] += d1 / d3 * d4;
				localFRParams1.disp[1] += d2 / d3 * d4;
			}
		}
	}

	private void cool(int paramInt) {
		this.temp *= (1.0D - paramInt / this.maxIter);
	}

	private FRParams getParams(VisualItem paramVisualItem) {
		FRParams localFRParams = (FRParams) paramVisualItem.getVizAttribute("frParams");
		if (localFRParams == null) {
			localFRParams = new FRParams();
			paramVisualItem.setVizAttribute("frParams", localFRParams);
		}
		return localFRParams;
	}

	public class FRParams {
		double[] loc = new double[2];
		double[] disp = new double[2];

		public FRParams() {
		}
	}
}


/* Location:              /home/vad/work/JAVA/2018.11.30/prefuse-apps.jar!/edu/berkeley/guir/prefusex/layout/FruchtermanReingoldLayout.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */