package edu.berkeley.guir.prefuse.graph.event;

import edu.berkeley.guir.prefuse.graph.Entity;
import edu.berkeley.guir.prefuse.graph.external.GraphLoader;

import java.util.EventListener;

public abstract interface GraphLoaderListener
		extends EventListener {
	public abstract void entityLoaded(GraphLoader paramGraphLoader, Entity paramEntity);

	public abstract void entityUnloaded(GraphLoader paramGraphLoader, Entity paramEntity);
}


/* Location:              /home/vad/work/JAVA/2018.11.30/prefuse-apps.jar!/edu/berkeley/guir/prefuse/graph/event/GraphLoaderListener.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */