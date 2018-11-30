//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package edu.berkeley.guir.prefuse.focus;

import edu.berkeley.guir.prefuse.event.FocusEvent;
import edu.berkeley.guir.prefuse.event.FocusEventMulticaster;
import edu.berkeley.guir.prefuse.event.FocusListener;
import edu.berkeley.guir.prefuse.graph.Entity;
import edu.berkeley.guir.prefuse.graph.Tree;
import edu.berkeley.guir.prefuse.util.Trie;
import edu.berkeley.guir.prefuse.util.Trie.TrieIterator;
import edu.berkeley.guir.prefuse.util.Trie.TrieNode;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.StringTokenizer;

public class KeywordSearchFocusSet implements FocusSet {
	private FocusListener m_listener;
	private LinkedHashSet m_set;
	private Trie m_trie;
	private TrieNode m_curNode;
	private String m_delim;
	private String m_query;

	public KeywordSearchFocusSet() {
		this(false);
	}

	public KeywordSearchFocusSet(boolean var1) {
		this.m_listener = null;
		this.m_set = new LinkedHashSet();
		this.m_delim = ", ";
		this.m_query = null;
		this.m_trie = new Trie(var1);
	}

	public void addFocusListener(FocusListener var1) {
		this.m_listener = FocusEventMulticaster.add(this.m_listener, var1);
	}

	public void removeFocusListener(FocusListener var1) {
		this.m_listener = FocusEventMulticaster.remove(this.m_listener, var1);
	}

	public String getDelimiterString() {
		return this.m_delim;
	}

	public void setDelimiterString(String var1) {
		this.m_delim = var1;
	}

	public String getQuery() {
		return this.m_query;
	}

	public void search(String var1) {
		Entity[] var2 = (Entity[])this.m_set.toArray(FocusEvent.EMPTY);
		this.m_set.clear();
		this.m_query = var1;
		this.m_curNode = this.m_trie.find(var1);
		if (this.m_curNode != null) {
			Iterator var3 = this.trieIterator();

			while(var3.hasNext()) {
				this.m_set.add(var3.next());
			}
		}

		Entity[] var5 = (Entity[])this.m_set.toArray(FocusEvent.EMPTY);
		FocusEvent var4 = new FocusEvent(this, 2, var5, var2);
		this.m_listener.focusChanged(var4);
	}

	public static Tree getTree(Iterator var0, String var1) {
		KeywordSearchFocusSet var2 = new KeywordSearchFocusSet(false);
		var2.index(var0, var1);
		return var2.m_trie.tree();
	}

	public void index(Iterator var1, String var2) {
		while(var1.hasNext()) {
			Entity var3 = (Entity)var1.next();
			this.index(var3, var2);
		}

	}

	public void index(Entity var1, String var2) {
		String var3;
		if ((var3 = var1.getAttribute(var2)) != null) {
			StringTokenizer var4 = new StringTokenizer(var3, this.m_delim);

			while(var4.hasMoreTokens()) {
				String var5 = var4.nextToken();
				this.addString(var5, var1);
			}

		}
	}

	private void addString(String var1, Entity var2) {
		this.m_trie.addString(var1, var2);
	}

	public void remove(Entity var1, String var2) {
		String var3;
		if ((var3 = var1.getAttribute(var2)) != null) {
			StringTokenizer var4 = new StringTokenizer(var3, this.m_delim);

			while(var4.hasMoreTokens()) {
				String var5 = var4.nextToken();
				this.removeString(var5, var1);
			}

		}
	}

	private void removeString(String var1, Entity var2) {
		this.m_trie.removeString(var1, var2);
	}

	public void clear() {
		this.m_curNode = null;
		this.m_query = null;
		Entity[] var1 = (Entity[])this.m_set.toArray(FocusEvent.EMPTY);
		this.m_set.clear();
		FocusEvent var2 = new FocusEvent(this, 1, (Entity[])null, var1);
		this.m_listener.focusChanged(var2);
	}

	public Iterator iterator() {
		return this.m_curNode == null ? Collections.EMPTY_LIST.iterator() : this.m_set.iterator();
	}

	private Iterator trieIterator() {
		Trie var10002 = this.m_trie;
		this.m_trie.getClass();
		//fixme
//		return new TrieIterator(var10002, this.m_curNode);
		return null;
	}

	public int size() {
		return this.m_curNode == null ? 0 : this.m_set.size();
	}

	public boolean contains(Entity var1) {
		return this.m_set.contains(var1);
	}

	public void add(Entity var1) {
		throw new UnsupportedOperationException();
	}

	public void add(Collection var1) {
		throw new UnsupportedOperationException();
	}

	public void remove(Entity var1) {
		throw new UnsupportedOperationException();
	}

	public void remove(Collection var1) {
		throw new UnsupportedOperationException();
	}

	public void set(Entity var1) {
		throw new UnsupportedOperationException();
	}

	public void set(Collection var1) {
		throw new UnsupportedOperationException();
	}
}
