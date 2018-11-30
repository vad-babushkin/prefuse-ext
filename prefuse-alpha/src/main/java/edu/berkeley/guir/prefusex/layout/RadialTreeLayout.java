package edu.berkeley.guir.prefusex.layout;

import edu.berkeley.guir.prefuse.ItemRegistry;
import edu.berkeley.guir.prefuse.NodeItem;
import edu.berkeley.guir.prefuse.VisualItem;
import edu.berkeley.guir.prefuse.action.assignment.TreeLayout;
import edu.berkeley.guir.prefuse.event.FocusEvent;
import edu.berkeley.guir.prefuse.event.FocusListener;
import edu.berkeley.guir.prefuse.graph.Entity;
import edu.berkeley.guir.prefuse.graph.Node;
import edu.berkeley.guir.prefuse.graph.TreeNode;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Iterator;

public class RadialTreeLayout
		extends TreeLayout
		implements FocusListener {
	public static final int DEFAULT_RADIUS = 50;
	protected static final double TWO_PI = 6.283185307179586D;
	protected int m_maxDepth = 0;
	protected double m_radiusInc;
	protected double m_startTheta;
	protected double m_endTheta;
	protected boolean m_setTheta = false;
	protected boolean m_autoScale = true;
	protected Point2D m_origin;
	protected TreeNode m_prevParent;
	protected NodeItem m_pfocus;
	protected NodeItem m_focus;
	protected ItemRegistry m_registry;

	public RadialTreeLayout() {
		this(50);
	}

	public RadialTreeLayout(int paramInt) {
		this.m_radiusInc = paramInt;
		this.m_prevParent = null;
		this.m_startTheta = 0.0D;
		this.m_endTheta = 6.283185307179586D;
	}

	public double getRadiusIncrement() {
		return this.m_radiusInc;
	}

	public void setRadiusIncrement(double paramDouble) {
		this.m_radiusInc = paramDouble;
	}

	public boolean getAutoScale() {
		return this.m_autoScale;
	}

	public void setAutoScale(boolean paramBoolean) {
		this.m_autoScale = paramBoolean;
	}

	public void setStartTheta(double paramDouble) {
		this.m_startTheta = paramDouble;
		this.m_setTheta = true;
	}

	public void setEndTheta(double paramDouble) {
		this.m_endTheta = paramDouble;
		this.m_setTheta = true;
	}

	public void run(ItemRegistry paramItemRegistry, double paramDouble) {
		if (this.m_registry != paramItemRegistry) {
			if (this.m_registry != null) {
				this.m_registry.getDefaultFocusSet().removeFocusListener(this);
			}
			this.m_registry = paramItemRegistry;
			this.m_registry.getDefaultFocusSet().addFocusListener(this);
		}
		this.m_origin = getLayoutAnchor(paramItemRegistry);
		NodeItem localNodeItem1 = getLayoutRoot(paramItemRegistry);
		RadialParams localRadialParams = getParams(localNodeItem1);
		this.m_maxDepth = 0;
		countVisibleDescendants(localNodeItem1, 0);
		if (this.m_autoScale) {
			setScale(getLayoutBounds(paramItemRegistry));
		}
		if ((!this.m_setTheta) && (this.m_pfocus != null)) {
			NodeItem localNodeItem2 = getPrevParent(this.m_focus, this.m_pfocus);
			this.m_startTheta = calcStartingTheta(localNodeItem1, localNodeItem2);
			this.m_endTheta = (this.m_startTheta + 6.283185307179586D);
		}
		setLocation(localNodeItem1, null, this.m_origin.getX(), this.m_origin.getY());
		localRadialParams.angle = (this.m_endTheta - this.m_startTheta);
		layout(localNodeItem1, this.m_radiusInc, this.m_startTheta, this.m_endTheta);
		this.m_prevParent = null;
	}

	protected void setScale(Rectangle2D paramRectangle2D) {
		double d = Math.min(paramRectangle2D.getWidth(), paramRectangle2D.getHeight()) / 2.0D;
		if (this.m_maxDepth > 0) {
			this.m_radiusInc = ((d - 40.0D) / this.m_maxDepth);
		}
	}

	private NodeItem getPrevParent(NodeItem paramNodeItem1, NodeItem paramNodeItem2) {
		while ((paramNodeItem2 != null) && (paramNodeItem2.getParent() != paramNodeItem1)) {
			paramNodeItem2 = (NodeItem) paramNodeItem2.getParent();
		}
		return paramNodeItem2;
	}

	private double calcStartingTheta(NodeItem paramNodeItem1, NodeItem paramNodeItem2) {
		if (paramNodeItem2 == null) {
			return 0.0D;
		}
		Point2D localPoint2D1 = paramNodeItem2.getLocation();
		Point2D localPoint2D2 = paramNodeItem1.getLocation();
		double d1 = Math.atan2(localPoint2D1.getY() - localPoint2D2.getY(), localPoint2D1.getX() - localPoint2D2.getX());
		int i = paramNodeItem1.getChildIndex(paramNodeItem2);
		int j = getParams(paramNodeItem1).numDescendants;
		int k = getParams(paramNodeItem2).numDescendants;
		int m = 0;
		for (int n = 0; n < i; n++) {
			m += getParams((NodeItem) paramNodeItem1.getChild(n)).numDescendants;
		}
		double d2 = (m + k / 2.0D) / j;
		return d1 - d2 * 6.283185307179586D;
	}

	private int countVisibleDescendants(NodeItem paramNodeItem, int paramInt) {
		if (paramInt > this.m_maxDepth) {
			this.m_maxDepth = paramInt;
		}
		int i = 0;
		if (paramNodeItem.getChildCount() > 0) {
			Iterator localIterator = paramNodeItem.getChildren();
			while (localIterator.hasNext()) {
				NodeItem localNodeItem = (NodeItem) localIterator.next();
				i += countVisibleDescendants(localNodeItem, paramInt + 1);
			}
		}
		i = 1;
		getParams(paramNodeItem).numDescendants = i;
		return i;
	}

	protected void layout(NodeItem paramNodeItem, double paramDouble1, double paramDouble2, double paramDouble3) {
		int i = getParams(paramNodeItem).numDescendants;
		if (i == 0) {
			return;
		}
		double d1 = paramDouble3 - paramDouble2;
		double d2 = d1 / 2.0D;
		double d4 = 0.0D;
		Iterator localIterator = paramNodeItem.getChildren();
		while (localIterator.hasNext()) {
			NodeItem localNodeItem = (NodeItem) localIterator.next();
			RadialParams localRadialParams = getParams(localNodeItem);
			double d3 = localRadialParams.numDescendants / i;
			setPolarLocation(localNodeItem, paramDouble1, paramDouble2 + d4 * d1 + d3 * d2);
			localRadialParams.angle = (d3 * d1);
			layout(localNodeItem, paramDouble1 + this.m_radiusInc, paramDouble2 + d4 * d1, paramDouble2 + (d4 + d3) * d1);
			d4 += d3;
		}
	}

	protected void setPolarLocation(NodeItem paramNodeItem, double paramDouble1, double paramDouble2) {
		double d1 = this.m_origin.getX() + paramDouble1 * Math.cos(paramDouble2);
		double d2 = this.m_origin.getY() + paramDouble1 * Math.sin(paramDouble2);
		super.setLocation(paramNodeItem, (NodeItem) paramNodeItem.getParent(), d1, d2);
	}

	public void focusChanged(FocusEvent paramFocusEvent) {
		if (paramFocusEvent.getEventType() != 2) {
			return;
		}
		Entity localEntity1 = paramFocusEvent.getFirstAdded();
		Entity localEntity2 = paramFocusEvent.getFirstRemoved();
		if (((localEntity1 instanceof Node)) && ((localEntity2 instanceof Node))) {
			this.m_focus = this.m_registry.getNodeItem((Node) localEntity1);
			this.m_pfocus = this.m_registry.getNodeItem((Node) localEntity2);
		} else {
			this.m_pfocus = this.m_focus;
			this.m_focus = null;
		}
	}

	private RadialParams getParams(VisualItem paramVisualItem) {
		RadialParams localRadialParams = (RadialParams) paramVisualItem.getVizAttribute("radialParams");
		if (localRadialParams == null) {
			localRadialParams = new RadialParams();
			paramVisualItem.setVizAttribute("radialParams", localRadialParams);
		}
		return localRadialParams;
	}

	public class RadialParams {
		int numDescendants;
		double angle;

		public RadialParams() {
		}
	}
}


/* Location:              /home/vad/work/JAVA/2018.11.30/prefuse-apps.jar!/edu/berkeley/guir/prefusex/layout/RadialTreeLayout.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */