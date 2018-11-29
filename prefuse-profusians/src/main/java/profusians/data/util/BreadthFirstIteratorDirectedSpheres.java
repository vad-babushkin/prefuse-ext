package profusians.data.util;

import java.util.Iterator;

import prefuse.Constants;
import prefuse.data.Edge;
import prefuse.data.Node;
import prefuse.data.Tuple;
import prefuse.util.collections.Queue;

public class BreadthFirstIteratorDirectedSpheres implements Iterator {

    protected Queue m_queue = new Queue();

    protected int m_depth;

    protected int m_successorDepth;

    protected int m_predecessorDepth;

    protected int m_traversal;

    protected boolean m_includeNodes;

    protected boolean m_includeEdges;

    public BreadthFirstIteratorDirectedSpheres() {
	// do nothing, requires init call
    }

    public BreadthFirstIteratorDirectedSpheres( Node n,
	     int successorDepth, int predecessorDepth,
	     int traversal) {
	init(new Node[] { n }, successorDepth, predecessorDepth, traversal);
    }

    public BreadthFirstIteratorDirectedSpheres( Iterator it,
	     int successorDepth, int predecessorDepth,
	     int traversal) {
	init(it, successorDepth, predecessorDepth, traversal);
    }

    public void init( Object o, int successorDepth,
	     int predecessorDepth, int traversal) {
	// initialize the member variables
	m_queue.clear();

	m_successorDepth = successorDepth;
	m_predecessorDepth = predecessorDepth;
	m_depth = Math.max(successorDepth, predecessorDepth);

	if ((traversal < 0) || (traversal >= Constants.TRAVERSAL_COUNT)) {
	    throw new IllegalArgumentException("Unrecognized traversal type: "
		    + traversal);
	}
	m_traversal = traversal;
	m_includeNodes = ((traversal == Constants.NODE_TRAVERSAL) || (traversal == Constants.NODE_AND_EDGE_TRAVERSAL));
	m_includeEdges = ((traversal == Constants.EDGE_TRAVERSAL) || (traversal == Constants.NODE_AND_EDGE_TRAVERSAL));

	// seed the queue
	// TODO: clean this up? (use generalized iterator?)
	if (m_includeNodes) {
	    if (o instanceof Node) {
		m_queue.add(o, 0);
	    } else {
		 Iterator tuples = (Iterator) o;
		while (tuples.hasNext()) {
		    m_queue.add(tuples.next(), 0);
		}
	    }
	} else {
	    if (o instanceof Node) {
		 Node n = (Node) o;
		m_queue.visit(n, 0);
		 Iterator edges = getEdges(n);
		while (edges.hasNext()) {
		     Edge e = (Edge) edges.next();
		     Node nn = e.getAdjacentNode(n);
		    if (stopTraversing(nn, e, 0)) {
			continue;
		    }
		    m_queue.visit(nn, 1);
		    if (m_queue.getDepth(e) < 0) {
			m_queue.add(e, 1);
		    }
		}
	    } else {
		 Iterator tuples = (Iterator) o;
		while (tuples.hasNext()) {
		    // TODO: graceful error handling when non-node in set?
		     Node n = (Node) tuples.next();
		    m_queue.visit(n, 0);
		     Iterator edges = getEdges(n);
		    while (edges.hasNext()) {
			 Edge e = (Edge) edges.next();
			 Node nn = e.getAdjacentNode(n);
			if (stopTraversing(nn, e, 0)) {
			    continue;
			}
			m_queue.visit(nn, 1);
			if (m_queue.getDepth(e) < 0) {
			    m_queue.add(e, 1);
			}
		    }
		}
	    }
	}
    }

    // ------------------------------------------------------------------------

    public void remove() {
	throw new UnsupportedOperationException();
    }

    public boolean hasNext() {
	return !m_queue.isEmpty();
    }

    protected Iterator getEdges( Node n) {
	return n.edges(); // TODO: add support for all edges, in links
	// only, out links only
    }

    public int getDepth( Tuple t) {
	return m_queue.getDepth(t);
    }

    public Object next() {
	Tuple t = (Tuple) m_queue.removeFirst();

	switch (m_traversal) {

	case Constants.NODE_TRAVERSAL:
	case Constants.NODE_AND_EDGE_TRAVERSAL:
	    for (; true; t = (Tuple) m_queue.removeFirst()) {
		if (t instanceof Edge) {
		    return t;
		} else {
		     Node n = (Node) t;
		     int d = m_queue.getDepth(n);

		    if (d < m_depth) {
			 int dd = d + 1;
			 Iterator edges = getEdges(n);
			while (edges.hasNext()) {
			     Edge e = (Edge) edges.next();
			     Node v = e.getAdjacentNode(n);
			    if (stopTraversing(v, e, d)) {
				continue;
			    }
			    if (m_includeEdges && (m_queue.getDepth(e) < 0)) {
				m_queue.add(e, dd);
			    }
			    if (m_queue.getDepth(v) < 0) {
				m_queue.add(v, dd);
			    }
			}
		    } else if (m_includeEdges && (d == m_depth)) {
			 Iterator edges = getEdges(n);
			while (edges.hasNext()) {
			     Edge e = (Edge) edges.next();
			     Node v = e.getAdjacentNode(n);

			     int dv = m_queue.getDepth(v);
			    if ((dv > 0) && (m_queue.getDepth(e) < 0)) {
				m_queue.add(e, Math.min(d, dv));
			    }
			}
		    }
		    return n;
		}
	    }

	case Constants.EDGE_TRAVERSAL:
	     Edge e = (Edge) t;
	     Node u = e.getSourceNode();
	     Node v = e.getTargetNode();
	     int du = m_queue.getDepth(u);
	     int dv = m_queue.getDepth(v);

	    if (du != dv) {
		 Node n = (dv > du ? v : u);
		 int d = Math.max(du, dv);

		if (d < m_depth) {
		     int dd = d + 1;
		     Iterator edges = getEdges(n);
		    while (edges.hasNext()) {
			 Edge ee = (Edge) edges.next();

			if (m_queue.getDepth(ee) >= 0) {
			    continue; // already visited
			}

			 Node nn = ee.getAdjacentNode(n);

			if (stopTraversing(nn, ee, d)) {
			    continue;
			}
			m_queue.visit(nn, dd);
			m_queue.add(ee, dd);
		    }
		}
	    }
	    return e;

	default:
	    throw new IllegalStateException();
	}
    }

    private boolean stopTraversing( Node n, Edge e, int depth) {
	if (((n == e.getTargetNode()) && (m_successorDepth <= depth))
		|| ((n == e.getSourceNode()) && (m_predecessorDepth <= depth))) {
	    return true;
	}
	return false;
    }
}
