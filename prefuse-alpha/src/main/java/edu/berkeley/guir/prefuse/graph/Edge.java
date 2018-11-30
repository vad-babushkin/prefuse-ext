package edu.berkeley.guir.prefuse.graph;

public abstract interface Edge
		extends Entity {
	public abstract boolean isDirected();

	public abstract boolean isTreeEdge();

	public abstract boolean isIncident(Node paramNode);

	public abstract Node getFirstNode();

	public abstract Node getSecondNode();

	public abstract void setFirstNode(Node paramNode);

	public abstract void setSecondNode(Node paramNode);

	public abstract Node getAdjacentNode(Node paramNode);
}


/* Location:              /home/vad/work/JAVA/2018.11.30/prefuse-apps.jar!/edu/berkeley/guir/prefuse/graph/Edge.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */