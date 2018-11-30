package edu.berkeley.guir.prefuse.collections;

import edu.berkeley.guir.prefuse.graph.Node;

import java.util.*;

public class BreadthFirstGraphIterator
		implements Iterator {
	private Set m_visited = new HashSet();
	private LinkedList m_queue = new LinkedList();

	public BreadthFirstGraphIterator(Node paramNode) {
		this.m_queue.add(paramNode);
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
		Node localNode1 = (Node) this.m_queue.removeFirst();
		this.m_visited.add(localNode1);
		Iterator localIterator = localNode1.getNeighbors();
		while (localIterator.hasNext()) {
			Node localNode2 = (Node) localIterator.next();
			if (!this.m_visited.contains(localNode2)) {
				this.m_queue.add(localNode2);
			}
		}
		return localNode1;
	}
}


/* Location:              /home/vad/work/JAVA/2018.11.30/prefuse-apps.jar!/edu/berkeley/guir/prefuse/collections/BreadthFirstGraphIterator.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */