package edu.berkeley.guir.prefuse.action.filter;

import edu.berkeley.guir.prefuse.EdgeItem;
import edu.berkeley.guir.prefuse.ItemRegistry;
import edu.berkeley.guir.prefuse.NodeItem;
import edu.berkeley.guir.prefuse.graph.DefaultGraph;
import edu.berkeley.guir.prefuse.graph.Edge;
import edu.berkeley.guir.prefuse.graph.Graph;
import edu.berkeley.guir.prefuse.graph.Node;

import java.util.Iterator;

public class GraphFilter
		extends Filter {
	public static final String[] ITEM_CLASSES = {"node", "edge"};
	protected boolean m_edgesVisible;

	public GraphFilter() {
		this(true, true);
	}

	public GraphFilter(boolean paramBoolean) {
		this(paramBoolean, true);
	}

	public GraphFilter(boolean paramBoolean1, boolean paramBoolean2) {
		super(ITEM_CLASSES, paramBoolean2);
		this.m_edgesVisible = paramBoolean1;
	}

	public void run(ItemRegistry paramItemRegistry, double paramDouble) {
		Graph localGraph = paramItemRegistry.getGraph();
		Object localObject = paramItemRegistry.getFilteredGraph();
		if ((localObject instanceof DefaultGraph)) {
			((DefaultGraph) localObject).reinit(localGraph.isDirected());
		} else {
			localObject = new DefaultGraph(localGraph.isDirected());
		}
		Iterator localIterator1 = localGraph.getNodes();
		NodeItem localNodeItem;
		while (localIterator1.hasNext()) {
			localNodeItem = paramItemRegistry.getNodeItem((Node) localIterator1.next(), true);
			((Graph) localObject).addNode(localNodeItem);
		}
		localIterator1 = ((Graph) localObject).getNodes();
		while (localIterator1.hasNext()) {
			localNodeItem = (NodeItem) localIterator1.next();
			Node localNode1 = (Node) localNodeItem.getEntity();
			Iterator localIterator2 = localNode1.getEdges();
			while (localIterator2.hasNext()) {
				Edge localEdge = (Edge) localIterator2.next();
				Node localNode2 = localEdge.getAdjacentNode(localNode1);
				EdgeItem localEdgeItem = paramItemRegistry.getEdgeItem(localEdge, true);
				((Graph) localObject).addEdge(localEdgeItem);
				if (!this.m_edgesVisible) {
					localEdgeItem.setVisible(false);
				}
			}
		}
		paramItemRegistry.setFilteredGraph((Graph) localObject);
		super.run(paramItemRegistry, paramDouble);
	}
}


/* Location:              /home/vad/work/JAVA/2018.11.30/prefuse-apps.jar!/edu/berkeley/guir/prefuse/action/filter/GraphFilter.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */