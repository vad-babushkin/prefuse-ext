package edu.berkeley.guir.prefuse.graph;

public abstract interface Tree
		extends Graph {
	public abstract TreeNode getRoot();

	public abstract void setRoot(TreeNode paramTreeNode);

	public abstract void changeRoot(TreeNode paramTreeNode);

	public abstract int getDepth(TreeNode paramTreeNode);

	public abstract boolean addChild(Edge paramEdge);

	public abstract boolean addChild(Node paramNode1, Node paramNode2);

	public abstract boolean removeChild(Edge paramEdge);
}


/* Location:              /home/vad/work/JAVA/2018.11.30/prefuse-apps.jar!/edu/berkeley/guir/prefuse/graph/Tree.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */