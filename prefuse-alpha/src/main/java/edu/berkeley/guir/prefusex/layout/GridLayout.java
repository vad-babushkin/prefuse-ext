//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package edu.berkeley.guir.prefusex.layout;

import edu.berkeley.guir.prefuse.EdgeItem;
import edu.berkeley.guir.prefuse.ItemRegistry;
import edu.berkeley.guir.prefuse.NodeItem;
import edu.berkeley.guir.prefuse.VisualItem;
import edu.berkeley.guir.prefuse.action.assignment.Layout;
import edu.berkeley.guir.prefuse.graph.Graph;
import edu.berkeley.guir.prefuse.graph.Node;
import java.awt.geom.Rectangle2D;
import java.util.Iterator;

public class GridLayout extends Layout {
	protected int rows;
	protected int cols;
	protected boolean sorted;
	protected boolean analyze;

	public GridLayout() {
		this.sorted = false;
		this.analyze = false;
		this.analyze = true;
	}

	public GridLayout(int var1, int var2) {
		this(var1, var2, false);
	}

	public GridLayout(int var1, int var2, boolean var3) {
		this.sorted = false;
		this.analyze = false;
		this.rows = var1;
		this.cols = var2;
		var3 = true;
		this.analyze = false;
	}

	public void run(ItemRegistry var1, double var2) {
		Rectangle2D var4 = this.getLayoutBounds(var1);
		double var5 = var4.getMinX();
		double var7 = var4.getMinY();
		double var9 = var4.getWidth();
		double var11 = var4.getHeight();
		Graph var13 = var1.getFilteredGraph();
		int var14 = this.rows;
		int var15 = this.cols;
		if (this.analyze) {
			int[] var16 = analyzeGraphGrid(var13);
			var14 = var16[0];
			var15 = var16[1];
		}

		Iterator var23 = this.sorted ? var1.getNodeItems() : var13.getNodes();

		for(int var17 = 0; var23.hasNext() && var17 < var14 * var15; ++var17) {
			NodeItem var18 = (NodeItem)var23.next();
			var18.setVisible(true);
			this.setEdgeVisibility(var18, true);
			double var19 = var5 + var9 * ((double)(var17 % var15) / (double)(var15 - 1));
			double var21 = var7 + var11 * ((double)(var17 / var15) / (double)(var14 - 1));
			this.setLocation(var18, (VisualItem)null, var19, var21);
		}

		while(var23.hasNext()) {
			NodeItem var24 = (NodeItem)var23.next();
			var24.setVisible(false);
			this.setEdgeVisibility(var24, false);
		}

	}

	private void setEdgeVisibility(NodeItem var1, boolean var2) {
		Iterator var3 = var1.getEdges();

		while(var3.hasNext()) {
			EdgeItem var4 = (EdgeItem)var3.next();
			var4.setVisible(var2);
		}

	}

	public static int[] analyzeGraphGrid(Graph var0) {
		Iterator var3 = var0.getNodes();
		var3.next();

		int var2;
		for(var2 = 2; var3.hasNext(); ++var2) {
			Node var4 = (Node)var3.next();
			if (var4.getEdgeCount() == 2) {
				break;
			}
		}

		int var1 = var0.getNodeCount() / var2;
		return new int[]{var1, var2};
	}

	public int getNumCols() {
		return this.cols;
	}

	public void setNumCols(int var1) {
		this.cols = var1;
	}

	public int getNumRows() {
		return this.rows;
	}

	public void setNumRows(int var1) {
		this.rows = var1;
	}

	public boolean isSorted() {
		return this.sorted;
	}

	public void setSorted(boolean var1) {
		this.sorted = var1;
	}
}
