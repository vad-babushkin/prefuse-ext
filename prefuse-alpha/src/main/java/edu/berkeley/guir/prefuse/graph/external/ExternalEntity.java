package edu.berkeley.guir.prefuse.graph.external;

import edu.berkeley.guir.prefuse.graph.Node;

public abstract interface ExternalEntity
		extends Node {
	public abstract void setLoader(GraphLoader paramGraphLoader);

	public abstract void unload();

	public abstract void touch();
}


/* Location:              /home/vad/work/JAVA/2018.11.30/prefuse-apps.jar!/edu/berkeley/guir/prefuse/graph/external/ExternalEntity.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */