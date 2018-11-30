//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package edu.berkeley.guir.prefuse.action.filter;

import edu.berkeley.guir.prefuse.EdgeItem;
import edu.berkeley.guir.prefuse.ItemRegistry;
import edu.berkeley.guir.prefuse.NodeItem;
import edu.berkeley.guir.prefuse.collections.SingleElementIterator;
import edu.berkeley.guir.prefuse.graph.DefaultTree;
import edu.berkeley.guir.prefuse.graph.Edge;
import edu.berkeley.guir.prefuse.graph.Graph;
import edu.berkeley.guir.prefuse.graph.Node;
import edu.berkeley.guir.prefuse.graph.Tree;
import edu.berkeley.guir.prefuse.graph.TreeNode;
import java.util.Iterator;

public class FisheyeTreeFilter extends Filter {
	public static final String[] ITEM_CLASSES = new String[]{"node", "edge"};
	public static final int DEFAULT_MIN_DOI = -2;
	public static final String ATTR_CENTER = "center";
	private int m_minDOI;
	private boolean m_edgesVisible;
	private Node m_froot;
	private ItemRegistry m_registry;
	private double m_localDOIDivisor;
	private TreeNode m_root;

	public FisheyeTreeFilter() {
		this(-2);
	}

	public FisheyeTreeFilter(int var1) {
		this(var1, true);
	}

	public FisheyeTreeFilter(int var1, boolean var2) {
		this(var1, var2, true);
	}

	public FisheyeTreeFilter(int var1, boolean var2, boolean var3) {
		super(ITEM_CLASSES, var3);
		this.m_edgesVisible = true;
		this.m_edgesVisible = var2;
		this.m_minDOI = var1;
	}

	public void setTreeRoot(Node var1) {
		this.m_froot = var1;
	}

	protected Iterator getFoci(ItemRegistry var1) {
		Object var2 = var1.getDefaultFocusSet().iterator();
		if (!((Iterator)var2).hasNext()) {
			var2 = new SingleElementIterator(this.m_root);
		}

		return (Iterator)var2;
	}

	public void run(ItemRegistry var1, double var2) {
		this.m_registry = var1;
		Graph var4 = var1.getGraph();
		if (!(var4 instanceof Tree)) {
			throw new IllegalStateException("The FisheyeTreeFilter requires that the backing graph returned by registry.getGraph() is a Tree instance.");
		} else {
			Tree var5 = (Tree)var4;
			this.m_localDOIDivisor = (double)var5.getNodeCount();
			this.m_root = var5.getRoot();
			Graph var6 = var1.getFilteredGraph();
			DefaultTree var7 = null;
			if (var6 instanceof DefaultTree) {
				var7 = (DefaultTree)var6;
				var7.setRoot((TreeNode)null);
			} else {
				var7 = new DefaultTree();
			}

			Iterator var8 = this.getFoci(var1);

			while(true) {
				Object var9;
				do {
					if (!var8.hasNext()) {
						var7.setRoot(var1.getNodeItem(this.m_root));
						Iterator var18 = var1.getNodeItems();

						while(var18.hasNext()) {
							NodeItem var19 = (NodeItem)var18.next();
							Node var20 = (Node)var19.getEntity();
							Iterator var21 = var20.getEdges();

							while(var21.hasNext()) {
								Edge var13 = (Edge)var21.next();
								Node var14 = var13.getAdjacentNode(var20);
								if (var1.isVisible(var14)) {
									EdgeItem var15 = var1.getEdgeItem(var13, true);
									if (var13.isTreeEdge()) {
										TreeNode var16 = (TreeNode)var15.getAdjacentNode(var19);
										Object var17 = var16.getParent() == var19 ? var19 : var16;
										((TreeNode)var17).addChild(var15);
									} else {
										var15.getFirstNode().addEdge(var15);
										var15.getSecondNode().addEdge(var15);
									}

									if (!this.m_edgesVisible) {
										var15.setVisible(false);
									}
								}
							}
						}

						this.m_registry = null;
						this.m_root = null;
						var1.setFilteredGraph(var7);
						super.run(var1, var2);
						return;
					}

					var9 = var8.next();
				} while(!(var9 instanceof TreeNode));

				TreeNode var10 = (TreeNode)var9;
				NodeItem var11 = var1.getNodeItem(var10);
				boolean var12 = false;
				var12 = var11 == null || var11.getDirty() > 0 || var11.getDOI() < 0.0D;
				var11 = var1.getNodeItem(var10, true);
				if (var12) {
					this.setDOI(var11, 0, 0);
					if ((int)var11.getDOI() > this.m_minDOI) {
						this.visitDescendants(var10, var11, (TreeNode)null);
					}

					this.visitAncestors(var10, var11);
				}
			}
		}
	}

	protected void visitDescendants(TreeNode var1, NodeItem var2, TreeNode var3) {
		int var4 = var3 == null ? this.getCenter(var2) : var1.getChildIndex(var3);
		Iterator var5 = var1.getChildren();
		int var6 = 0;

		while(var5.hasNext()) {
			TreeNode var7 = (TreeNode)var5.next();
			if (var7 != var3) {
				NodeItem var8 = this.m_registry.getNodeItem(var7, true);
				this.setDOI(var8, (int)var2.getDOI() - 1, Math.abs(var4 - var6));
				if ((int)var8.getDOI() > this.m_minDOI) {
					this.visitDescendants(var7, var8, (TreeNode)null);
				}

				++var6;
			}
		}

	}

	protected void visitAncestors(TreeNode var1, NodeItem var2) {
		if (var1.getParent() != null && var1 != this.m_root) {
			TreeNode var3 = var1.getParent();
			NodeItem var4 = this.m_registry.getNodeItem(var3);
			boolean var5 = false;
			var5 = var4 == null || var4.getDirty() > 0 || var4.getDOI() < 0.0D;
			var4 = this.m_registry.getNodeItem(var3, true);
			if (var5) {
				this.setDOI(var4, 0, 0);
				if ((int)var4.getDOI() > this.m_minDOI) {
					this.visitDescendants(var3, var4, var1);
				}

				this.visitAncestors(var3, var4);
			}

		}
	}

	protected void setDOI(NodeItem var1, int var2, int var3) {
		double var4 = (double)(-var3) / Math.min(1000.0D, this.m_localDOIDivisor);
		var1.setDOI((double)var2 + var4);
	}

	private int getCenter(NodeItem var1) {
		TreeNode var2 = (TreeNode)var1.getVizAttribute("center");
		if (var2 != null) {
			TreeNode var3 = (TreeNode)var1.getEntity();
			int var4 = var3.getChildIndex(var2);
			if (var4 > -1) {
				return var4;
			}
		}

		return 0;
	}
}
