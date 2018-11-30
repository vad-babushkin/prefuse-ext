package edu.berkeley.guir.prefuse.action.filter;

import edu.berkeley.guir.prefuse.EdgeItem;
import edu.berkeley.guir.prefuse.ItemRegistry;
import edu.berkeley.guir.prefuse.NodeItem;
import edu.berkeley.guir.prefuse.graph.Edge;
import edu.berkeley.guir.prefuse.graph.TreeNode;

import java.util.Iterator;

public class TreeEdgeFilter
		extends Filter {
	private boolean m_edgesVisible;

	public TreeEdgeFilter() {
		this(true);
	}

	public TreeEdgeFilter(boolean paramBoolean) {
		super("edge", true);
		this.m_edgesVisible = paramBoolean;
	}

	public void run(ItemRegistry paramItemRegistry, double paramDouble) {
		Iterator localIterator1 = paramItemRegistry.getNodeItems();
		while (localIterator1.hasNext()) {
			NodeItem localNodeItem = (NodeItem) localIterator1.next();
			TreeNode localTreeNode1 = (TreeNode) paramItemRegistry.getEntity(localNodeItem);
			if (localTreeNode1.getChildCount() > 0) {
				Iterator localIterator2 = localTreeNode1.getChildEdges();
				while (localIterator2.hasNext()) {
					Edge localEdge = (Edge) localIterator2.next();
					TreeNode localTreeNode2 = (TreeNode) localEdge.getAdjacentNode(localTreeNode1);
					if (paramItemRegistry.isVisible(localTreeNode2)) {
						EdgeItem localEdgeItem = paramItemRegistry.getEdgeItem(localEdge, true);
						localNodeItem.addChild(localEdgeItem);
						if (!this.m_edgesVisible) {
							localEdgeItem.setVisible(false);
						}
					}
				}
			}
		}
		super.run(paramItemRegistry, paramDouble);
	}
}


/* Location:              /home/vad/work/JAVA/2018.11.30/prefuse-apps.jar!/edu/berkeley/guir/prefuse/action/filter/TreeEdgeFilter.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */