package edu.berkeley.guir.prefusex.layout;

import edu.berkeley.guir.prefuse.EdgeItem;
import edu.berkeley.guir.prefuse.ItemRegistry;
import edu.berkeley.guir.prefuse.NodeItem;
import edu.berkeley.guir.prefuse.action.assignment.Layout;
import edu.berkeley.guir.prefusex.force.*;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Iterator;

public class ForceDirectedLayout
		extends Layout {
	protected ItemRegistry registry;
	private ForceSimulator m_fsim;
	private long m_lasttime = -1L;
	private long m_maxstep = 50L;
	private boolean m_runonce;
	private int m_iterations = 100;
	private boolean m_enforceBounds;

	public ForceDirectedLayout(boolean paramBoolean) {
		this(paramBoolean, false);
	}

	public ForceDirectedLayout(boolean paramBoolean1, boolean paramBoolean2) {
		this.m_enforceBounds = paramBoolean1;
		this.m_runonce = paramBoolean2;
		this.m_fsim = new ForceSimulator();
		this.m_fsim.addForce(new NBodyForce());
		this.m_fsim.addForce(new SpringForce());
		this.m_fsim.addForce(new DragForce());
	}

	public ForceDirectedLayout(ForceSimulator paramForceSimulator, boolean paramBoolean) {
		this(paramForceSimulator, paramBoolean, false);
	}

	public ForceDirectedLayout(ForceSimulator paramForceSimulator, boolean paramBoolean1, boolean paramBoolean2) {
		this.m_enforceBounds = paramBoolean1;
		this.m_runonce = paramBoolean2;
		this.m_fsim = paramForceSimulator;
	}

	public ForceSimulator getForceSimulator() {
		return this.m_fsim;
	}

	public void setForceSimulator(ForceSimulator paramForceSimulator) {
		this.m_fsim = paramForceSimulator;
	}

	public void run(ItemRegistry paramItemRegistry, double paramDouble) {
		this.registry = paramItemRegistry;
		if (this.m_runonce) {
			Point2D localPoint2D = getLayoutAnchor(paramItemRegistry);
			Iterator localIterator = paramItemRegistry.getNodeItems();
			while (localIterator.hasNext()) {
				NodeItem localNodeItem = (NodeItem) localIterator.next();
				localNodeItem.setLocation(localPoint2D);
			}
			this.m_fsim.clear();
			initSimulator(paramItemRegistry, this.m_fsim);
			for (int i = 0; i < this.m_iterations; i++) {
				this.m_fsim.runSimulator(50L);
			}
			updateNodePositions();
		} else {
			if (this.m_lasttime == -1L) {
				this.m_lasttime = (System.currentTimeMillis() - 20L);
			}
			long l1 = System.currentTimeMillis();
			long l2 = Math.min(this.m_maxstep, l1 - this.m_lasttime);
			this.m_lasttime = l1;
			this.m_fsim.clear();
			initSimulator(paramItemRegistry, this.m_fsim);
			this.m_fsim.runSimulator(l2);
			updateNodePositions();
		}
		this.registry = null;
		if (paramDouble == 1.0D) {
			reset(paramItemRegistry);
		}
	}

	private void updateNodePositions() {
		Rectangle2D localRectangle2D = getLayoutBounds(this.registry);
		double d1 = 0.0D;
		double d2 = 0.0D;
		double d3 = 0.0D;
		double d4 = 0.0D;
		if (localRectangle2D != null) {
			d1 = localRectangle2D.getMinX();
			d3 = localRectangle2D.getMinY();
			d2 = localRectangle2D.getMaxX();
			d4 = localRectangle2D.getMaxY();
		}
		Iterator localIterator = this.registry.getNodeItems();
		while (localIterator.hasNext()) {
			NodeItem localNodeItem = (NodeItem) localIterator.next();
			if (localNodeItem.isFixed()) {
				if (Double.isNaN(localNodeItem.getX())) {
					setLocation(localNodeItem, null, 0.0D, 0.0D);
				}
			} else {
				ForceItem localForceItem = (ForceItem) localNodeItem.getVizAttribute("forceItem");
				double d5 = localForceItem.location[0];
				double d6 = localForceItem.location[1];
				if ((this.m_enforceBounds) && (localRectangle2D != null)) {
					if (d5 > d2) {
						d5 = d2;
					}
					if (d5 < d1) {
						d5 = d1;
					}
					if (d6 > d4) {
						d6 = d4;
					}
					if (d6 < d3) {
						d6 = d3;
					}
				}
				setLocation(localNodeItem, null, d5, d6);
			}
		}
	}

	public void reset(ItemRegistry paramItemRegistry) {
		Iterator localIterator = paramItemRegistry.getNodeItems();
		while (localIterator.hasNext()) {
			NodeItem localNodeItem = (NodeItem) localIterator.next();
			ForceItem localForceItem = (ForceItem) localNodeItem.getVizAttribute("forceItem");
			if (localForceItem != null) {
				localForceItem.location[0] = ((float) localNodeItem.getEndLocation().getX());
				localForceItem.location[1] = ((float) localNodeItem.getEndLocation().getY());
				localForceItem.force[0] = (localForceItem.force[1] = 0.0F);
				localForceItem.velocity[0] = (localForceItem.velocity[1] = 0.0F);
			}
		}
		this.m_lasttime = -1L;
	}

	protected void initSimulator(ItemRegistry paramItemRegistry, ForceSimulator paramForceSimulator) {
		Iterator localIterator = paramItemRegistry.getNodeItems();
		Object localObject1;
		Object localObject2;
		while (localIterator.hasNext()) {
			localObject1 = (NodeItem) localIterator.next();
			localObject2 = (ForceItem) ((NodeItem) localObject1).getVizAttribute("forceItem");
			if (localObject2 == null) {
				localObject2 = new ForceItem();
				((ForceItem) localObject2).mass = getMassValue((NodeItem) localObject1);
				((NodeItem) localObject1).setVizAttribute("forceItem", localObject2);
			}
			double d1 = ((NodeItem) localObject1).getEndLocation().getX();
			double d2 = ((NodeItem) localObject1).getEndLocation().getY();
			((ForceItem) localObject2).location[0] = (Double.isNaN(d1) ? 0.0F : (float) d1);
			((ForceItem) localObject2).location[1] = (Double.isNaN(d2) ? 0.0F : (float) d2);
			paramForceSimulator.addItem((ForceItem) localObject2);
		}
		localIterator = paramItemRegistry.getEdgeItems();
		while (localIterator.hasNext()) {
			localObject1 = (EdgeItem) localIterator.next();
			localObject2 = (NodeItem) ((EdgeItem) localObject1).getFirstNode();
			ForceItem localForceItem1 = (ForceItem) ((NodeItem) localObject2).getVizAttribute("forceItem");
			NodeItem localNodeItem = (NodeItem) ((EdgeItem) localObject1).getSecondNode();
			ForceItem localForceItem2 = (ForceItem) localNodeItem.getVizAttribute("forceItem");
			float f1 = getSpringCoefficient((EdgeItem) localObject1);
			float f2 = getSpringLength((EdgeItem) localObject1);
			paramForceSimulator.addSpring(localForceItem1, localForceItem2, f1 >= 0.0F ? f1 : -1.0F, f2 >= 0.0F ? f2 : -1.0F);
		}
	}

	protected float getMassValue(NodeItem paramNodeItem) {
		return 1.0F;
	}

	protected float getSpringLength(EdgeItem paramEdgeItem) {
		return -1.0F;
	}

	protected float getSpringCoefficient(EdgeItem paramEdgeItem) {
		return -1.0F;
	}
}


/* Location:              /home/vad/work/JAVA/2018.11.30/prefuse-apps.jar!/edu/berkeley/guir/prefusex/layout/ForceDirectedLayout.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */