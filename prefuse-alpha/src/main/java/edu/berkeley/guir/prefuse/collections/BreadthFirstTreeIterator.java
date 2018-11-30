package edu.berkeley.guir.prefuse.collections;

import edu.berkeley.guir.prefuse.graph.TreeNode;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.NoSuchElementException;

public class BreadthFirstTreeIterator
		implements Iterator {
	private LinkedList m_queue = new LinkedList();

	public BreadthFirstTreeIterator(TreeNode paramTreeNode) {
		this.m_queue.add(paramTreeNode);
	}

	public void remove() {
		throw new UnsupportedOperationException();
	}

	public boolean hasNext() {
		return !this.m_queue.isEmpty();
	}

	public Object next() {
		if (this.m_queue.isEmpty()) {
			throw new NoSuchElementException();
		}
		TreeNode localTreeNode1 = (TreeNode) this.m_queue.removeFirst();
		Iterator localIterator = localTreeNode1.getChildren();
		while (localIterator.hasNext()) {
			TreeNode localTreeNode2 = (TreeNode) localIterator.next();
			this.m_queue.add(localTreeNode2);
		}
		return localTreeNode1;
	}
}


/* Location:              /home/vad/work/JAVA/2018.11.30/prefuse-apps.jar!/edu/berkeley/guir/prefuse/collections/BreadthFirstTreeIterator.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */