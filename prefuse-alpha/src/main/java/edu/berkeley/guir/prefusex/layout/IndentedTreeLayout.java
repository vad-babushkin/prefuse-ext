//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package edu.berkeley.guir.prefusex.layout;

import edu.berkeley.guir.prefuse.AggregateItem;
import edu.berkeley.guir.prefuse.ItemRegistry;
import edu.berkeley.guir.prefuse.NodeItem;
import edu.berkeley.guir.prefuse.VisualItem;
import edu.berkeley.guir.prefuse.action.assignment.TreeLayout;
import edu.berkeley.guir.prefuse.collections.DOIItemComparator;
import edu.berkeley.guir.prefuse.graph.TreeNode;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Point2D.Double;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class IndentedTreeLayout extends TreeLayout {
	public static final String ATTR_EXPANDED = "expanded";
	private ItemRegistry m_registry;
	private List m_entryList = new ArrayList();
	private int m_verticalInc = 15;
	private int m_indent = 16;
	private boolean m_elide = false;
	private List m_tlist = new LinkedList();
	private Comparator m_comp = new Comparator() {
		Comparator comp = new DOIItemComparator();

		public int compare(Object var1, Object var2) {
			NodeItem var3 = ((IndentedTreeLayout.LayoutEntry)var1).nodeItem;
			NodeItem var4 = ((IndentedTreeLayout.LayoutEntry)var2).nodeItem;
			return this.comp.compare(var3, var4);
		}
	};
	private AggregateItem m_tmpAggr = null;

	public IndentedTreeLayout() {
	}

	public int getIndent() {
		return this.m_indent;
	}

	public void setIndent(int var1) {
		this.m_indent = var1;
	}

	public boolean isEliding() {
		return this.m_elide;
	}

	public void setEliding(boolean var1) {
		this.m_elide = var1;
	}

	public Point2D getLayoutAnchor(ItemRegistry var1) {
		Point2D var2 = super.getLayoutAnchor();
		if (var2 != null) {
			return var2;
		} else {
			Rectangle2D var3 = this.getLayoutBounds(var1);
			double var4 = 0.0D;
			double var6 = 0.0D;
			if (var3 != null) {
				var4 = var3.getX();
				var6 = var3.getY();
			}

			return new Double(var4, var6);
		}
	}

	public void run(ItemRegistry var1, double var2) {
		this.m_registry = var1;
		this.m_tmpAggr = null;
		NodeItem var4 = this.getLayoutRoot(var1);
		if (var4 != null && var4.isVisible()) {
			Rectangle2D var5 = this.getLayoutBounds(var1);
			Point2D var6 = this.getLayoutAnchor(var1);
			int var7 = (int)Math.ceil(var5.getHeight() - var6.getY());
			int var8 = this.calcTreeHeight(this.m_entryList, var4, 0, 0);
			this.updateStartLocations(this.m_entryList);
			if (this.m_elide && var8 > var7) {
				this.elide(var8, var7);
			}

			this.layout(this.m_entryList, (int)Math.ceil(var6.getY() + var4.getBounds().getHeight() / 2.0D), (int)Math.ceil(var6.getX()));
		} else {
			System.err.println("IndentedTreeLayout: Tree root not visible!");
		}

		this.m_entryList.clear();
	}

	protected int calcTreeHeight(List var1, NodeItem var2, int var3, int var4) {
		if (var2 != null && var2.isVisible()) {
			IndentedTreeLayout.LayoutEntry var5 = new IndentedTreeLayout.LayoutEntry(var2, var4);
			var5.index = var1.size();
			var1.add(var5);
			var3 = (int)((double)var3 + var2.getBounds().getHeight());
			NodeItem var7;
			if (this.isExpanded(var2)) {
				for(Iterator var6 = var2.getChildren(); var6.hasNext(); var3 = this.calcTreeHeight(var1, var7, var3, var4 + 1)) {
					var7 = (NodeItem)var6.next();
				}
			}
		}

		return var3;
	}

	protected void elide(int var1, int var2) {
		ArrayList var3 = new ArrayList(this.m_entryList);
		boolean[] var4 = new boolean[var3.size()];
		Collections.sort(var3, this.m_comp);
		Iterator var5 = var3.iterator();

		int var8;
		while(var5.hasNext() && var1 > var2) {
			IndentedTreeLayout.LayoutEntry var6 = (IndentedTreeLayout.LayoutEntry)var5.next();
			NodeItem var7 = var6.nodeItem;
			int var9 = var6.index;
			var4[var9] = true;
			if ((var8 = this.elisionRun(var4, var9)) > 0) {
				for(int var10 = 0; var10 < var8; ++var10) {
					NodeItem var11 = ((IndentedTreeLayout.LayoutEntry)this.m_entryList.get(var9 + var10)).nodeItem;
					var1 = (int)((double)var1 - var11.getBounds().getHeight());
				}
			}
		}

		AggregateItem var12 = null;
		int var13 = 0;

		for(var8 = 0; var13 < var4.length; ++var13) {
			if ((var12 == null || !var4[var13]) && (var13 >= var4.length - 1 || !var4[var13] || !var4[var13 + 1])) {
				if (var12 != null && !var4[var13]) {
					var12 = null;
					var8 = 0;
				}
			} else {
				IndentedTreeLayout.LayoutEntry var14 = (IndentedTreeLayout.LayoutEntry)this.m_entryList.get(var13);
				NodeItem var15 = var14.nodeItem;
				TreeNode var16 = (TreeNode)var15.getEntity();
				if (var12 == null) {
					var12 = this.m_registry.getAggregateItem(var16, false);
					if (var12 != null) {
						this.m_registry.removeMappings(var12);
					}

					var12 = this.m_registry.getAggregateItem(var16, true);
					this.copyAttributes(var15, var12);
				} else {
					this.m_registry.addMapping(var16, var12);
				}

				++var8;
				var12.setAggregateSize(var8);
				var15.setVisible(false);
				var14.elided = true;
				var14.aggrItem = var12;
			}
		}

	}

	private int elisionRun(boolean[] var1, int var2) {
		int var3 = var1.length;
		if (var2 == 0) {
			return var3 > 1 && var1[1] ? 1 : 0;
		} else if (var2 == var3 - 1) {
			return var2 > 0 && var1[var2 - 1] ? 1 : 0;
		} else if (var3 >= 2 && var1[var2 - 1] && var1[var2 + 1]) {
			return 2;
		} else {
			return (var2 <= 0 || !var1[var2 - 1]) && (var2 >= var3 - 1 || !var1[var2 + 1]) ? 0 : 1;
		}
	}

	private void copyAttributes(VisualItem var1, VisualItem var2) {
		var2.setLocation(var1.getLocation());
		var2.setEndLocation(var1.getEndLocation());
		var2.setSize(var1.getSize());
		var2.setEndSize(var1.getEndSize());
	}

	protected void updateStartLocations(List var1) {
		for(int var2 = 0; var2 < var1.size(); ++var2) {
			IndentedTreeLayout.LayoutEntry var3 = (IndentedTreeLayout.LayoutEntry)var1.get(var2);
			NodeItem var4 = var3.nodeItem;
			if (var4.isNewlyVisible()) {
				TreeNode var5 = (TreeNode)var4.getEntity();
				AggregateItem var6 = this.m_registry.getAggregateItem(var5);
				if (var6 != null && var6.isVisible()) {
					var4.setLocation(var6.getEndLocation());
				} else {
					TreeNode var7 = var5.getParent();
					if (var7 != null) {
						NodeItem var8 = this.m_registry.getNodeItem(var7);
						var4.setLocation(var8.getEndLocation());
					}
				}
			}
		}

	}

	protected int layout(List var1, int var2, int var3) {
		NodeItem var4 = null;

		for(int var5 = 0; var5 < var1.size(); ++var5) {
			IndentedTreeLayout.LayoutEntry var6 = (IndentedTreeLayout.LayoutEntry)var1.get(var5);
			if (!var6.hidden) {
				NodeItem var7;
				if (var6.elided) {
					var7 = var6.aggrItem;
					if (var7 == var4) {
						continue;
					}

					var4 = var7;
				} else {
					var7 = var6.nodeItem;
				}

				this.setLocation(var7, (NodeItem)var7.getParent(), (double)(var6.depth * this.m_indent + var2), (double)var3);
				var3 = (int)((double)var3 + var7.getBounds().getHeight());
			}
		}

		return var3;
	}

	protected void setLocation(VisualItem var1, VisualItem var2, double var3, double var5) {
		super.setLocation(var1, var2, var3, var5);
		List var7 = null;
		if (var1 instanceof AggregateItem) {
			var7 = ((AggregateItem)var1).getEntities();
		}

		if (var7 != null) {
			Iterator var8 = var7.iterator();

			while(var8.hasNext()) {
				NodeItem var9 = this.m_registry.getNodeItem((TreeNode)var8.next());
				super.setLocation(var9, var1, var3, var5);
			}
		}

	}

	private boolean isExpanded(VisualItem var1) {
		Boolean var2 = (Boolean)var1.getVizAttribute("expanded");
		return var2 == null ? false : var2;
	}

	private class LayoutEntry {
		NodeItem nodeItem;
		NodeItem aggrItem;
		boolean elided;
		boolean hidden;
		int index;
		int depth;

		public LayoutEntry(NodeItem var2, int var3) {
			this.nodeItem = var2;
			this.aggrItem = null;
			this.elided = false;
			this.hidden = false;
			this.index = -1;
			this.depth = var3;
		}
	}
}
