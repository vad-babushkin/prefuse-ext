package edu.berkeley.guir.prefuse.graph;

import java.util.Iterator;

public abstract interface TreeNode
		extends Node {
	public abstract boolean addChild(Edge paramEdge);

	public abstract boolean addChild(int paramInt, Edge paramEdge);

	public abstract TreeNode getChild(int paramInt);

	public abstract int getChildCount();

	public abstract Edge getChildEdge(int paramInt);

	public abstract Iterator getChildEdges();

	public abstract int getChildIndex(Edge paramEdge);

	public abstract int getChildIndex(TreeNode paramTreeNode);

	public abstract Iterator getChildren();

	public abstract TreeNode getNextSibling();

	public abstract int getDescendantCount();

	public abstract TreeNode getParent();

	public abstract Edge getParentEdge();

	public abstract TreeNode getPreviousSibling();

	public abstract boolean isChild(TreeNode paramTreeNode);

	public abstract boolean isChildEdge(Edge paramEdge);

	public abstract boolean isDescendant(TreeNode paramTreeNode);

	public abstract boolean isSibling(TreeNode paramTreeNode);

	public abstract void removeAllAsChildren();

	public abstract void removeAllChildren();

	public abstract TreeNode removeAsChild(int paramInt);

	public abstract boolean removeAsChild(TreeNode paramTreeNode);

	public abstract TreeNode removeChild(int paramInt);

	public abstract boolean removeChild(TreeNode paramTreeNode);

	public abstract boolean removeChildEdge(Edge paramEdge);

	public abstract Edge removeChildEdge(int paramInt);

	public abstract boolean setAsChild(int paramInt, TreeNode paramTreeNode);

	public abstract boolean setAsChild(TreeNode paramTreeNode);

	public abstract void setDescendantCount(int paramInt);

	public abstract void setParentEdge(Edge paramEdge);
}


/* Location:              /home/vad/work/JAVA/2018.11.30/prefuse-apps.jar!/edu/berkeley/guir/prefuse/graph/TreeNode.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */