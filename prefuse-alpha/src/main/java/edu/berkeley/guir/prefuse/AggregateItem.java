package edu.berkeley.guir.prefuse;

import edu.berkeley.guir.prefuse.graph.Edge;
import edu.berkeley.guir.prefuse.graph.Entity;
import edu.berkeley.guir.prefuse.graph.Node;

import java.util.List;

public class AggregateItem
		extends NodeItem {
	private double m_orientation = 0.0D;
	private double m_startOrientation = 0.0D;
	private double m_endOrientation = 0.0D;
	private int m_aggrSize;
	private NodeItem m_nitem;

	public void init(ItemRegistry paramItemRegistry, String paramString, Entity paramEntity) {
		if ((paramEntity != null) && (!(paramEntity instanceof Node))) {
			throw new IllegalArgumentException("AggregateItem can only represent an Entity of type Node.");
		}
		super.init(paramItemRegistry, paramString, paramEntity);
		Object localObject = null;
		if ((paramEntity instanceof Node)) {
			localObject = this.m_registry.getNodeItem((Node) paramEntity);
		} else if ((paramEntity instanceof Edge)) {
			localObject = this.m_registry.getEdgeItem((Edge) paramEntity);
		}
		if (localObject != null) {
			setDOI(((VisualItem) localObject).getDOI());
			setStartLocation(((VisualItem) localObject).getStartLocation());
			setLocation(((VisualItem) localObject).getLocation());
			setEndLocation(((VisualItem) localObject).getEndLocation());
			setStartSize(((VisualItem) localObject).getStartSize());
			setSize(((VisualItem) localObject).getSize());
			setEndSize(((VisualItem) localObject).getEndSize());
			setFont(((VisualItem) localObject).getFont());
		}
	}

	public void clear() {
		super.clear();
		this.m_aggrSize = 0;
		this.m_orientation = 0.0D;
		this.m_startOrientation = 0.0D;
		this.m_endOrientation = 0.0D;
		this.m_location.setLocation(0.0D, 0.0D);
		this.m_startLocation.setLocation(0.0D, 0.0D);
		this.m_endLocation.setLocation(0.0D, 0.0D);
	}

	public List getEntities() {
		return this.m_registry.getEntities(this);
	}

	public Entity getEntity(int paramInt) {
		return (Entity) this.m_registry.getEntities(this).get(paramInt);
	}

	public NodeItem getNodeItem() {
		return this.m_nitem;
	}

	public void setNodeItem(NodeItem paramNodeItem) {
		this.m_nitem = paramNodeItem;
	}

	public int getAggregateSize() {
		return this.m_aggrSize;
	}

	public void setAggregateSize(int paramInt) {
		this.m_aggrSize = paramInt;
	}

	public double getOrientation() {
		return this.m_orientation;
	}

	public void setOrientation(double paramDouble) {
		this.m_orientation = paramDouble;
	}

	public double getEndOrientation() {
		return this.m_endOrientation;
	}

	public double getStartOrientation() {
		return this.m_startOrientation;
	}

	public void setEndOrientation(double paramDouble) {
		this.m_endOrientation = paramDouble;
	}

	public void setStartOrientation(double paramDouble) {
		this.m_startOrientation = paramDouble;
	}
}


/* Location:              /home/vad/work/JAVA/2018.11.30/prefuse-apps.jar!/edu/berkeley/guir/prefuse/AggregateItem.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */