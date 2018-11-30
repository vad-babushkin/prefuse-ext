//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package edu.berkeley.guir.prefuse.graph;

import edu.berkeley.guir.prefuse.collections.NodeIterator;
import edu.berkeley.guir.prefuse.collections.WrapAroundIterator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class DefaultTreeNode extends DefaultNode implements TreeNode {
	protected List m_children;
	protected int m_numDescendants = 0;
	protected TreeNode m_parent;
	protected Edge m_parentEdge;

	public DefaultTreeNode() {
		this.m_parentEdge = null;
		this.m_parent = null;
	}

	public DefaultTreeNode(Edge var1) {
		((TreeNode)var1.getAdjacentNode(this)).addChild(var1);
	}

	public boolean addChild(Edge var1) {
		int var2 = this.m_children == null ? 0 : this.m_children.size();
		return this.addChild(var2, var1);
	}

	public boolean addChild(int var1, Edge var2) {
		Node var3 = var2.getAdjacentNode(this);
		if (var3 != null && !var2.isDirected() && var3 instanceof TreeNode) {
			TreeNode var4 = (TreeNode)var3;
			if (this.getIndex(var4) > -1) {
				throw new IllegalStateException("Node is already a neighbor.");
			} else if (this.getChildIndex(var4) > -1) {
				return false;
			} else {
				if (this.m_children == null) {
					this.m_children = new ArrayList(3);
				}

				int var5 = var1 > 0 ? this.getIndex(this.getChild(var1 - 1)) + 1 : 0;
				super.addEdge(var5, var2);
				this.m_children.add(var1, var2);
				var4.addEdge(var2);
				var4.setParentEdge(var2);
				int var6 = 1 + var4.getDescendantCount();

				for(Object var7 = this; var7 != null; var7 = ((TreeNode)var7).getParent()) {
					((TreeNode)var7).setDescendantCount(((TreeNode)var7).getDescendantCount() + var6);
				}

				return true;
			}
		} else {
			throw new IllegalArgumentException("Not a valid, connecting tree edge!");
		}
	}

	public TreeNode getChild(int var1) {
		if (this.m_children != null && var1 >= 0 && var1 < this.m_children.size()) {
			return (TreeNode)((Edge)this.m_children.get(var1)).getAdjacentNode(this);
		} else {
			throw new IndexOutOfBoundsException();
		}
	}

	public int getChildCount() {
		return this.m_children == null ? 0 : this.m_children.size();
	}

	public Edge getChildEdge(int var1) {
		if (this.m_children != null && var1 >= 0 && var1 < this.m_children.size()) {
			return (Edge)this.m_children.get(var1);
		} else {
			throw new IndexOutOfBoundsException();
		}
	}

	public int getChildIndex(Edge var1) {
		return this.m_children == null ? -1 : this.m_children.indexOf(var1);
	}

	public int getChildIndex(TreeNode var1) {
		if (this.m_children != null) {
			for(int var2 = 0; var2 < this.m_children.size(); ++var2) {
				if (var1 == ((Edge)this.m_children.get(var2)).getAdjacentNode(this)) {
					return var2;
				}
			}
		}

		return -1;
	}

	public Iterator getChildEdges() {
		if (this.m_children != null && this.m_children.size() != 0) {
			int var1 = this.m_parent == null ? 0 : GraphLib.nearestIndex(this, this.m_parent) % this.m_children.size();
			return (Iterator)(var1 == 0 ? this.m_children.iterator() : new WrapAroundIterator(this.m_children, var1));
		} else {
			return Collections.EMPTY_LIST.iterator();
		}
	}

	public Iterator getChildren() {
		return new NodeIterator(this.getChildEdges(), this);
	}

	public int getDescendantCount() {
		return this.m_numDescendants;
	}

	public TreeNode getNextSibling() {
		int var1 = this.m_parent.getChildIndex(this) + 1;
		return var1 == this.m_parent.getChildCount() ? null : this.m_parent.getChild(var1);
	}

	public TreeNode getParent() {
		return this.m_parent;
	}

	public Edge getParentEdge() {
		return this.m_parentEdge;
	}

	public TreeNode getPreviousSibling() {
		int var1 = this.m_parent.getChildIndex(this);
		return var1 == 0 ? null : this.m_parent.getChild(var1 - 1);
	}

	public boolean isChild(TreeNode var1) {
		return this.getChildIndex(var1) >= 0;
	}

	public boolean isChildEdge(Edge var1) {
		return this.m_children == null ? false : this.m_children.indexOf(var1) > -1;
	}

	public boolean isDescendant(TreeNode var1) {
		while(var1 != null) {
			if (this == var1) {
				return true;
			}

			var1 = var1.getParent();
		}

		return false;
	}

	public boolean isSibling(TreeNode var1) {
		return this != var1 && this.getParent() == var1.getParent();
	}

	public void removeAllAsChildren() {
		if (this.m_children != null) {
			Iterator var1 = this.m_children.iterator();

			while(var1.hasNext()) {
				TreeNode var2 = (TreeNode)((Edge)var1.next()).getAdjacentNode(this);
				var2.setParentEdge((Edge)null);
			}

			this.m_children.clear();
			int var4 = this.m_numDescendants;

			for(Object var3 = this; var3 != null; var3 = ((TreeNode)var3).getParent()) {
				((TreeNode)var3).setDescendantCount(((TreeNode)var3).getDescendantCount() - var4);
			}

		}
	}

	public void removeAllChildren() {
		if (this.m_children != null) {
			Iterator var1 = this.m_children.iterator();

			while(var1.hasNext()) {
				Edge var2 = (Edge)var1.next();
				TreeNode var3 = (TreeNode)var2.getAdjacentNode(this);
				var3.setParentEdge((Edge)null);
				var3.removeNeighbor(this);
				this.removeEdge(var2);
			}

			this.m_children.clear();
			int var4 = this.m_numDescendants;

			for(Object var5 = this; var5 != null; var5 = ((TreeNode)var5).getParent()) {
				((TreeNode)var5).setDescendantCount(((TreeNode)var5).getDescendantCount() - var4);
			}

		}
	}

	public boolean removeAsChild(TreeNode var1) {
		return this.removeAsChild(this.getChildIndex(var1)) != null;
	}

	public TreeNode removeAsChild(int var1) {
		if (var1 >= 0 && var1 < this.getChildCount()) {
			Edge var2 = (Edge)this.m_children.remove(var1);
			TreeNode var3 = (TreeNode)var2.getAdjacentNode(this);
			var3.setParentEdge((Edge)null);
			int var4 = 1 + var3.getDescendantCount();

			for(Object var5 = this; var5 != null; var5 = ((TreeNode)var5).getParent()) {
				((TreeNode)var5).setDescendantCount(((TreeNode)var5).getDescendantCount() - var4);
			}

			return var3;
		} else {
			throw new IndexOutOfBoundsException();
		}
	}

	public boolean removeChild(TreeNode var1) {
		return this.removeChildAndNeighbor(this.getChildIndex(var1)) != null;
	}

	public TreeNode removeChild(int var1) {
		return this.removeChildAndNeighbor(var1);
	}

	private TreeNode removeChildAndNeighbor(int var1) {
		super.removeEdge(super.getEdge(this.getChild(var1)));
		TreeNode var2 = this.removeAsChild(var1);
		var2.removeNeighbor(this);
		return var2;
	}

	public boolean removeChildEdge(Edge var1) {
		return this.removeChildEdge(this.getChildIndex(var1)) != null;
	}

	public Edge removeChildEdge(int var1) {
		if (var1 >= 0 && var1 < this.getChildCount()) {
			Edge var2 = (Edge)this.m_children.remove(var1);
			TreeNode var3 = (TreeNode)var2.getAdjacentNode(this);
			var3.setParentEdge((Edge)null);
			var3.removeEdge(var2);
			int var4 = 1 + var3.getDescendantCount();

			for(Object var5 = this; var5 != null; var5 = ((TreeNode)var5).getParent()) {
				((TreeNode)var5).setDescendantCount(((TreeNode)var5).getDescendantCount() - var4);
			}

			return var2;
		} else {
			throw new IndexOutOfBoundsException();
		}
	}

	public boolean setAsChild(TreeNode var1) {
		int var2 = this.m_children == null ? 0 : this.m_children.size();
		return this.setAsChild(var2, var1);
	}

	public boolean setAsChild(int var1, TreeNode var2) {
		int var3;
		if ((var3 = this.getIndex(var2)) < 0) {
			throw new IllegalStateException("Node is not already a neighbor!");
		} else if (this.getChildIndex(var2) > -1) {
			return false;
		} else {
			int var4 = this.m_children == null ? 0 : this.m_children.size();
			if (var1 >= 0 && var1 <= var4) {
				if (this.m_children == null) {
					this.m_children = new ArrayList(3);
				}

				Edge var5 = this.getEdge(var3);
				this.m_children.add(var1, var5);
				var2.setParentEdge(var5);
				int var6 = 1 + var2.getDescendantCount();

				for(Object var7 = this; var7 != null; var7 = ((TreeNode)var7).getParent()) {
					((TreeNode)var7).setDescendantCount(((TreeNode)var7).getDescendantCount() + var6);
				}

				return true;
			} else {
				throw new IndexOutOfBoundsException();
			}
		}
	}

	public void setDescendantCount(int var1) {
		this.m_numDescendants = var1;
	}

	public void setParentEdge(Edge var1) {
		this.m_parentEdge = var1;
		this.m_parent = var1 == null ? null : (TreeNode)var1.getAdjacentNode(this);
	}
}
