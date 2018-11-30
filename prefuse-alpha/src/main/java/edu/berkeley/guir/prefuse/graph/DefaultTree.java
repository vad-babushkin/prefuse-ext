//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package edu.berkeley.guir.prefuse.graph;

import edu.berkeley.guir.prefuse.collections.BreadthFirstTreeIterator;
import edu.berkeley.guir.prefuse.collections.TreeEdgeIterator;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;

public class DefaultTree extends AbstractGraph implements Tree {
	protected TreeNode m_root;

	public DefaultTree(TreeNode var1) {
		this.m_root = var1;
	}

	public DefaultTree() {
		this.m_root = null;
	}

	public boolean isDirected() {
		return false;
	}

	public void changeRoot(TreeNode var1) {
		if (!this.contains((Node)var1)) {
			throw new IllegalArgumentException("The new root must already be in the tree");
		} else {
			LinkedList var3 = new LinkedList();

			for(TreeNode var4 = var1; var4 != null; var4 = var4.getParent()) {
				var3.addFirst(var4);
				if (var4 == this.m_root) {
					break;
				}
			}

			Iterator var9 = var3.iterator();

			TreeNode var6;
			for(TreeNode var5 = (TreeNode)var9.next(); var9.hasNext(); var5 = var6) {
				var6 = (TreeNode)var9.next();
				int var7 = var5.getChildIndex(var6);
				Edge var8 = var5.getChildEdge(var7);
				var5.removeAsChild(var7);
				var5.setParentEdge(var8);
				var6.setAsChild(GraphLib.nearestIndex(var6, var5), var5);
			}

			this.m_root = var1;
		}
	}

	public void setRoot(TreeNode var1) {
		if (var1 != this.m_root) {
			TreeNode var2 = this.m_root;
			this.m_root = var1;
			this.fireNodeRemoved(var2);
			if (var1 != null) {
				this.fireNodeAdded(var1);
			}
		}

	}

	public int getNodeCount() {
		return this.m_root == null ? 0 : this.m_root.getDescendantCount() + 1;
	}

	public int getEdgeCount() {
		return Math.max(0, this.getNodeCount() - 1);
	}

	public Iterator getNodes() {
		return (Iterator)(this.m_root == null ? Collections.EMPTY_LIST.iterator() : new BreadthFirstTreeIterator(this.m_root));
	}

	public Iterator getEdges() {
		return new TreeEdgeIterator(this.getNodes());
	}

	public TreeNode getRoot() {
		return this.m_root;
	}

	public int getDepth(TreeNode var1) {
		int var2 = 0;

		TreeNode var3;
		for(var3 = var1; var3 != this.m_root && var3 != null; var3 = var3.getParent()) {
			++var2;
		}

		return var3 == null ? -1 : var2;
	}

	public boolean addChild(Edge var1) {
		TreeNode var2 = (TreeNode)var1.getFirstNode();
		TreeNode var3 = (TreeNode)var1.getSecondNode();
		TreeNode var4 = this.contains((Node)var2) ? var2 : var3;
		TreeNode var5 = var4 == var2 ? var3 : var2;
		if (!var1.isDirected() && this.contains((Node)var4) && !this.contains((Node)var5)) {
			var4.addChild(var1);
			this.fireNodeAdded(var5);
			this.fireEdgeAdded(var1);
			return true;
		} else {
			return false;
		}
	}

	public boolean addChild(Node var1, Node var2) {
		return this.addChild(new DefaultEdge(var1, var2));
	}

	public boolean removeChild(Edge var1) {
		TreeNode var2 = (TreeNode)var1.getFirstNode();
		TreeNode var3 = (TreeNode)var1.getSecondNode();
		if (this.contains((Node)var2) && this.contains((Node)var3)) {
			int var4;
			TreeNode var5;
			TreeNode var6;
			if ((var4 = var2.getChildIndex(var1)) > -1) {
				var5 = var2;
				var6 = var3;
			} else {
				if ((var4 = var3.getChildIndex(var1)) <= -1) {
					return false;
				}

				var5 = var3;
				var6 = var2;
			}

			var5.removeChild(var4);
			this.fireEdgeRemoved(var1);
			this.fireNodeRemoved(var6);
			return true;
		} else {
			return false;
		}
	}

	public boolean addNode(Node var1) {
		throw new UnsupportedOperationException("DefaultTree does not support addNode(). Use setRoot() or addChild() instead");
	}

	public boolean addEdge(Edge var1) {
		if (var1.isDirected()) {
			throw new IllegalStateException("Directedness of edge and graph differ");
		} else {
			Node var2 = var1.getFirstNode();
			Node var3 = var1.getSecondNode();
			if (this.contains(var2) && this.contains(var3) && !var2.isNeighbor(var3) && !var3.isNeighbor(var2)) {
				var2.addEdge(var1);
				var3.addEdge(var1);
				this.fireEdgeAdded(var1);
				return true;
			} else {
				return false;
			}
		}
	}

	public boolean removeNode(Node var1) {
		if (!this.contains(var1)) {
			return false;
		} else {
			if (var1 == this.m_root) {
				this.m_root = null;
			} else {
				((TreeNode)var1).getParent().removeChild((TreeNode)var1);
			}

			this.fireNodeRemoved(var1);
			return true;
		}
	}

	public boolean removeEdge(Edge var1) {
		if (!this.contains(var1)) {
			return false;
		} else {
			TreeNode var2 = (TreeNode)var1.getFirstNode();
			TreeNode var3 = (TreeNode)var1.getSecondNode();
			if (!var1.isTreeEdge()) {
				var2.removeEdge(var1);
				var3.removeEdge(var1);
				this.fireEdgeRemoved(var1);
			} else {
				TreeNode var4;
				if (var2 != this.m_root && var3 != this.m_root) {
					var4 = var2.getParent() == var3 ? var3 : var2;
					var4.removeChildEdge(var1);
					this.fireNodeRemoved(var1.getAdjacentNode(var4));
				} else {
					var4 = this.m_root;
					this.m_root = null;
					this.fireNodeRemoved(var4);
				}
			}

			return true;
		}
	}

	public boolean replaceNode(Node var1, Node var2) {
		if (var2.getEdgeCount() <= 0 && this.contains(var1) && !this.contains(var2)) {
			if (!(var2 instanceof TreeNode)) {
				throw new IllegalArgumentException("Node next must be a TreeNode");
			} else {
				TreeNode var3 = (TreeNode)var1;
				TreeNode var4 = (TreeNode)var2;

				Iterator var5;
				Edge var6;
				for(var5 = var3.getEdges(); var5.hasNext(); var4.addEdge(var6)) {
					var6 = (Edge)var5.next();
					if (var6.getFirstNode() == var3) {
						var6.setFirstNode(var4);
					} else {
						var6.setSecondNode(var4);
					}
				}

				var5 = var3.getChildEdges();

				while(var5.hasNext()) {
					var6 = (Edge)var5.next();
					var4.addChild(var6);
				}

				((TreeNode)var1).removeAllAsChildren();
				var1.removeAllNeighbors();
				if (var3 == this.m_root) {
					this.setRoot(var4);
				}

				this.fireNodeReplaced(var1, var2);
				return true;
			}
		} else {
			return false;
		}
	}

	public boolean replaceEdge(Edge var1, Edge var2) {
		boolean var3 = this.contains(var1) && !this.contains(var2) && !var2.isDirected();
		if (!var3) {
			return false;
		} else {
			Node var4 = var1.getFirstNode();
			Node var5 = var1.getSecondNode();
			Node var6 = var2.getFirstNode();
			Node var7 = var2.getSecondNode();
			var3 = var4 == var6 && var5 == var7 || var4 == var7 && var5 == var6;
			if (var3) {
				int var8 = var4.getIndex(var1);
				var4.removeEdge(var8);
				var4.addEdge(var8, var2);
				var8 = var5.getIndex(var1);
				var5.removeEdge(var8);
				var5.addEdge(var8, var2);
				this.fireEdgeReplaced(var1, var2);
				return true;
			} else {
				return false;
			}
		}
	}

	public boolean contains(Node var1) {
		if (var1 instanceof TreeNode) {
			for(TreeNode var2 = (TreeNode)var1; var2 != null; var2 = var2.getParent()) {
				if (var2 != null && var2 == this.m_root) {
					return true;
				}
			}
		}

		return false;
	}

	public boolean contains(Edge var1) {
		Node var2 = var1.getFirstNode();
		return this.contains(var2) && var2.isIncidentEdge(var1);
	}
}
