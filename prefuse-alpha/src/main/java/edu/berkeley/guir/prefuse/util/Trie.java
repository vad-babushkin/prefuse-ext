//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package edu.berkeley.guir.prefuse.util;

import edu.berkeley.guir.prefuse.graph.DefaultEdge;
import edu.berkeley.guir.prefuse.graph.DefaultTree;
import edu.berkeley.guir.prefuse.graph.DefaultTreeNode;
import edu.berkeley.guir.prefuse.graph.Entity;
import edu.berkeley.guir.prefuse.graph.Tree;
import edu.berkeley.guir.prefuse.graph.TreeNode;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.NoSuchElementException;

public class Trie {
	private Trie.TrieBranch root = new Trie.TrieBranch();
	private boolean caseSensitive = false;

	public Trie(boolean var1) {
		this.caseSensitive = var1;
	}

	public void addString(String var1, Entity var2) {
		Trie.TrieLeaf var3 = new Trie.TrieLeaf(var1, var2);
		this.addLeaf(this.root, var3, 0);
	}

	public void removeString(String var1, Entity var2) {
		this.removeLeaf(this.root, var1, var2, 0);
	}

	private final int getIndex(char[] var1, char var2) {
		for(int var3 = 0; var3 < var1.length; ++var3) {
			if (var1[var3] == var2) {
				return var3;
			}
		}

		return -1;
	}

	private final char getChar(String var1, int var2) {
		char var3 = var2 >= 0 && var2 < var1.length() ? var1.charAt(var2) : 0;
		return this.caseSensitive ? var3 : Character.toLowerCase(var3);
	}

	private boolean removeLeaf(Trie.TrieBranch var1, String var2, Entity var3, int var4) {
		char var5 = this.getChar(var2, var4);
		int var6 = this.getIndex(var1.chars, var5);
		if (var6 == -1) {
			return false;
		} else {
			Trie.TrieNode var7 = var1.children[var6];
			if (var7 instanceof Trie.TrieBranch) {
				Trie.TrieBranch var11 = (Trie.TrieBranch)var7;
				boolean var12 = this.removeLeaf(var11, var2, var3, var4 + 1);
				if (var12) {
					--var1.leafCount;
					if (var11.leafCount == 1) {
						var1.children[var6] = var11.children[var11.children[0] != null ? 0 : 1];
					}
				}

				return var12;
			} else {
				Trie.TrieLeaf var8 = (Trie.TrieLeaf)var7;
				if (var8.entity == var3) {
					var1.children[var6] = var8.next;
					if (var8.next == null) {
						this.repairBranch(var1, var6);
					}

					--var1.leafCount;
					return true;
				} else {
					Trie.TrieLeaf var9;
					for(var9 = var8.next; var9 != null && var9.entity != var3; var9 = var9.next) {
						var8 = var9;
					}

					if (var9 == null) {
						return false;
					} else {
						for(Trie.TrieLeaf var10 = (Trie.TrieLeaf)var7; var10.entity != var3; var10 = var10.next) {
							--var10.leafCount;
						}

						var8.next = var9.next;
						--var1.leafCount;
						return true;
					}
				}
			}
		}
	}

	private void repairBranch(Trie.TrieBranch var1, int var2) {
		if (var2 == 0) {
			var1.children[0] = null;
		} else {
			int var3 = var1.chars.length;
			char[] var4 = new char[var3 - 1];
			Trie.TrieNode[] var5 = new Trie.TrieNode[var3 - 1];
			System.arraycopy(var1.chars, 0, var4, 0, var2);
			System.arraycopy(var1.children, 0, var5, 0, var2);
			System.arraycopy(var1.chars, var2 + 1, var4, var2, var3 - var2 - 1);
			System.arraycopy(var1.children, var2 + 1, var5, var2, var3 - var2 - 1);
			var1.chars = var4;
			var1.children = var5;
		}

	}

	private void addLeaf(Trie.TrieBranch var1, Trie.TrieLeaf var2, int var3) {
		var1.leafCount += var2.leafCount;
		char var4 = this.getChar(var2.word, var3);
		int var5 = this.getIndex(var1.chars, var4);
		if (var5 == -1) {
			this.addChild(var1, var2, var4);
		} else {
			Trie.TrieNode var6 = var1.children[var5];
			if (var6 == null) {
				var1.children[var5] = var2;
			} else if (var6 instanceof Trie.TrieBranch) {
				this.addLeaf((Trie.TrieBranch)var6, var2, var3 + 1);
			} else {
				Trie.TrieLeaf var7 = (Trie.TrieLeaf)var6;
				if (var5 != 0) {
					label45: {
						if (this.caseSensitive) {
							if (var7.word.equals(var2.word)) {
								break label45;
							}
						} else if (var7.word.equalsIgnoreCase(var2.word)) {
							break label45;
						}

						Trie.TrieBranch var8 = new Trie.TrieBranch();
						var1.children[var5] = var8;
						this.addLeaf(var8, var7, var3 + 1);
						this.addLeaf(var8, var2, var3 + 1);
						return;
					}
				}

				while(var7.next != null) {
					++var7.leafCount;
					var7 = var7.next;
				}

				++var7.leafCount;
				var7.next = var2;
			}
		}

	}

	private void addChild(Trie.TrieBranch var1, Trie.TrieNode var2, char var3) {
		int var4 = var1.chars.length;
		char[] var5 = new char[var4 + 1];
		Trie.TrieNode[] var6 = new Trie.TrieNode[var4 + 1];
		System.arraycopy(var1.chars, 0, var5, 0, var4);
		System.arraycopy(var1.children, 0, var6, 0, var4);
		var5[var4] = var3;
		var6[var4] = var2;
		var1.chars = var5;
		var1.children = var6;
	}

	public Trie.TrieNode find(String var1) {
		return var1.length() < 1 ? null : this.find(var1, this.root, 0);
	}

	private Trie.TrieNode find(String var1, Trie.TrieBranch var2, int var3) {
		char var4 = this.getChar(var1, var3);
		int var5 = this.getIndex(var2.chars, var4);
		if (var5 == -1) {
			return null;
		} else if (var2.children[var5] instanceof Trie.TrieLeaf) {
			return var2.children[var5];
		} else {
			return var1.length() - 1 == var3 ? var2.children[var5] : this.find(var1, (Trie.TrieBranch)var2.children[var5], var3 + 1);
		}
	}

	public Tree tree() {
		DefaultTreeNode var1 = new DefaultTreeNode();
		var1.setAttribute("label", "root");
		this.tree(this.root, var1);
		return new DefaultTree(var1);
	}

	private void tree(Trie.TrieBranch var1, TreeNode var2) {
		for(int var3 = 0; var3 < var1.chars.length; ++var3) {
			if (var1.children[var3] instanceof Trie.TrieLeaf) {
				for(Trie.TrieLeaf var8 = (Trie.TrieLeaf)var1.children[var3]; var8 != null; var8 = var8.next) {
					DefaultTreeNode var7 = new DefaultTreeNode();
					var7.setAttribute("label", var8.word);
					DefaultEdge var6 = new DefaultEdge(var2, var7);
					var2.addChild(var6);
				}
			} else {
				DefaultTreeNode var4 = new DefaultTreeNode();
				var4.setAttribute("label", String.valueOf(var1.chars[var3]));
				DefaultEdge var5 = new DefaultEdge(var2, var4);
				var2.addChild(var5);
				this.tree((Trie.TrieBranch)var1.children[var3], var4);
			}
		}

	}

	public class TrieIterator implements Iterator {
		private LinkedList queue = new LinkedList();
		private Entity next;

		public TrieIterator(Trie.TrieNode var2) {
			this.queue.add(var2);
		}

		public boolean hasNext() {
			return !this.queue.isEmpty();
		}

		public Object next() {
			if (this.queue.isEmpty()) {
				throw new NoSuchElementException();
			} else {
				Trie.TrieNode var1 = (Trie.TrieNode)this.queue.removeFirst();
				if (var1 instanceof Trie.TrieLeaf) {
					Trie.TrieLeaf var5 = (Trie.TrieLeaf)var1;
					Entity var2 = var5.entity;
					if (var5.next != null) {
						this.queue.addFirst(var5.next);
					}

					return var2;
				} else {
					Trie.TrieBranch var3 = (Trie.TrieBranch)var1;

					for(int var4 = var3.chars.length - 1; var4 > 0; --var4) {
						this.queue.addFirst(var3.children[var4]);
					}

					if (var3.children[0] != null) {
						this.queue.addFirst(var3.children[0]);
					}

					return this.next();
				}
			}
		}

		public void remove() {
			throw new UnsupportedOperationException();
		}
	}

	public class TrieLeaf extends Trie.TrieNode {
		String word;
		Entity entity;
		Trie.TrieLeaf next;

		public TrieLeaf(String var2, Entity var3) {
			super();
			this.word = var2;
			this.entity = var3;
			this.next = null;
			this.leafCount = 1;
		}
	}

	public class TrieBranch extends Trie.TrieNode {
		char[] chars = new char[]{'\u0000'};
		Trie.TrieNode[] children = new Trie.TrieNode[1];

		public TrieBranch() {
			super();
		}
	}

	public class TrieNode {
		boolean isLeaf;
		int leafCount = 0;

		public TrieNode() {
		}
	}
}
