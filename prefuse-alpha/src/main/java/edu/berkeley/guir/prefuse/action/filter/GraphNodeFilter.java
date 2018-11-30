package edu.berkeley.guir.prefuse.action.filter;

import edu.berkeley.guir.prefuse.ItemRegistry;
import edu.berkeley.guir.prefuse.NodeItem;
import edu.berkeley.guir.prefuse.graph.Node;

import java.util.Iterator;

public class GraphNodeFilter
		extends Filter {
	public GraphNodeFilter() {
		super("node", true);
	}

	public void run(ItemRegistry paramItemRegistry, double paramDouble) {
		Iterator localIterator = paramItemRegistry.getGraph().getNodes();
		while (localIterator.hasNext()) {
			NodeItem localNodeItem = paramItemRegistry.getNodeItem((Node) localIterator.next(), true);
		}
		super.run(paramItemRegistry, paramDouble);
	}
}


/* Location:              /home/vad/work/JAVA/2018.11.30/prefuse-apps.jar!/edu/berkeley/guir/prefuse/action/filter/GraphNodeFilter.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */