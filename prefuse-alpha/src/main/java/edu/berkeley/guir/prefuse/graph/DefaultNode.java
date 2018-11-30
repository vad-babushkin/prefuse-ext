package edu.berkeley.guir.prefuse.graph;

import edu.berkeley.guir.prefuse.collections.NodeIterator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

public class DefaultNode
		extends DefaultEntity
		implements Node {
	protected List m_edges = new ArrayList(3);

	public boolean addEdge(Edge paramEdge) {
		return addEdge(this.m_edges.size(), paramEdge);
	}

	public boolean addEdge(int paramInt, Edge paramEdge) {
		if ((paramEdge.isDirected()) && (this != paramEdge.getFirstNode())) {
			throw new IllegalArgumentException("Directed edges must have the source as the first node in the Edge.");
		}
		Node localNode = paramEdge.getAdjacentNode(this);
		if (localNode == null) {
			throw new IllegalArgumentException("The Edge must be incident on this Node.");
		}
		if (isNeighbor(localNode)) {
			return false;
		}
		this.m_edges.add(paramInt, paramEdge);
		return true;
	}

	public Edge getEdge(int paramInt) {
		return (Edge) this.m_edges.get(paramInt);
	}

	public Edge getEdge(Node paramNode) {
		for (int i = 0; i < this.m_edges.size(); i++) {
			Edge localEdge = (Edge) this.m_edges.get(i);
			if (paramNode == localEdge.getAdjacentNode(this)) {
				return localEdge;
			}
		}
		throw new NoSuchElementException();
	}

	public int getEdgeCount() {
		return this.m_edges.size();
	}

	public Iterator getEdges() {
		return this.m_edges.iterator();
	}

	public int getIndex(Edge paramEdge) {
		return this.m_edges.indexOf(paramEdge);
	}

	public int getIndex(Node paramNode) {
		for (int i = 0; i < this.m_edges.size(); i++) {
			if (paramNode == ((Edge) this.m_edges.get(i)).getAdjacentNode(this)) {
				return i;
			}
		}
		return -1;
	}

	public Node getNeighbor(int paramInt) {
		return ((Edge) this.m_edges.get(paramInt)).getAdjacentNode(this);
	}

	public Iterator getNeighbors() {
		return new NodeIterator(this.m_edges.iterator(), this);
	}

	public boolean isIncidentEdge(Edge paramEdge) {
		return this.m_edges.indexOf(paramEdge) > -1;
	}

	public boolean isNeighbor(Node paramNode) {
		return getIndex(paramNode) > -1;
	}

	public void removeAllNeighbors() {
		this.m_edges.clear();
	}

	public boolean removeEdge(Edge paramEdge) {
		int i = this.m_edges.indexOf(paramEdge);
		return this.m_edges.remove(i) != null;
	}

	public Edge removeEdge(int paramInt) {
		return (Edge) this.m_edges.remove(paramInt);
	}

	public boolean removeNeighbor(Node paramNode) {
		for (int i = 0; i < this.m_edges.size(); i++) {
			if (paramNode == ((Edge) this.m_edges.get(i)).getAdjacentNode(this)) {
				return this.m_edges.remove(i) != null;
			}
		}
		return false;
	}

	public Node removeNeighbor(int paramInt) {
		return ((Edge) this.m_edges.remove(paramInt)).getAdjacentNode(this);
	}
}


/* Location:              /home/vad/work/JAVA/2018.11.30/prefuse-apps.jar!/edu/berkeley/guir/prefuse/graph/DefaultNode.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */