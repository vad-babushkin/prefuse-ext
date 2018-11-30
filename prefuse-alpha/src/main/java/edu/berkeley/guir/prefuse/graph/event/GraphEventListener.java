package edu.berkeley.guir.prefuse.graph.event;

import edu.berkeley.guir.prefuse.graph.Edge;
import edu.berkeley.guir.prefuse.graph.Graph;
import edu.berkeley.guir.prefuse.graph.Node;

import java.util.EventListener;

public abstract interface GraphEventListener
		extends EventListener {
	public abstract void nodeAdded(Graph paramGraph, Node paramNode);

	public abstract void nodeRemoved(Graph paramGraph, Node paramNode);

	public abstract void nodeReplaced(Graph paramGraph, Node paramNode1, Node paramNode2);

	public abstract void edgeAdded(Graph paramGraph, Edge paramEdge);

	public abstract void edgeRemoved(Graph paramGraph, Edge paramEdge);

	public abstract void edgeReplaced(Graph paramGraph, Edge paramEdge1, Edge paramEdge2);
}


/* Location:              /home/vad/work/JAVA/2018.11.30/prefuse-apps.jar!/edu/berkeley/guir/prefuse/graph/event/GraphEventListener.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */