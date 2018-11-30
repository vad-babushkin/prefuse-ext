package edu.berkeley.guir.prefuse.collections;

import edu.berkeley.guir.prefuse.graph.Edge;
import edu.berkeley.guir.prefuse.graph.TreeNode;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class TreeEdgeIterator
		implements Iterator {
	private Iterator m_nodeIterator;
	private Iterator m_edgeIterator;
	private TreeNode m_curNode;
	private Edge m_next;

	public TreeEdgeIterator(Iterator paramIterator) {
		this.m_nodeIterator = paramIterator;
		if (paramIterator.hasNext()) {
			this.m_curNode = ((TreeNode) paramIterator.next());
			this.m_edgeIterator = this.m_curNode.getChildEdges();
		}
		this.m_next = findNext();
	}

	public void remove() {
		throw new UnsupportedOperationException();
	}

	public boolean hasNext() {
		return this.m_next != null;
	}

	public Object next() {
		if (this.m_next == null) {
			throw new NoSuchElementException("No next item in iterator");
		}
		Edge localEdge = this.m_next;
		this.m_next = findNext();
		return localEdge;
	}

	private Edge findNext() {
		for (; ; ) {
			if ((this.m_edgeIterator != null) && (this.m_edgeIterator.hasNext())) {
				return (Edge) this.m_edgeIterator.next();
			}
			if (!this.m_nodeIterator.hasNext()) {
				break;
			}
			this.m_curNode = ((TreeNode) this.m_nodeIterator.next());
			this.m_edgeIterator = this.m_curNode.getChildEdges();
		}
		this.m_curNode = null;
		this.m_nodeIterator = null;
		this.m_edgeIterator = null;
		return null;
	}
}


/* Location:              /home/vad/work/JAVA/2018.11.30/prefuse-apps.jar!/edu/berkeley/guir/prefuse/collections/TreeEdgeIterator.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */