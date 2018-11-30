package edu.berkeley.guir.prefuse.graph;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;

public class DefaultGraph
		extends AbstractGraph {
	protected LinkedHashSet m_nodes;
	protected LinkedHashSet m_edges;
	protected boolean m_directed = false;

	public DefaultGraph(Collection paramCollection, boolean paramBoolean) {
		this(paramBoolean);
		this.m_nodes.addAll(paramCollection);
		Iterator localIterator1 = this.m_nodes.iterator();
		while (localIterator1.hasNext()) {
			Node localNode = (Node) localIterator1.next();
			Iterator localIterator2 = localNode.getEdges();
			while (localIterator2.hasNext()) {
				Edge localEdge = (Edge) localIterator2.next();
				if (localEdge.isDirected() != paramBoolean) {
					throw new IllegalStateException("Directedness of edge and graph differ");
				}
				this.m_edges.add(localEdge);
			}
		}
	}

	public DefaultGraph(Collection paramCollection) {
		this(paramCollection, false);
	}

	public DefaultGraph() {
		this(false);
	}

	public DefaultGraph(boolean paramBoolean) {
		this.m_directed = paramBoolean;
		this.m_nodes = new LinkedHashSet();
		this.m_edges = new LinkedHashSet();
	}

	public void reinit(boolean paramBoolean) {
		this.m_nodes.clear();
		this.m_edges.clear();
		this.m_directed = paramBoolean;
	}

	public boolean addNode(Node paramNode) {
		if (this.m_nodes.contains(paramNode)) {
			return false;
		}
		this.m_nodes.add(paramNode);
		fireNodeAdded(paramNode);
		return true;
	}

	public boolean removeNode(Node paramNode) {
		if (!this.m_nodes.contains(paramNode)) {
			return false;
		}
		int i = paramNode.getEdgeCount();
		Object localObject;
		for (int j = 0; j < i; j++) {
			localObject = paramNode.removeEdge(0);
			if (!((Edge) localObject).isDirected()) {
				Node localNode = ((Edge) localObject).getAdjacentNode(paramNode);
				localNode.removeNeighbor(paramNode);
			}
			this.m_edges.remove(localObject);
			fireEdgeRemoved((Edge) localObject);
		}
		this.m_nodes.remove(paramNode);
		if (this.m_directed) {
			Iterator localIterator = this.m_nodes.iterator();
			while (localIterator.hasNext()) {
				localObject = (Node) localIterator.next();
				int k = ((Node) localObject).getIndex(paramNode);
				if (k > -1) {
					Edge localEdge = ((Node) localObject).removeEdge(k);
					this.m_edges.remove(localEdge);
					fireEdgeRemoved(localEdge);
				}
			}
		}
		fireNodeRemoved(paramNode);
		return true;
	}

	public boolean addEdge(Node paramNode1, Node paramNode2) {
		return addEdge(new DefaultEdge(paramNode1, paramNode2, this.m_directed));
	}

	public boolean addEdge(Edge paramEdge) {
		if ((this.m_directed ^ paramEdge.isDirected())) {
			throw new IllegalStateException("Directedness of edge and graph differ");
		}
		Node localNode1 = paramEdge.getFirstNode();
		Node localNode2 = paramEdge.getSecondNode();
		if ((this.m_edges.contains(paramEdge)) || (localNode1.isNeighbor(localNode2)) || ((!this.m_directed) && (localNode2.isNeighbor(localNode1)))) {
			return false;
		}
		localNode1.addEdge(paramEdge);
		if (!this.m_directed) {
			localNode2.addEdge(paramEdge);
		}
		this.m_edges.add(paramEdge);
		fireEdgeAdded(paramEdge);
		return true;
	}

	public boolean removeEdge(Edge paramEdge) {
		if ((this.m_directed ^ paramEdge.isDirected())) {
			throw new IllegalStateException("Directedness of edge and graph differ");
		}
		Node localNode1 = paramEdge.getFirstNode();
		Node localNode2 = paramEdge.getSecondNode();
		if ((!localNode1.isNeighbor(localNode2)) || ((!this.m_directed) && (!localNode2.isNeighbor(localNode1)))) {
			return false;
		}
		localNode1.removeNeighbor(localNode2);
		if (!this.m_directed) {
			localNode2.removeNeighbor(localNode1);
		}
		fireEdgeRemoved(paramEdge);
		return true;
	}

	public boolean replaceNode(Node paramNode1, Node paramNode2) {
		if ((paramNode2.getEdgeCount() > 0) || (!contains(paramNode1)) || (contains(paramNode2))) {
			return false;
		}
		Iterator localIterator = paramNode1.getEdges();
		Object localObject;
		while (localIterator.hasNext()) {
			localObject = (Edge) localIterator.next();
			if (((Edge) localObject).getFirstNode() == paramNode1) {
				((Edge) localObject).setFirstNode(paramNode2);
			} else {
				((Edge) localObject).setSecondNode(paramNode2);
			}
			paramNode2.addEdge((Edge) localObject);
		}
		paramNode1.removeAllNeighbors();
		if (this.m_directed) {
			localIterator = this.m_nodes.iterator();
			while (localIterator.hasNext()) {
				localObject = (Node) localIterator.next();
				int i = ((Node) localObject).getIndex(paramNode1);
				if (i > -1) {
					Edge localEdge = ((Node) localObject).getEdge(i);
					if (localEdge.getFirstNode() == paramNode1) {
						localEdge.setFirstNode(paramNode2);
					} else {
						localEdge.setSecondNode(paramNode2);
					}
				}
			}
		}
		fireNodeReplaced(paramNode1, paramNode2);
		return true;
	}

	public boolean replaceEdge(Edge paramEdge1, Edge paramEdge2) {
		int i = (this.m_edges.contains(paramEdge1)) && (!this.m_edges.contains(paramEdge2)) && (paramEdge2.isDirected() == this.m_directed) ? 1 : 0;
		if (i == 0) {
			return false;
		}
		Node localNode1 = paramEdge1.getFirstNode();
		Node localNode2 = paramEdge1.getSecondNode();
		Node localNode3 = paramEdge2.getFirstNode();
		Node localNode4 = paramEdge2.getSecondNode();
		if (this.m_directed) {
			i = (localNode1 == localNode3) && (localNode2 == localNode4) ? 1 : 0;
		} else {
			i = ((localNode1 == localNode3) && (localNode2 == localNode4)) || ((localNode1 == localNode4) && (localNode2 == localNode3)) ? 1 : 0;
		}
		if (i != 0) {
			int j = localNode1.getIndex(paramEdge1);
			localNode1.removeEdge(j);
			localNode1.addEdge(j, paramEdge2);
			if (!this.m_directed) {
				j = localNode2.getIndex(paramEdge1);
				localNode2.removeEdge(j);
				localNode2.addEdge(j, paramEdge2);
			}
			fireEdgeReplaced(paramEdge1, paramEdge2);
			return true;
		}
		return false;
	}

	public int getNodeCount() {
		return this.m_nodes.size();
	}

	public int getEdgeCount() {
		return this.m_edges.size();
	}

	public Iterator getNodes() {
		return this.m_nodes.iterator();
	}

	public Iterator getEdges() {
		return this.m_edges.iterator();
	}

	public boolean isDirected() {
		return this.m_directed;
	}

	public boolean contains(Node paramNode) {
		return this.m_nodes.contains(paramNode);
	}

	public boolean contains(Edge paramEdge) {
		return this.m_edges.contains(paramEdge);
	}
}


/* Location:              /home/vad/work/JAVA/2018.11.30/prefuse-apps.jar!/edu/berkeley/guir/prefuse/graph/DefaultGraph.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */