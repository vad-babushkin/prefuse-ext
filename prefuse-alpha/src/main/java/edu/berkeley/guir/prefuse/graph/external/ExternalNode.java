package edu.berkeley.guir.prefuse.graph.external;

import edu.berkeley.guir.prefuse.graph.DefaultNode;
import edu.berkeley.guir.prefuse.graph.Edge;
import edu.berkeley.guir.prefuse.graph.Node;

import java.util.Iterator;

public class ExternalNode
		extends DefaultNode
		implements ExternalEntity {
	protected GraphLoader m_loader;
	protected boolean m_loaded = false;
	protected boolean m_loadStarted = false;

	protected void checkLoadedStatus() {
		touch();
		if (!this.m_loadStarted) {
			this.m_loadStarted = true;
			this.m_loader.loadNeighbors(this);
		}
	}

	public void setLoader(GraphLoader paramGraphLoader) {
		this.m_loader = paramGraphLoader;
	}

	void setNeighborsLoaded(boolean paramBoolean) {
		this.m_loaded = paramBoolean;
		this.m_loadStarted = paramBoolean;
	}

	public boolean isNeighborsLoaded() {
		return this.m_loaded;
	}

	public void touch() {
		this.m_loader.touch(this);
	}

	public void unload() {
		Iterator localIterator = this.m_edges.iterator();
		while (localIterator.hasNext()) {
			Edge localEdge = (Edge) localIterator.next();
			Node localNode = localEdge.getAdjacentNode(this);
			localNode.removeEdge(localEdge);
			if ((localNode instanceof ExternalNode)) {
				((ExternalNode) localNode).setNeighborsLoaded(false);
			} else if ((localNode instanceof ExternalTreeNode)) {
				((ExternalTreeNode) localNode).setParentLoaded(false);
				((ExternalTreeNode) localNode).setChildrenLoaded(false);
			}
		}
		this.m_edges.clear();
	}

	public boolean addEdge(Edge paramEdge) {
		touch();
		return super.addEdge(paramEdge);
	}

	public boolean addEdge(int paramInt, Edge paramEdge) {
		touch();
		return super.addEdge(paramInt, paramEdge);
	}

	public Edge getEdge(int paramInt) {
		checkLoadedStatus();
		return super.getEdge(paramInt);
	}

	public Edge getEdge(Node paramNode) {
		checkLoadedStatus();
		return super.getEdge(paramNode);
	}

	public int getEdgeCount() {
		touch();
		return super.getEdgeCount();
	}

	public Iterator getEdges() {
		checkLoadedStatus();
		return super.getEdges();
	}

	public int getIndex(Edge paramEdge) {
		touch();
		return super.getIndex(paramEdge);
	}

	public int getIndex(Node paramNode) {
		touch();
		return super.getIndex(paramNode);
	}

	public Node getNeighbor(int paramInt) {
		checkLoadedStatus();
		return super.getNeighbor(paramInt);
	}

	public Iterator getNeighbors() {
		checkLoadedStatus();
		return super.getNeighbors();
	}

	public boolean isIncidentEdge(Edge paramEdge) {
		touch();
		return super.isIncidentEdge(paramEdge);
	}

	public boolean isNeighbor(Node paramNode) {
		touch();
		return super.isNeighbor(paramNode);
	}

	public boolean removeEdge(Edge paramEdge) {
		touch();
		return super.removeEdge(paramEdge);
	}

	public Edge removeEdge(int paramInt) {
		touch();
		return super.removeEdge(paramInt);
	}

	public Node removeNeighbor(int paramInt) {
		touch();
		return super.removeNeighbor(paramInt);
	}

	public boolean removeNeighbor(Node paramNode) {
		touch();
		return super.removeNeighbor(paramNode);
	}
}


/* Location:              /home/vad/work/JAVA/2018.11.30/prefuse-apps.jar!/edu/berkeley/guir/prefuse/graph/external/ExternalNode.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */