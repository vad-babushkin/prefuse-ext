//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package edu.berkeley.guir.prefuse.action.filter;

import edu.berkeley.guir.prefuse.EdgeItem;
import edu.berkeley.guir.prefuse.ItemRegistry;
import edu.berkeley.guir.prefuse.NodeItem;
import edu.berkeley.guir.prefuse.graph.DefaultTree;
import edu.berkeley.guir.prefuse.graph.Edge;
import edu.berkeley.guir.prefuse.graph.Graph;
import edu.berkeley.guir.prefuse.graph.GraphLib;
import edu.berkeley.guir.prefuse.graph.Node;
import edu.berkeley.guir.prefuse.graph.Tree;
import edu.berkeley.guir.prefuse.graph.TreeNode;
import java.util.Iterator;

public class TreeFilter extends Filter {
	public static final String[] ITEM_CLASSES = new String[]{"node", "edge"};
	private boolean m_edgesVisible;
	private boolean m_useFocusAsRoot;
	private Node m_root;

	public TreeFilter() {
		this(false, true, true);
	}

	public TreeFilter(boolean var1) {
		this(var1, true, true);
	}

	public TreeFilter(boolean var1, boolean var2) {
		this(var1, var2, true);
	}

	public TreeFilter(boolean var1, boolean var2, boolean var3) {
		super(ITEM_CLASSES, var3);
		this.m_edgesVisible = var2;
		this.m_useFocusAsRoot = var1;
	}

	public void setTreeRoot(Node var1) {
		this.m_root = var1;
	}

	public void run(ItemRegistry var1, double var2) {
		Graph var4 = var1.getGraph();
		boolean var5 = var4 instanceof Tree;
		Graph var6 = var1.getFilteredGraph();
		Object var7 = null;
		if (var5 && var6 instanceof DefaultTree) {
			var7 = (DefaultTree)var6;
			((Tree)var7).setRoot((TreeNode)null);
		} else if (var5) {
			var7 = new DefaultTree();
		}

		NodeItem var8 = null;
		Iterator var9 = var4.getNodes();

		while(var9.hasNext()) {
			NodeItem var10 = var1.getNodeItem((Node)var9.next(), true);
			if (var8 == null) {
				var8 = var10;
			}
		}

		Iterator var20 = var1.getDefaultFocusSet().iterator();
		NodeItem var11 = null;
		if (var20.hasNext()) {
			var11 = var1.getNodeItem((Node)var20.next(), true);
		}

		if (this.m_root != null) {
			Object var12 = this.m_root instanceof NodeItem ? this.m_root : var1.getNodeItem(this.m_root);
			if (var12 != var8) {
				var7 = null;
			}

			var8 = (NodeItem)var12;
		} else if (var11 != null && this.m_useFocusAsRoot) {
			var8 = var11;
		} else if (var5) {
			var8 = var1.getNodeItem(((Tree)var4).getRoot());
			if (var7 != null) {
				((Tree)var7).setRoot(var8);
			}
		}

		var9 = var1.getNodeItems();

		while(var9.hasNext()) {
			NodeItem var21 = (NodeItem)var9.next();
			Node var13 = (Node)var21.getEntity();
			Iterator var14 = var13.getEdges();

			while(var14.hasNext()) {
				Edge var15 = (Edge)var14.next();
				Node var16 = var15.getAdjacentNode(var13);
				EdgeItem var17 = var1.getEdgeItem(var15, true);
				if (var15.isTreeEdge()) {
					TreeNode var18 = (TreeNode)var17.getAdjacentNode(var21);
					Object var19 = ((TreeNode)var16).getParent() == var13 ? var21 : var18;
					((TreeNode)var19).addChild(var17);
				} else {
					var17.getFirstNode().addEdge(var17);
					var17.getSecondNode().addEdge(var17);
				}

				if (!this.m_edgesVisible) {
					var17.setVisible(false);
				}
			}
		}

		if (var7 == null) {
			var7 = GraphLib.breadthFirstTree(var8);
		}

		var1.setFilteredGraph((Graph)var7);
		super.run(var1, var2);
	}

	public boolean isEdgesVisible() {
		return this.m_edgesVisible;
	}

	public void setEdgesVisible(boolean var1) {
		this.m_edgesVisible = var1;
	}

	public boolean isUseFocusAsRoot() {
		return this.m_useFocusAsRoot;
	}

	public void setUseFocusAsRoot(boolean var1) {
		this.m_useFocusAsRoot = var1;
	}
}
