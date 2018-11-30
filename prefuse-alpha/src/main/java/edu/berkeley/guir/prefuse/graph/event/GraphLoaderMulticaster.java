package edu.berkeley.guir.prefuse.graph.event;

import edu.berkeley.guir.prefuse.event.EventMulticaster;
import edu.berkeley.guir.prefuse.graph.Entity;
import edu.berkeley.guir.prefuse.graph.external.GraphLoader;

import java.util.EventListener;

public class GraphLoaderMulticaster
		extends EventMulticaster
		implements GraphLoaderListener {
	public void entityLoaded(GraphLoader paramGraphLoader, Entity paramEntity) {
		((GraphLoaderListener) this.a).entityLoaded(paramGraphLoader, paramEntity);
		((GraphLoaderListener) this.b).entityLoaded(paramGraphLoader, paramEntity);
	}

	public void entityUnloaded(GraphLoader paramGraphLoader, Entity paramEntity) {
		((GraphLoaderListener) this.a).entityUnloaded(paramGraphLoader, paramEntity);
		((GraphLoaderListener) this.b).entityUnloaded(paramGraphLoader, paramEntity);
	}

	public static GraphLoaderListener add(GraphLoaderListener paramGraphLoaderListener1, GraphLoaderListener paramGraphLoaderListener2) {
		return (GraphLoaderListener) addInternal(paramGraphLoaderListener1, paramGraphLoaderListener2);
	}

	public static GraphLoaderListener remove(GraphLoaderListener paramGraphLoaderListener1, GraphLoaderListener paramGraphLoaderListener2) {
		return (GraphLoaderListener) removeInternal(paramGraphLoaderListener1, paramGraphLoaderListener2);
	}

	protected static EventListener addInternal(EventListener paramEventListener1, EventListener paramEventListener2) {
		if (paramEventListener1 == null) {
			return paramEventListener2;
		}
		if (paramEventListener2 == null) {
			return paramEventListener1;
		}
		return new GraphLoaderMulticaster(paramEventListener1, paramEventListener2);
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

	protected GraphLoaderMulticaster(EventListener paramEventListener1, EventListener paramEventListener2) {
		super(paramEventListener1, paramEventListener2);
	}
}


/* Location:              /home/vad/work/JAVA/2018.11.30/prefuse-apps.jar!/edu/berkeley/guir/prefuse/graph/event/GraphLoaderMulticaster.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */