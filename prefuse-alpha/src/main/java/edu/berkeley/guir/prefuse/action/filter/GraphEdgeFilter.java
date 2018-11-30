package edu.berkeley.guir.prefuse.action.filter;

import edu.berkeley.guir.prefuse.EdgeItem;
import edu.berkeley.guir.prefuse.ItemRegistry;
import edu.berkeley.guir.prefuse.NodeItem;
import edu.berkeley.guir.prefuse.graph.Edge;
import edu.berkeley.guir.prefuse.graph.Node;

import java.util.Iterator;

public class GraphEdgeFilter
		extends Filter {
	private boolean m_edgesVisible;

	public GraphEdgeFilter() {
		this(true);
	}

	public GraphEdgeFilter(boolean paramBoolean) {
		super("edge", true);
		this.m_edgesVisible = paramBoolean;
	}

	public void run(ItemRegistry paramItemRegistry, double paramDouble) {
		Iterator localIterator1 = paramItemRegistry.getNodeItems();
		while (localIterator1.hasNext()) {
			NodeItem localNodeItem = (NodeItem) localIterator1.next();
			Node localNode1 = (Node) localNodeItem.getEntity();
			Iterator localIterator2 = localNode1.getEdges();
			while (localIterator2.hasNext()) {
				Edge localEdge = (Edge) localIterator2.next();
				Node localNode2 = localEdge.getAdjacentNode(localNode1);
				if (paramItemRegistry.isVisible(localNode2)) {
					EdgeItem localEdgeItem = paramItemRegistry.getEdgeItem(localEdge, true);
					localNodeItem.addEdge(localEdgeItem);
					if (!this.m_edgesVisible) {
						localEdgeItem.setVisible(false);
					}
				}
			}
		}
		super.run(paramItemRegistry, paramDouble);
	}
}


/* Location:              /home/vad/work/JAVA/2018.11.30/prefuse-apps.jar!/edu/berkeley/guir/prefuse/action/filter/GraphEdgeFilter.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */