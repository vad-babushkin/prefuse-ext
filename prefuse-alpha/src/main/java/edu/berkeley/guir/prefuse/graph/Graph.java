package edu.berkeley.guir.prefuse.graph;

import edu.berkeley.guir.prefuse.graph.event.GraphEventListener;

import java.util.Iterator;

public abstract interface Graph {
	public abstract int getNodeCount();

	public abstract int getEdgeCount();

	public abstract Iterator getNodes();

	public abstract Iterator getEdges();

	public abstract boolean isDirected();

	public abstract boolean addNode(Node paramNode);

	public abstract boolean addEdge(Edge paramEdge);

	public abstract boolean removeNode(Node paramNode);

	public abstract boolean removeEdge(Edge paramEdge);

	public abstract boolean replaceNode(Node paramNode1, Node paramNode2);

	public abstract boolean replaceEdge(Edge paramEdge1, Edge paramEdge2);

	public abstract boolean contains(Node paramNode);

	public abstract boolean contains(Edge paramEdge);

	public abstract void addGraphEventListener(GraphEventListener paramGraphEventListener);

	public abstract void removeGraphEventListener(GraphEventListener paramGraphEventListener);
}


/* Location:              /home/vad/work/JAVA/2018.11.30/prefuse-apps.jar!/edu/berkeley/guir/prefuse/graph/Graph.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */