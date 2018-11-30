package edu.berkeley.guir.prefuse.graph.event;

import edu.berkeley.guir.prefuse.event.EventMulticaster;
import edu.berkeley.guir.prefuse.graph.Edge;
import edu.berkeley.guir.prefuse.graph.Graph;
import edu.berkeley.guir.prefuse.graph.Node;

import java.util.EventListener;

public class GraphEventMulticaster
		extends EventMulticaster
		implements GraphEventListener {
	public void nodeAdded(Graph paramGraph, Node paramNode) {
		((GraphEventListener) this.a).nodeAdded(paramGraph, paramNode);
		((GraphEventListener) this.b).nodeAdded(paramGraph, paramNode);
	}

	public void nodeRemoved(Graph paramGraph, Node paramNode) {
		((GraphEventListener) this.a).nodeRemoved(paramGraph, paramNode);
		((GraphEventListener) this.b).nodeRemoved(paramGraph, paramNode);
	}

	public void nodeReplaced(Graph paramGraph, Node paramNode1, Node paramNode2) {
		((GraphEventListener) this.a).nodeReplaced(paramGraph, paramNode1, paramNode2);
		((GraphEventListener) this.b).nodeReplaced(paramGraph, paramNode1, paramNode2);
	}

	public void edgeAdded(Graph paramGraph, Edge paramEdge) {
		((GraphEventListener) this.a).edgeAdded(paramGraph, paramEdge);
		((GraphEventListener) this.b).edgeAdded(paramGraph, paramEdge);
	}

	public void edgeRemoved(Graph paramGraph, Edge paramEdge) {
		((GraphEventListener) this.a).edgeRemoved(paramGraph, paramEdge);
		((GraphEventListener) this.b).edgeRemoved(paramGraph, paramEdge);
	}

	public void edgeReplaced(Graph paramGraph, Edge paramEdge1, Edge paramEdge2) {
		((GraphEventListener) this.a).edgeReplaced(paramGraph, paramEdge1, paramEdge2);
		((GraphEventListener) this.b).edgeReplaced(paramGraph, paramEdge1, paramEdge2);
	}

	public static GraphEventListener add(GraphEventListener paramGraphEventListener1, GraphEventListener paramGraphEventListener2) {
		return (GraphEventListener) addInternal(paramGraphEventListener1, paramGraphEventListener2);
	}

	public static GraphEventListener remove(GraphEventListener paramGraphEventListener1, GraphEventListener paramGraphEventListener2) {
		return (GraphEventListener) removeInternal(paramGraphEventListener1, paramGraphEventListener2);
	}

	protected static EventListener addInternal(EventListener paramEventListener1, EventListener paramEventListener2) {
		if (paramEventListener1 == null) {
			return paramEventListener2;
		}
		if (paramEventListener2 == null) {
			return paramEventListener1;
		}
		return new GraphEventMulticaster(paramEventListener1, paramEventListener2);
	}

	protected EventListener remove(EventListener paramEventListener) {
		if (paramEventListener == this.a) {
			return this.b;
		}
		if (paramEventListener == this.b) {
			return this.a;
		}
		EventListener localEventListener1 = removeInternal(this.a, paramEventListener);
		EventListener localEventListener2 = removeInternal(this.b, paramEventListener);
		if ((localEventListener1 == this.a) && (localEventListener2 == this.b)) {
			return this;
		}
		return addInternal(localEventListener1, localEventListener2);
	}

	protected GraphEventMulticaster(EventListener paramEventListener1, EventListener paramEventListener2) {
		super(paramEventListener1, paramEventListener2);
	}
}


/* Location:              /home/vad/work/JAVA/2018.11.30/prefuse-apps.jar!/edu/berkeley/guir/prefuse/graph/event/GraphEventMulticaster.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */