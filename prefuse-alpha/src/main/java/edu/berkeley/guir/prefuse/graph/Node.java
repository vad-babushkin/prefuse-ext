package edu.berkeley.guir.prefuse.graph;

import java.util.Iterator;

public abstract interface Node
		extends Entity {
	public abstract boolean addEdge(Edge paramEdge);

	public abstract boolean addEdge(int paramInt, Edge paramEdge);

	public abstract Edge getEdge(Node paramNode);

	public abstract Edge getEdge(int paramInt);

	public abstract int getEdgeCount();

	public abstract Iterator getEdges();

	public abstract int getIndex(Edge paramEdge);

	public abstract int getIndex(Node paramNode);

	public abstract Node getNeighbor(int paramInt);

	public abstract Iterator getNeighbors();

	public abstract boolean isIncidentEdge(Edge paramEdge);

	public abstract boolean isNeighbor(Node paramNode);

	public abstract void removeAllNeighbors();

	public abstract boolean removeEdge(Edge paramEdge);

	public abstract Edge removeEdge(int paramInt);

	public abstract boolean removeNeighbor(Node paramNode);

	public abstract Node removeNeighbor(int paramInt);
}


/* Location:              /home/vad/work/JAVA/2018.11.30/prefuse-apps.jar!/edu/berkeley/guir/prefuse/graph/Node.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */