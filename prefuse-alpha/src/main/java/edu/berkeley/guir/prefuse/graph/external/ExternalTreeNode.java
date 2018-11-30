//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package edu.berkeley.guir.prefuse.graph.external;

import edu.berkeley.guir.prefuse.graph.DefaultTreeNode;
import edu.berkeley.guir.prefuse.graph.Edge;
import edu.berkeley.guir.prefuse.graph.Node;
import edu.berkeley.guir.prefuse.graph.TreeNode;
import java.util.Iterator;

public class ExternalTreeNode extends DefaultTreeNode implements ExternalEntity {
	protected static final int LOAD_CHILDREN = 1;
	protected static final int LOAD_PARENT = 2;
	protected static final int LOAD_ALL = 3;
	protected GraphLoader m_loader;
	protected boolean m_ploaded = false;
	protected boolean m_ploadStarted = false;
	protected boolean m_loaded = false;
	protected boolean m_loadStarted = false;

	public ExternalTreeNode() {
	}

	protected void checkLoadedStatus(int var1) {
		this.touch();
		if ((var1 & 1) > 0 && !this.m_loadStarted) {
			this.m_loadStarted = true;
			this.m_loader.loadChildren(this);
		}

		if ((var1 & 2) > 0 && !this.m_ploadStarted) {
			this.m_ploadStarted = true;
			this.m_loader.loadParent(this);
		}

	}

	public void setLoader(GraphLoader var1) {
		this.m_loader = var1;
	}

	void setChildrenLoaded(boolean var1) {
		this.m_loaded = var1;
		this.m_loadStarted = var1;
	}

	void setParentLoaded(boolean var1) {
		this.m_ploaded = var1;
		this.m_ploadStarted = var1;
	}

	public boolean isParentLoaded() {
		return this.m_ploaded;
	}

	public boolean isChildrenLoaded() {
		return this.m_loaded;
	}

	public void touch() {
		this.m_loader.touch(this);
	}

	public void unload() {
		Iterator var1;
		Edge var2;
		if (this.m_children != null) {
			var1 = this.m_children.iterator();

			while(var1.hasNext()) {
				var2 = (Edge)var1.next();
				TreeNode var3 = (TreeNode)var2.getAdjacentNode(this);
				var3.removeAsChild(this);
				if (var3 instanceof ExternalTreeNode) {
					((ExternalTreeNode)var3).setParentLoaded(false);
				}
			}

			this.m_children.clear();
		}

		this.m_parent.removeChild(this);
		if (this.m_parent instanceof ExternalTreeNode) {
			((ExternalTreeNode)this.m_parent).setChildrenLoaded(false);
		}

		this.m_parent = null;
		this.m_parentEdge = null;
		var1 = this.m_edges.iterator();

		while(var1.hasNext()) {
			var2 = (Edge)var1.next();
			Node var4 = var2.getAdjacentNode(this);
			var4.removeEdge(var2);
		}

		this.m_edges.clear();
	}

	public boolean addChild(Edge var1) {
		this.touch();
		return super.addChild(var1);
	}

	public boolean addChild(int var1, Edge var2) {
		this.touch();
		return super.addChild(var1, var2);
	}

	public TreeNode getChild(int var1) {
		this.checkLoadedStatus(1);
		return super.getChild(var1);
	}

	public int getChildCount() {
		this.touch();
		return super.getChildCount();
	}

	public Edge getChildEdge(int var1) {
		this.checkLoadedStatus(1);
		return super.getChildEdge(var1);
	}

	public Iterator getChildEdges() {
		this.checkLoadedStatus(1);
		return super.getChildEdges();
	}

	public int getChildIndex(Edge var1) {
		this.touch();
		return super.getChildIndex(var1);
	}

	public int getChildIndex(TreeNode var1) {
		this.touch();
		return super.getChildIndex(var1);
	}

	public Iterator getChildren() {
		this.checkLoadedStatus(1);
		return super.getChildren();
	}

	public TreeNode getNextSibling() {
		this.checkLoadedStatus(2);
		return super.getNextSibling();
	}

	public int getDescendantCount() {
		this.touch();
		return super.getDescendantCount();
	}

	public TreeNode getParent() {
		this.checkLoadedStatus(2);
		return super.getParent();
	}

	public Edge getParentEdge() {
		this.checkLoadedStatus(2);
		return super.getParentEdge();
	}

	public TreeNode getPreviousSibling() {
		this.checkLoadedStatus(2);
		return super.getPreviousSibling();
	}

	public boolean isChild(TreeNode var1) {
		this.touch();
		return super.isChild(var1);
	}

	public boolean isChildEdge(Edge var1) {
		this.touch();
		return super.isChildEdge(var1);
	}

	public boolean isDescendant(TreeNode var1) {
		this.touch();
		return super.isDescendant(var1);
	}

	public boolean isSibling(TreeNode var1) {
		this.checkLoadedStatus(2);
		return super.isSibling(var1);
	}

	public void removeAllAsChildren() {
		this.touch();
		super.removeAllAsChildren();
	}

	public void removeAllChildren() {
		this.touch();
		super.removeAllChildren();
	}

	public TreeNode removeAsChild(int var1) {
		this.touch();
		return super.removeAsChild(var1);
	}

	public boolean removeAsChild(TreeNode var1) {
		this.touch();
		return super.removeAsChild(var1);
	}

	public TreeNode removeChild(int var1) {
		this.touch();
		return super.removeChild(var1);
	}

	public boolean removeChild(TreeNode var1) {
		this.touch();
		return super.removeChild(var1);
	}

	public boolean removeChildEdge(Edge var1) {
		this.touch();
		return super.removeChildEdge(var1);
	}

	public Edge removeChildEdge(int var1) {
		this.touch();
		return super.removeChildEdge(var1);
	}

	public boolean setAsChild(int var1, TreeNode var2) {
		this.touch();
		return super.setAsChild(var1, var2);
	}

	public boolean setAsChild(TreeNode var1) {
		this.touch();
		return super.setAsChild(var1);
	}

	public void setDescendantCount(int var1) {
		this.touch();
		super.setDescendantCount(var1);
	}

	public void setParentEdge(Edge var1) {
		this.touch();
		super.setParentEdge(var1);
	}

	public boolean addEdge(Edge var1) {
		this.touch();
		return super.addEdge(var1);
	}

	public boolean addEdge(int var1, Edge var2) {
		this.touch();
		return super.addEdge(var1, var2);
	}

	public Edge getEdge(int var1) {
		this.checkLoadedStatus(3);
		return super.getEdge(var1);
	}

	public Edge getEdge(Node var1) {
		this.checkLoadedStatus(3);
		return super.getEdge(var1);
	}

	public int getEdgeCount() {
		this.touch();
		return super.getEdgeCount();
	}

	public Iterator getEdges() {
		this.checkLoadedStatus(3);
		return super.getEdges();
	}

	public int getIndex(Edge var1) {
		this.touch();
		return super.getIndex(var1);
	}

	public int getIndex(Node var1) {
		this.touch();
		return super.getIndex(var1);
	}

	public Node getNeighbor(int var1) {
		this.checkLoadedStatus(3);
		return super.getNeighbor(var1);
	}

	public Iterator getNeighbors() {
		this.checkLoadedStatus(3);
		return super.getNeighbors();
	}

	public boolean isIncidentEdge(Edge var1) {
		this.touch();
		return super.isIncidentEdge(var1);
	}

	public boolean isNeighbor(Node var1) {
		this.touch();
		return super.isNeighbor(var1);
	}

	public boolean removeEdge(Edge var1) {
		this.touch();
		return super.removeEdge(var1);
	}

	public Edge removeEdge(int var1) {
		this.touch();
		return super.removeEdge(var1);
	}

	public Node removeNeighbor(int var1) {
		this.touch();
		return super.removeNeighbor(var1);
	}

	public boolean removeNeighbor(Node var1) {
		this.touch();
		return super.removeNeighbor(var1);
	}
}
