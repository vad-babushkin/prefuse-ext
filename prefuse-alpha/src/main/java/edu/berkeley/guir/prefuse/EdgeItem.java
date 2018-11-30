package edu.berkeley.guir.prefuse;

import edu.berkeley.guir.prefuse.graph.Edge;
import edu.berkeley.guir.prefuse.graph.Entity;
import edu.berkeley.guir.prefuse.graph.Node;

public class EdgeItem
		extends VisualItem
		implements Edge {
	protected NodeItem m_node1;
	protected NodeItem m_node2;

	public void init(ItemRegistry paramItemRegistry, String paramString, Entity paramEntity) {
		if ((paramEntity != null) && (!(paramEntity instanceof Edge))) {
			throw new IllegalArgumentException("EdgeItem can only represent an Entity of type Edge.");
		}
		super.init(paramItemRegistry, paramString, paramEntity);
		Edge localEdge = (Edge) paramEntity;
		Node localNode1 = localEdge.getFirstNode();
		Node localNode2 = localEdge.getSecondNode();
		NodeItem localNodeItem1 = getItem(localNode1);
		setFirstNode(localNodeItem1);
		NodeItem localNodeItem2 = getItem(localNode2);
		setSecondNode(localNodeItem2);
	}

	protected NodeItem getItem(Node paramNode) {
		return this.m_registry.getNodeItem(paramNode);
	}

	private void nodeItemCheck(Node paramNode) {
		if (!(paramNode instanceof NodeItem)) {
			throw new IllegalArgumentException("Node must be an instance of NodeItem");
		}
	}

	public boolean isDirected() {
		return ((Edge) this.m_entity).isDirected();
	}

	public boolean isTreeEdge() {
		NodeItem localNodeItem1 = this.m_node1;
		NodeItem localNodeItem2 = this.m_node2;
		return (localNodeItem1.getParent() == localNodeItem2) || (localNodeItem2.getParent() == localNodeItem1);
	}

	public Node getAdjacentNode(Node paramNode) {
		nodeItemCheck(paramNode);
		if (this.m_node1 == paramNode) {
			return this.m_node2;
		}
		if (this.m_node2 == paramNode) {
			return this.m_node1;
		}
		throw new IllegalArgumentException("The given node is not incident on this Edge.");
	}

	public Node getFirstNode() {
		return this.m_node1;
	}

	public void setFirstNode(Node paramNode) {
		nodeItemCheck(paramNode);
		this.m_node1 = ((NodeItem) paramNode);
	}

	public Node getSecondNode() {
		return this.m_node2;
	}

	public void setSecondNode(Node paramNode) {
		nodeItemCheck(paramNode);
		this.m_node2 = ((NodeItem) paramNode);
	}

	public boolean isIncident(Node paramNode) {
		return (paramNode == this.m_node1) || (paramNode == this.m_node2);
	}
}


/* Location:              /home/vad/work/JAVA/2018.11.30/prefuse-apps.jar!/edu/berkeley/guir/prefuse/EdgeItem.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */