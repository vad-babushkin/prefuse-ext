package edu.berkeley.guir.prefuse.action.assignment;

import edu.berkeley.guir.prefuse.ItemRegistry;
import edu.berkeley.guir.prefuse.NodeItem;
import edu.berkeley.guir.prefuse.graph.Graph;
import edu.berkeley.guir.prefuse.graph.Tree;

public abstract class TreeLayout
		extends Layout {
	protected NodeItem m_root;

	public NodeItem getLayoutRoot() {
		return this.m_root;
	}

	public void setLayoutRoot(NodeItem paramNodeItem) {
		this.m_root = paramNodeItem;
	}

	public NodeItem getLayoutRoot(ItemRegistry paramItemRegistry) {
		if (this.m_root != null) {
			return this.m_root;
		}
		Graph localGraph = paramItemRegistry.getFilteredGraph();
		if ((localGraph instanceof Tree)) {
			return (NodeItem) ((Tree) localGraph).getRoot();
		}
		throw new IllegalStateException("The filtered graph returned by ItemRegistry.getFilteredGraph() must be a Tree instance for a TreeLayout to work. Try using a different filter (e.g. edu.berkeley.guir.prefuse.action.filter.TreeFilter).");
	}
}


/* Location:              /home/vad/work/JAVA/2018.11.30/prefuse-apps.jar!/edu/berkeley/guir/prefuse/action/assignment/TreeLayout.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */