package edu.berkeley.guir.prefusex.layout;

import edu.berkeley.guir.prefuse.ItemRegistry;
import edu.berkeley.guir.prefuse.NodeItem;
import edu.berkeley.guir.prefuse.action.assignment.TreeLayout;

import java.awt.geom.Point2D;
import java.util.Iterator;

public class BalloonTreeLayout
		extends TreeLayout {
	private ItemRegistry m_registry;
	private int m_minRadius = 2;

	public BalloonTreeLayout() {
		this(2);
	}

	public BalloonTreeLayout(int paramInt) {
	}

	public void run(ItemRegistry paramItemRegistry, double paramDouble) {
		this.m_registry = paramItemRegistry;
		Point2D localPoint2D = getLayoutAnchor(paramItemRegistry);
		NodeItem localNodeItem = getLayoutRoot(paramItemRegistry);
		layout(localNodeItem, localPoint2D.getX(), localPoint2D.getY());
	}

	public int getMinRadius() {
		return this.m_minRadius;
	}

	public void setMinRadius(int paramInt) {
		this.m_minRadius = paramInt;
	}

	public void layout(NodeItem paramNodeItem, double paramDouble1, double paramDouble2) {
		firstWalk(paramNodeItem);
		secondWalk(paramNodeItem, null, paramDouble1, paramDouble2, 1.0D, 0.0D);
	}

	private void firstWalk(NodeItem paramNodeItem) {
		ParamBlock localParamBlock1 = getParams(paramNodeItem);
		localParamBlock1.d = 0;
		double d = 0.0D;
		Iterator localIterator = paramNodeItem.getChildren();
		while (localIterator.hasNext()) {
			NodeItem localNodeItem = (NodeItem) localIterator.next();
			firstWalk(localNodeItem);
			ParamBlock localParamBlock2 = getParams(localNodeItem);
			localParamBlock1.d = Math.max(localParamBlock1.d, localParamBlock2.r);
			localParamBlock2.a = Math.atan(localParamBlock2.r / (localParamBlock1.d + localParamBlock2.r));
			d += localParamBlock2.a;
		}
		adjustChildren(localParamBlock1, d);
		setRadius(localParamBlock1);
	}

	private void adjustChildren(ParamBlock paramParamBlock, double paramDouble) {
		if (paramDouble > 3.141592653589793D) {
			paramParamBlock.c = (3.141592653589793D / paramDouble);
			paramParamBlock.f = 0.0D;
		} else {
			paramParamBlock.c = 1.0D;
			paramParamBlock.f = (3.141592653589793D - paramDouble);
		}
	}

	private void setRadius(ParamBlock paramParamBlock) {
		paramParamBlock.r = (Math.max(paramParamBlock.d, this.m_minRadius) + 2 * paramParamBlock.d);
	}

	private void setRadius(NodeItem paramNodeItem, ParamBlock paramParamBlock) {
		int i = paramNodeItem.getChildCount();
		double d1 = 3.141592653589793D;
		double d2 = i == 0 ? 0.0D : paramParamBlock.f / i;
		double d3 = 0.0D;
		double d4 = 0.0D;
		double d5 = 0.0D;
		Iterator localIterator = paramNodeItem.getChildren();
		NodeItem localNodeItem;
		ParamBlock localParamBlock;
		while (localIterator.hasNext()) {
			localNodeItem = (NodeItem) localIterator.next();
			localParamBlock = getParams(localNodeItem);
			d1 += d3 + localParamBlock.a + d2;
			d4 += localParamBlock.r * Math.cos(d1);
			d5 += localParamBlock.r * Math.sin(d1);
			d3 = localParamBlock.a;
		}
		if (i != 0) {
			d4 /= i;
			d5 /= i;
		}
		paramParamBlock.rx = (-d4);
		paramParamBlock.ry = (-d5);
		d1 = 3.141592653589793D;
		d3 = 0.0D;
		paramParamBlock.r = 0;
		localIterator = paramNodeItem.getChildren();
		while (localIterator.hasNext()) {
			localNodeItem = (NodeItem) localIterator.next();
			localParamBlock = getParams(localNodeItem);
			d1 += d3 + localParamBlock.a + d2;
			double d6 = localParamBlock.r * Math.cos(d1) - d4;
			double d7 = localParamBlock.r * Math.sin(d1) - d5;
			double d8 = Math.sqrt(d6 * d6 + d7 * d7) + localParamBlock.r;
			paramParamBlock.r = Math.max(paramParamBlock.r, (int) Math.round(d8));
			d3 = localParamBlock.a;
		}
		if (paramParamBlock.r == 0) {
			paramParamBlock.r = (this.m_minRadius + 2 * paramParamBlock.d);
		}
	}

	private void secondWalk2(NodeItem paramNodeItem1, NodeItem paramNodeItem2, double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4) {
		ParamBlock localParamBlock1 = getParams(paramNodeItem1);
		double d1 = Math.cos(paramDouble4);
		double d2 = Math.sin(paramDouble4);
		double d3 = paramDouble1 + paramDouble3 * (localParamBlock1.rx * d1 - localParamBlock1.ry * d2);
		double d4 = paramDouble2 + paramDouble3 * (localParamBlock1.rx * d2 + localParamBlock1.ry * d1);
		setLocation(paramNodeItem1, paramNodeItem2, d3, d4);
		double d5 = paramDouble3 * localParamBlock1.d;
		double d6 = 3.141592653589793D;
		double d7 = localParamBlock1.f / (paramNodeItem1.getChildCount() + 1);
		double d8 = 0.0D;
		Iterator localIterator = paramNodeItem1.getChildren();
		while (localIterator.hasNext()) {
			NodeItem localNodeItem = (NodeItem) localIterator.next();
			ParamBlock localParamBlock2 = getParams(localNodeItem);
			double d9 = localParamBlock1.c * localParamBlock2.a;
			double d10 = localParamBlock1.d * Math.tan(d9) / (1.0D - Math.tan(d9));
			d6 += d8 + d9 + d7;
			double d11 = (paramDouble3 * d10 + d5) * Math.cos(d6) + localParamBlock1.rx;
			double d12 = (paramDouble3 * d10 + d5) * Math.sin(d6) + localParamBlock1.ry;
			double d13 = d11 * d1 - d12 * d2;
			double d14 = d11 * d2 + d12 * d1;
			d8 = d9;
			secondWalk2(localNodeItem, paramNodeItem1, paramDouble1 + d13, paramDouble2 + d14, paramDouble3 * d10 / localParamBlock2.r, d6);
		}
	}

	private void secondWalk(NodeItem paramNodeItem1, NodeItem paramNodeItem2, double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4) {
		setLocation(paramNodeItem1, paramNodeItem2, paramDouble1, paramDouble2);
		ParamBlock localParamBlock1 = getParams(paramNodeItem1);
		int i = paramNodeItem1.getChildCount();
		double d1 = paramDouble3 * localParamBlock1.d;
		double d2 = paramDouble4 + 3.141592653589793D;
		double d3 = i == 0 ? 0.0D : localParamBlock1.f / i;
		double d4 = 0.0D;
		Iterator localIterator = paramNodeItem1.getChildren();
		while (localIterator.hasNext()) {
			NodeItem localNodeItem = (NodeItem) localIterator.next();
			ParamBlock localParamBlock2 = getParams(localNodeItem);
			double d5 = localParamBlock1.c * localParamBlock2.a;
			double d6 = localParamBlock1.d * Math.tan(d5) / (1.0D - Math.tan(d5));
			d2 += d4 + d5 + d3;
			double d7 = (paramDouble3 * d6 + d1) * Math.cos(d2);
			double d8 = (paramDouble3 * d6 + d1) * Math.sin(d2);
			d4 = d5;
			secondWalk(localNodeItem, paramNodeItem1, paramDouble1 + d7, paramDouble2 + d8, paramDouble3 * localParamBlock1.c, d2);
		}
	}

	private ParamBlock getParams(NodeItem paramNodeItem) {
		ParamBlock localParamBlock = (ParamBlock) paramNodeItem.getVizAttribute("balloonParams");
		if (localParamBlock == null) {
			localParamBlock = new ParamBlock();
			paramNodeItem.setVizAttribute("balloonParams", localParamBlock);
		}
		return localParamBlock;
	}

	public class ParamBlock {
		public int d;
		public int r;
		public double rx;
		public double ry;
		public double a;
		public double c;
		public double f;

		public ParamBlock() {
		}
	}
}


/* Location:              /home/vad/work/JAVA/2018.11.30/prefuse-apps.jar!/edu/berkeley/guir/prefusex/layout/BalloonTreeLayout.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */