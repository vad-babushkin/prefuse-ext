package edu.berkeley.guir.prefuse.graph;

public class DefaultEdge
		extends DefaultEntity
		implements Edge {
	protected Node m_node1;
	protected Node m_node2;
	protected boolean m_directed;

	public DefaultEdge(Node paramNode1, Node paramNode2) {
		this(paramNode1, paramNode2, false);
	}

	public DefaultEdge(Node paramNode1, Node paramNode2, boolean paramBoolean) {
		this.m_node1 = paramNode1;
		this.m_node2 = paramNode2;
		this.m_directed = paramBoolean;
	}

	public boolean isDirected() {
		return this.m_directed;
	}

	public boolean isTreeEdge() {
		if (((this.m_node1 instanceof TreeNode)) && ((this.m_node2 instanceof TreeNode))) {
			TreeNode localTreeNode1 = (TreeNode) this.m_node1;
			TreeNode localTreeNode2 = (TreeNode) this.m_node2;
			return (localTreeNode1.getParent() == localTreeNode2) || (localTreeNode2.getParent() == localTreeNode1);
		}
		return false;
	}

	public boolean isIncident(Node paramNode) {
		return (this.m_node1 == paramNode) || (this.m_node2 == paramNode);
	}

	public Node getFirstNode() {
		return this.m_node1;
	}

	public Node getSecondNode() {
		return this.m_node2;
	}

	public void setFirstNode(Node paramNode) {
		this.m_node1 = paramNode;
	}

	public void setSecondNode(Node paramNode) {
		this.m_node2 = paramNode;
	}

	public Node getAdjacentNode(Node paramNode) {
		if (paramNode == this.m_node1) {
			return this.m_node2;
		}
		if (paramNode == this.m_node2) {
			return this.m_node1;
		}
		return null;
	}

	public String toString() {
		return "Edge(" + this.m_node1 + "," + this.m_node2 + ")";
	}
}


/* Location:              /home/vad/work/JAVA/2018.11.30/prefuse-apps.jar!/edu/berkeley/guir/prefuse/graph/DefaultEdge.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */