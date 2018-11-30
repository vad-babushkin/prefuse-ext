//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package edu.berkeley.guir.prefusex.layout;

import edu.berkeley.guir.prefuse.ItemRegistry;
import edu.berkeley.guir.prefuse.NodeItem;
import edu.berkeley.guir.prefuse.VisualItem;
import edu.berkeley.guir.prefuse.action.assignment.TreeLayout;
import edu.berkeley.guir.prefuse.graph.Graph;
import edu.berkeley.guir.prefuse.graph.GraphLib;
import edu.berkeley.guir.prefuse.graph.Tree;
import java.awt.geom.Rectangle2D;
import java.util.Iterator;

public class TopDownTreeLayout extends TreeLayout {
	private int ySep = 20;
	private int minXSep = 2;

	public TopDownTreeLayout() {
	}

	public void run(ItemRegistry var1, double var2) {
		Graph var4 = var1.getFilteredGraph();
		if (!(var4 instanceof Tree)) {
			throw new IllegalStateException("This layout only works with a tree!");
		} else {
			Tree var5 = (Tree)var4;
			NodeItem var6 = (NodeItem)var5.getRoot();
			int var7 = GraphLib.getTreeHeight(var5);
			TopDownTreeLayout.TDEdge var8 = new TopDownTreeLayout.TDEdge(var7);
			TopDownTreeLayout.TDEdge var9 = new TopDownTreeLayout.TDEdge(var7);
			this.layout(var6, var7, this.ySep, var8, var9);
			Iterator var10 = var5.getNodes();

			while(var10.hasNext()) {
				NodeItem var11 = (NodeItem)var10.next();
				TopDownTreeLayout.TDParams var12 = this.getParams(var11);
				this.setLocation(var11, (NodeItem)var11.getParent(), (double)var12.x, (double)var12.y);
			}

		}
	}

	private void layout(NodeItem var1, int var2, int var3, TopDownTreeLayout.TDEdge var4, TopDownTreeLayout.TDEdge var5) {
		Rectangle2D var12 = var1.getBounds();
		int var13 = (int)Math.round(var12.getHeight());
		int var14 = (int)Math.round(var12.getWidth());
		int var15 = var1.getChildCount();
		int var7;
		if (var15 == 0) {
			var4.yloc = var3 + var13 - 1;
			var5.yloc = var3 + var13 - 1;
		} else {
			TopDownTreeLayout.TDEdge[] var10 = new TopDownTreeLayout.TDEdge[var15];
			TopDownTreeLayout.TDEdge[] var11 = new TopDownTreeLayout.TDEdge[var15];
			Iterator var16 = var1.getChildren();

			for(var7 = 0; var16.hasNext(); ++var7) {
				var10[var7] = new TopDownTreeLayout.TDEdge(var2);
				var11[var7] = new TopDownTreeLayout.TDEdge(var2);
				this.layout((NodeItem)var16.next(), var2, var3 + var13 + this.ySep, var10[var7], var11[var7]);
			}

			var4 = var10[0];
			var5 = var11[0];
			var16 = var1.getChildren();
			NodeItem var17 = (NodeItem)var16.next();
			TopDownTreeLayout.TDParams var18 = this.getParams(var17);
			var18.x = 0;

			for(var7 = 1; var16.hasNext(); ++var7) {
				var17 = (NodeItem)var16.next();
				var18 = this.getParams(var17);
				int var9 = 0;

				int var8;
				for(var8 = var3 + var13 + this.ySep; var8 <= Math.min(var10[var7].yloc, var5.yloc); ++var8) {
					var9 = Math.max(var9, var10[var7].offset[var8] + var5.offset[var8]);
				}

				var18.x = var9 + this.minXSep;

				for(var8 = var4.yloc + 1; var8 <= var10[var7].yloc; ++var8) {
					var4.offset[var8] = var10[var7].offset[var8] - var18.x;
				}

				var4.yloc = Math.max(var4.yloc, var10[var7].yloc);

				for(var8 = var3; var8 <= var11[var7].yloc; ++var8) {
					var5.offset[var8] = var11[var7].offset[var8] + var18.x;
				}

				var5.yloc = Math.max(var5.yloc, var11[var7].yloc);
			}

			if (var15 > 1) {
				var17 = (NodeItem)var1.getChild(var15 - 1);
				var18 = this.getParams(var17);
				int var6 = var18.x / 2;

				for(var7 = 0; var7 < var15; ++var7) {
					;
				}

				var18.x -= var6;

				for(var7 = var3; var7 <= var4.yloc; ++var7) {
					var4.offset[var7] += var6;
				}

				for(var7 = var3; var7 <= var5.yloc; ++var7) {
					var5.offset[var7] -= var6;
				}
			}

			var11 = null;
			var10 = null;
		}

		for(var7 = var3 - this.ySep; var7 < var3 + var13; ++var7) {
			var4.offset[var7] = var14 / 2;
			var5.offset[var7] = (var14 + 1) / 2;
		}

		TopDownTreeLayout.TDParams var19 = this.getParams(var1);
		var19.y = var3;
	}

	private TopDownTreeLayout.TDParams getParams(VisualItem var1) {
		TopDownTreeLayout.TDParams var2 = (TopDownTreeLayout.TDParams)var1.getVizAttribute("tdParams");
		if (var2 == null) {
			var2 = new TopDownTreeLayout.TDParams();
			var1.setVizAttribute("tdParams", var2);
		}

		return var2;
	}

	public class TDEdge {
		int yloc;
		int[] offset;

		public TDEdge() {
		}

		public TDEdge(int var2) {
			this.offset = new int[var2];
		}
	}

	public class TDParams {
		int x;
		int y;

		public TDParams() {
		}
	}
}
