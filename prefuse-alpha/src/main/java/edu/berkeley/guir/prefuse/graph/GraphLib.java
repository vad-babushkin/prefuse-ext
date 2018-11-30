//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package edu.berkeley.guir.prefuse.graph;

import edu.berkeley.guir.prefuse.collections.BreadthFirstGraphIterator;
import edu.berkeley.guir.prefuse.collections.EdgeNodeComparator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;

public class GraphLib {
	public GraphLib() {
	}

	public static Node[] search(Graph var0, String var1, String var2) {
		ArrayList var3 = new ArrayList();
		Iterator var4 = var0.getNodes();

		while(var4.hasNext()) {
			Node var5 = (Node)var4.next();
			String var6 = var5.getAttribute(var1);
			if (var6 != null && var6.equals(var2)) {
				var3.add(var5);
			}
		}

		return (Node[])var3.toArray(new Node[var3.size()]);
	}

	public static Node[] getMostConnectedNodes(Graph var0) {
		ArrayList var1 = new ArrayList();
		int var2 = -1;
		Iterator var4 = var0.getNodes();

		while(var4.hasNext()) {
			Node var5 = (Node)var4.next();
			int var3;
			if ((var3 = var5.getEdgeCount()) > var2) {
				var2 = var3;
				var1.clear();
				var1.add(var5);
			} else if (var3 == var2) {
				var1.add(var5);
			}
		}

		return (Node[])var1.toArray(new Node[var1.size()]);
	}

	public static Tree breadthFirstTree(TreeNode var0) {
		if (var0 == null) {
			return null;
		} else {
			BreadthFirstGraphIterator var1 = new BreadthFirstGraphIterator(var0);

			while(var1.hasNext()) {
				TreeNode var2 = (TreeNode)var1.next();
				var2.removeAllAsChildren();
			}

			HashSet var7 = new HashSet();
			LinkedList var3 = new LinkedList();
			var3.add(var0);
			var7.add(var0);
			var0.setParentEdge((Edge)null);

			while(!var3.isEmpty()) {
				TreeNode var4 = (TreeNode)var3.removeFirst();
				Iterator var6 = var4.getNeighbors();

				while(var6.hasNext()) {
					TreeNode var5 = (TreeNode)var6.next();
					if (!var7.contains(var5)) {
						var4.setAsChild(var5);
						var3.add(var5);
						var7.add(var5);
					}
				}
			}

			return new DefaultTree(var0);
		}
	}

	public static void sortTree(Tree var0, Comparator var1) {
		TreeNode var2 = var0.getRoot();
		sortHelper(var2, new EdgeNodeComparator(var1));
	}

	private static void sortHelper(TreeNode var0, EdgeNodeComparator var1) {
		ArrayList var2 = new ArrayList();
		Iterator var3 = var0.getChildren();

		while(var3.hasNext()) {
			var2.add(var3.next());
		}

		var1.setIgnoredNode(var0);
		Collections.sort(var2, var1);
		var0.removeAllChildren();
		var3 = var2.iterator();

		while(var3.hasNext()) {
			Edge var4 = (Edge)var3.next();
			var0.addChild(var4);
			sortHelper((TreeNode)var4.getAdjacentNode(var0), var1);
		}

	}

	public static int nearestIndex(TreeNode var0, TreeNode var1) {
		int var2 = 0;

		for(int var3 = 0; var3 < var0.getEdgeCount(); ++var3) {
			TreeNode var4 = (TreeNode)var0.getNeighbor(var3);
			if (var4 == var1) {
				return var2;
			}

			if (var4.getParent() == var0) {
				++var2;
			}
		}

		return var0.getChildCount();
	}

	public static int getTreeHeight(Tree var0) {
		TreeNode var1 = var0.getRoot();
		return getTreeHeightHelper(var1, 0);
	}

	private static int getTreeHeightHelper(TreeNode var0, int var1) {
		int var2 = var1;
		TreeNode var4;
		if (var0.getChildCount() > 0) {
			for(Iterator var3 = var0.getChildren(); var3.hasNext(); var2 = Math.max(var2, getTreeHeightHelper(var4, var1 + 1))) {
				var4 = (TreeNode)var3.next();
			}
		}

		return var2;
	}

	public static Graph getNodes(int var0) {
		DefaultGraph var1 = new DefaultGraph();

		for(int var2 = 0; var2 < var0; ++var2) {
			DefaultTreeNode var3 = new DefaultTreeNode();
			var3.setAttribute("label", String.valueOf(var2));
			var1.addNode(var3);
		}

		return var1;
	}

	public static Graph getStar(int var0) {
		DefaultGraph var1 = new DefaultGraph();
		DefaultTreeNode var2 = new DefaultTreeNode();
		var2.setAttribute("label", "0");
		var1.addNode(var2);

		for(int var3 = 1; var3 <= var0; ++var3) {
			DefaultTreeNode var4 = new DefaultTreeNode();
			var4.setAttribute("label", String.valueOf(var3));
			var1.addNode(var4);
			DefaultEdge var5 = new DefaultEdge(var4, var2);
			var1.addEdge(var5);
		}

		return var1;
	}

	public static Graph getClique(int var0) {
		DefaultGraph var1 = new DefaultGraph();
		Node[] var2 = new Node[var0];

		int var3;
		for(var3 = 0; var3 < var0; ++var3) {
			var2[var3] = new DefaultTreeNode();
			var2[var3].setAttribute("label", String.valueOf(var3));
			var1.addNode(var2[var3]);
		}

		for(var3 = 0; var3 < var0; ++var3) {
			for(int var4 = var3; var4 < var0; ++var4) {
				if (var3 != var4) {
					var1.addEdge(new DefaultEdge(var2[var3], var2[var4]));
				}
			}
		}

		return var1;
	}

	public static Graph getGrid(int var0, int var1) {
		DefaultGraph var2 = new DefaultGraph();
		Node[] var3 = new Node[var0 * var1];

		for(int var4 = 0; var4 < var0 * var1; ++var4) {
			var3[var4] = new DefaultTreeNode();
			var3[var4].setAttribute("label", String.valueOf(var4));
			var2.addNode(var3[var4]);
			if (var4 >= var1) {
				var2.addEdge(new DefaultEdge(var3[var4 - var1], var3[var4]));
			}

			if (var4 % var1 != 0) {
				var2.addEdge(new DefaultEdge(var3[var4 - 1], var3[var4]));
			}
		}

		return var2;
	}
}
