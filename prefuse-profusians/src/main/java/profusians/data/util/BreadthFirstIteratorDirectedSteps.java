package profusians.data.util;

import java.util.Iterator;

import prefuse.Constants;
import prefuse.data.Edge;
import prefuse.data.Node;
import prefuse.data.Tuple;
import profusians.util.collections.DirectedDepthQueue;

/**
 * Provides a distance-limited breadth first traversal over nodes, edges, or
 * both, using any number of traversal "roots".
 * 
 */

public class BreadthFirstIteratorDirectedSteps implements Iterator {

    protected DirectedDepthQueue m_queue = new DirectedDepthQueue();

    protected int m_successorStepsDepth;

    protected int m_predecessorStepsDepth;

    protected int m_traversal;

    protected boolean m_includeNodes;

    protected boolean m_includeEdges;

    public BreadthFirstIteratorDirectedSteps() {
	// do nothing, requires init call
    }

    public BreadthFirstIteratorDirectedSteps( Node n,
	     int successorStepsDepth, int predecessorStepsDepth,
	     int traversal) {
	init(new Node[] { n }, successorStepsDepth, predecessorStepsDepth,
		traversal);
    }

    public BreadthFirstIteratorDirectedSteps( Iterator it,
	     int successorStepsDepth, int predecessorStepsDepth,
	     int traversal) {
	init(it, successorStepsDepth, predecessorStepsDepth, traversal);
    }

    public void init( Object o, int successorStepsDepth,
	     int predecessorStepsDepth, int traversal) {
	// initialize the member variables
	m_queue.clear();

	m_successorStepsDepth = successorStepsDepth;
	m_predecessorStepsDepth = predecessorStepsDepth;

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
		m_queue.add(o, 0, 0);
	    } else {
		 Iterator tuples = (Iterator) o;
		while (tuples.hasNext()) {
		    m_queue.add(tuples.next(), 0, 0);
		}
	    }
	} else {
	    if (o instanceof Node) {
		 Node n = (Node) o;
		markVisit(n);
	    } else {
		 Iterator tuples = (Iterator) o;
		while (tuples.hasNext()) {

		     Node n = (Node) tuples.next();
		    markVisit(n);
		}
	    }
	}
    }

    // ------------------------------------------------------------------------

    /**
         * @see java.util.Iterator#remove()
         */
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

    public int getSuccessorDepth( Tuple t) {
	return m_queue.getSuccessorDepth(t);
    }

    public int getPredecessorDepth( Tuple t) {
	return m_queue.getPredecessorDepth(t);
    }

    /**
         * @see java.util.Iterator#next()
         */
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

		    int sd = m_queue.getSuccessorDepth(n);

		    if (sd < m_successorStepsDepth) {
			 int dd = sd + 1;
			 Iterator edges = n.outEdges();
			while (edges.hasNext()) {
			     Edge e = (Edge) edges.next();
			     Node v = e.getAdjacentNode(n);

			    if (m_includeEdges
				    && (m_queue.getSuccessorDepth(e) < 0)) {
				m_queue.add(e, dd, m_queue
					.getPredecessorDepth(n));
			    }
			    if (m_queue.getSuccessorDepth(v) < 0) {
				m_queue.add(v, dd, m_queue
					.getPredecessorDepth(n));
			    }
			}
		    }

		    int pd = m_queue.getPredecessorDepth(n);

		    if (pd < m_predecessorStepsDepth) {
			 int dd = pd + 1;
			 Iterator edges = n.inEdges();
			while (edges.hasNext()) {
			     Edge e = (Edge) edges.next();
			     Node v = e.getAdjacentNode(n);

			    if (m_includeEdges
				    && (m_queue.getPredecessorDepth(e) < 0)) {
				m_queue
					.add(e, m_queue.getSuccessorDepth(n),
						dd);
			    }
			    if (m_queue.getPredecessorDepth(v) < 0) {
				m_queue
					.add(v, m_queue.getSuccessorDepth(n),
						dd);
			    }
			}
		    }

		    else if (m_includeEdges) {
			sd = m_queue.getSuccessorDepth(n);
			if (sd == m_successorStepsDepth) {
			     Iterator edges = n.outEdges();
			    while (edges.hasNext()) {
				 Edge e = (Edge) edges.next();
				 Node v = e.getAdjacentNode(n);
				 int dv = m_queue.getSuccessorDepth(v);
				if ((dv > 0)
					&& (m_queue.getSuccessorDepth(e) < 0)) {
				     int un = m_queue
					    .getPredecessorDepth(n);
				     int uv = m_queue
					    .getPredecessorDepth(v);
				    if (sd + un < dv + uv) {
					m_queue.add(e, sd, un);
				    } else if (sd + un > dv + uv) {
					m_queue.add(e, dv, uv);
				    } else {
					if (sd < dv) {
					    m_queue.add(e, sd, un);
					} else {
					    m_queue.add(e, dv, uv);
					}
				    }
				}
			    }
			}
			pd = m_queue.getPredecessorDepth(n);
			if (pd == m_predecessorStepsDepth) {
			     Iterator edges = n.inEdges();
			    while (edges.hasNext()) {
				 Edge e = (Edge) edges.next();
				 Node v = e.getAdjacentNode(n);
				 int dv = m_queue.getPredecessorDepth(v);
				if ((dv > 0)
					&& (m_queue.getPredecessorDepth(e) < 0)) {
				     int un = m_queue.getSuccessorDepth(n);
				     int uv = m_queue.getSuccessorDepth(v);
				    if (un + pd < uv + dv) {
					m_queue.add(e, un, pd);
				    } else if (un + pd > uv + dv) {
					m_queue.add(e, uv, dv);
				    } else {
					if (un < uv) {
					    m_queue.add(e, un, pd);
					} else {
					    m_queue.add(e, uv, dv);
					}
				    }
				}
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

	    int du = m_queue.getSuccessorDepth(u);
	    int dv = m_queue.getSuccessorDepth(v);

	    if (du != dv) {
		Node n = (dv > du ? v : u);
		int d1, d2;
		if (du > dv) {
		    d1 = du;
		    d2 = m_queue.getPredecessorDepth(u);
		} else {
		    d1 = dv;
		    d2 = m_queue.getPredecessorDepth(v);
		}

		if (d1 < m_successorStepsDepth) {
		     int dd = d1 + 1;
		     Iterator edges = n.outEdges();
		    while (edges.hasNext()) {
			 Edge ee = (Edge) edges.next();
			if (m_queue.getSuccessorDepth(ee) >= 0) {
			    continue; // already visited
			}

			 Node nn = ee.getAdjacentNode(n);
			m_queue.visit(nn, dd, d2);
			m_queue.add(ee, dd, d2);
		    }
		}

		du = m_queue.getPredecessorDepth(u);
		dv = m_queue.getPredecessorDepth(v);

		if (du != dv) {
		    n = (dv > du ? v : u);

		    if (du > dv) {
			d1 = du;
			d2 = m_queue.getSuccessorDepth(u);
		    } else {
			d1 = dv;
			d2 = m_queue.getSuccessorDepth(v);
		    }

		    if (d1 < m_predecessorStepsDepth) {
			 int dd = d1 + 1;
			 Iterator edges = n.inEdges();
			while (edges.hasNext()) {
			     Edge ee = (Edge) edges.next();
			    if (m_queue.getPredecessorDepth(ee) >= 0) {
				continue; // already visited
			    }

			     Node nn = ee.getAdjacentNode(n);
			    m_queue.visit(nn, d2, dd);
			    m_queue.add(ee, d2, dd);
			}
		    }
		}

	    }

	    return e;

	default:
	    throw new IllegalStateException();
	}
    }

    private void markVisit( Node n) {
	m_queue.visit(n, 0, 0);
	 Iterator edges = n.edges();
	while (edges.hasNext()) {
	     Edge e = (Edge) edges.next();
	     Node nn = e.getAdjacentNode(n);

	    if (nn == e.getTargetNode()) {

		m_queue.visit(nn, 1, 0);
		if (m_queue.getSuccessorDepth(e) < 0) {
		    m_queue.add(e, 1, 0);
		}
	    } else {
		m_queue.visit(nn, 0, 1);
		if (m_queue.getPredecessorDepth(e) < 0) {
		    m_queue.add(e, 0, 1);
		}
	    }
	}

    }
} // end of class BreadthFirstIterator

