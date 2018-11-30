package edu.berkeley.guir.prefuse.collections;

import edu.berkeley.guir.prefuse.graph.Edge;
import edu.berkeley.guir.prefuse.graph.Node;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class NodeIterator
		implements Iterator {
	private Iterator edgeIter;
	private Node node;

	public NodeIterator(Iterator paramIterator, Node paramNode) {
		this.edgeIter = paramIterator;
		this.node = paramNode;
	}

	public void remove() {
		throw new UnsupportedOperationException();
	}

	public boolean hasNext() {
		return this.edgeIter.hasNext();
	}

	public Object next() {
		if (!this.edgeIter.hasNext()) {
			throw new NoSuchElementException();
		}
		Edge localEdge = (Edge) this.edgeIter.next();
		return localEdge.getAdjacentNode(this.node);
	}
}


/* Location:              /home/vad/work/JAVA/2018.11.30/prefuse-apps.jar!/edu/berkeley/guir/prefuse/collections/NodeIterator.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */