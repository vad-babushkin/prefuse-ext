package edu.berkeley.guir.prefuse.action.filter;

import edu.berkeley.guir.prefuse.EdgeItem;
import edu.berkeley.guir.prefuse.ItemRegistry;
import edu.berkeley.guir.prefuse.NodeItem;
import edu.berkeley.guir.prefuse.graph.*;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class WindowedTreeFilter
		extends Filter {
	public static final String[] ITEM_CLASSES = {"node", "edge"};
	public static final int DEFAULT_MIN_DOI = -2;
	private boolean m_edgesVisible;
	private boolean m_useFocusAsRoot;
	private int m_minDOI;
	private Node m_root;
	private List m_queue = new LinkedList();

	public WindowedTreeFilter() {
		this(-2);
	}

	public WindowedTreeFilter(int paramInt) {
		this(paramInt, false);
	}

	public WindowedTreeFilter(int paramInt, boolean paramBoolean) {
		this(paramInt, paramBoolean, true);
	}

	public WindowedTreeFilter(int paramInt, boolean paramBoolean1, boolean paramBoolean2) {
		this(paramInt, paramBoolean1, paramBoolean2, true);
	}

	public WindowedTreeFilter(int paramInt, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3) {
		super(ITEM_CLASSES, paramBoolean3);
		this.m_minDOI = paramInt;
		this.m_useFocusAsRoot = paramBoolean1;
		this.m_edgesVisible = paramBoolean2;
	}

	public void setTreeRoot(Node paramNode) {
		this.m_root = paramNode;
	}

	public void run(ItemRegistry paramItemRegistry, double paramDouble) {
		Graph localGraph = paramItemRegistry.getGraph();
		boolean bool = localGraph instanceof Tree;
		Object localObject1 = paramItemRegistry.getFilteredGraph();
		DefaultTree localDefaultTree = null;
		if ((bool) && ((localObject1 instanceof DefaultTree))) {
			localDefaultTree = (DefaultTree) localObject1;
			localDefaultTree.setRoot(null);
		} else {
			localObject1 = localDefaultTree = new DefaultTree();
		}
		Object localObject2 = null;
		Iterator localIterator1 = paramItemRegistry.getDefaultFocusSet().iterator();
		NodeItem localNodeItem1 = null;
		if (localIterator1.hasNext()) {
			localNodeItem1 = paramItemRegistry.getNodeItem((Node) localIterator1.next(), true);
		}
		Object localObject3;
		if (this.m_root != null) {
			localObject3 = (this.m_root instanceof NodeItem) ? this.m_root : paramItemRegistry.getNodeItem(this.m_root, true);
			localObject2 = (NodeItem) localObject3;
		} else if ((localNodeItem1 != null) && (this.m_useFocusAsRoot)) {
			localObject2 = localNodeItem1;
		} else if (bool) {
			localObject2 = paramItemRegistry.getNodeItem(((Tree) localGraph).getRoot(), true);
		} else {
			localObject3 = localGraph.getNodes();
			if (((Iterator) localObject3).hasNext()) {
				localObject2 = paramItemRegistry.getNodeItem((Node) ((Iterator) localObject3).next(), true);
			}
		}
		if (localObject2 == null) {
			throw new IllegalStateException("No root for the filtered tree has been specified.");
		}
		localDefaultTree.setRoot((TreeNode) localObject2);
		((NodeItem) localObject2).setDOI(0.0D);
		this.m_queue.add(localObject2);
		while (!this.m_queue.isEmpty()) {
			localObject3 = (NodeItem) this.m_queue.remove(0);
			Node localNode1 = (Node) ((NodeItem) localObject3).getEntity();
			double d = ((NodeItem) localObject3).getDOI() - 1.0D;
			if (d >= this.m_minDOI) {
				Iterator localIterator2 = localNode1.getEdges();
				int i = 0;
				while (localIterator2.hasNext()) {
					Edge localEdge = (Edge) localIterator2.next();
					Node localNode2 = localEdge.getAdjacentNode(localNode1);
					NodeItem localNodeItem2 = paramItemRegistry.getNodeItem(localNode2);
					int j = (localNodeItem2 == null) || (localNodeItem2.getDirty() > 0) ? 1 : 0;
					if (j != 0) {
						localNodeItem2 = paramItemRegistry.getNodeItem(localNode2, true);
					}
					EdgeItem localEdgeItem = paramItemRegistry.getEdgeItem(localEdge, true);
					if (j != 0) {
						((NodeItem) localObject3).addChild(localEdgeItem);
						localNodeItem2.setDOI(d);
						this.m_queue.add(localNodeItem2);
					} else {
						localEdgeItem.getFirstNode().addEdge(localEdgeItem);
						localEdgeItem.getSecondNode().addEdge(localEdgeItem);
					}
				}
			}
		}
		paramItemRegistry.setFilteredGraph(localDefaultTree);
		super.run(paramItemRegistry, paramDouble);
	}

	public boolean isEdgesVisible() {
		return this.m_edgesVisible;
	}

	public void setEdgesVisible(boolean paramBoolean) {
		this.m_edgesVisible = paramBoolean;
	}

	public int getMinDOI() {
		return this.m_minDOI;
	}

	public void setMinDOI(int paramInt) {
		this.m_minDOI = paramInt;
	}

	public boolean isUseFocusAsRoot() {
		return this.m_useFocusAsRoot;
	}

	public void setUseFocusAsRoot(boolean paramBoolean) {
		this.m_useFocusAsRoot = paramBoolean;
	}
}


/* Location:              /home/vad/work/JAVA/2018.11.30/prefuse-apps.jar!/edu/berkeley/guir/prefuse/action/filter/WindowedTreeFilter.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */