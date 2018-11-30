package edu.berkeley.guir.prefuse.collections;

import edu.berkeley.guir.prefuse.graph.Edge;
import edu.berkeley.guir.prefuse.graph.Node;

import java.util.Comparator;

public class EdgeNodeComparator
		implements Comparator {
	private Comparator nodecmp;
	private Node n;

	public EdgeNodeComparator(Comparator paramComparator) {
		this(paramComparator, null);
	}

	public EdgeNodeComparator(Comparator paramComparator, Node paramNode) {
		this.nodecmp = paramComparator;
		this.n = paramNode;
	}

	public void setIgnoredNode(Node paramNode) {
		this.n = paramNode;
	}

	public Node getIgnoredNode() {
		return this.n;
	}

	public int compare(Object paramObject1, Object paramObject2) {
		if (((paramObject1 instanceof Edge)) && ((paramObject2 instanceof Edge))) {
			Node localNode1 = ((Edge) paramObject1).getAdjacentNode(this.n);
			Node localNode2 = ((Edge) paramObject2).getAdjacentNode(this.n);
			return this.nodecmp.compare(localNode1, localNode2);
		}
		throw new IllegalArgumentException("Compared objects must be Edge instances");
	}
}


/* Location:              /home/vad/work/JAVA/2018.11.30/prefuse-apps.jar!/edu/berkeley/guir/prefuse/collections/EdgeNodeComparator.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */