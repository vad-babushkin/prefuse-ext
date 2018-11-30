package edu.berkeley.guir.prefuse.graph;

import edu.berkeley.guir.prefuse.graph.event.GraphEventListener;
import edu.berkeley.guir.prefuse.graph.event.GraphEventMulticaster;

public abstract class AbstractGraph
		implements Graph {
	protected GraphEventListener m_graphListener = null;

	public void addGraphEventListener(GraphEventListener paramGraphEventListener) {
		this.m_graphListener = GraphEventMulticaster.add(this.m_graphListener, paramGraphEventListener);
	}

	public void removeGraphEventListener(GraphEventListener paramGraphEventListener) {
		this.m_graphListener = GraphEventMulticaster.remove(this.m_graphListener, paramGraphEventListener);
	}

	protected void fireNodeAdded(Node paramNode) {
		if (this.m_graphListener != null) {
			this.m_graphListener.nodeAdded(this, paramNode);
		}
	}

	protected void fireNodeRemoved(Node paramNode) {
		if (this.m_graphListener != null) {
			this.m_graphListener.nodeRemoved(this, paramNode);
		}
	}

	protected void fireNodeReplaced(Node paramNode1, Node paramNode2) {
		if (this.m_graphListener != null) {
			this.m_graphListener.nodeReplaced(this, paramNode1, paramNode2);
		}
	}

	protected void fireEdgeAdded(Edge paramEdge) {
		if (this.m_graphListener != null) {
			this.m_graphListener.edgeAdded(this, paramEdge);
		}
	}

	protected void fireEdgeRemoved(Edge paramEdge) {
		if (this.m_graphListener != null) {
			this.m_graphListener.edgeRemoved(this, paramEdge);
		}
	}

	protected void fireEdgeReplaced(Edge paramEdge1, Edge paramEdge2) {
		if (this.m_graphListener != null) {
			this.m_graphListener.edgeReplaced(this, paramEdge1, paramEdge2);
		}
	}
}


/* Location:              /home/vad/work/JAVA/2018.11.30/prefuse-apps.jar!/edu/berkeley/guir/prefuse/graph/AbstractGraph.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */