package profusians.visual.util;

import java.util.Iterator;

import prefuse.Constants;
import prefuse.util.collections.Queue;
import prefuse.visual.EdgeItem;
import prefuse.visual.NodeItem;
import prefuse.visual.VisualItem;

/**
 * 
 * Provides a distance-limited breadth first traversal over nodeitems,
 * edgeitems, or both, using any number of traversal "roots".
 * 
 * This modification of the BreadthFirstIterator class from the prefuse library
 * acts on visual items and takes the visibility of the items into account if
 * requested.
 * 
 * By default, invisible items are excluded, which also has the effect that
 * invisible edges are not traversed.
 * 
 * @author <a href="http://jheer.org">jeffrey heer</a> (original
 *         BreadthFirstIterator)
 * @author <a href="http://goosebumps4all.net">martin dudek</a> (made it
 *         visual)
 */

public class VisualBreadthFirstIterator implements Iterator {

    protected Queue m_queue = new Queue();

    protected int m_depth;

    protected int m_traversal;

    protected boolean m_includeNodeItems;

    protected boolean m_includeEdgeItems;

    private boolean excludeInvisible = true;

    /**
         * Create an uninitialized BreadthFirstIterator. Use the
         * {@link #init(Object, int, int)} method to initialize the iterator.
         */
    public VisualBreadthFirstIterator() {
	// do nothing, requires init call
    }

    /**
         * Create a new BreadthFirstIterator starting from the given source node
         * item.
         * 
         * @param ni
         *                the source node item from which to begin the traversal
         * @param depth
         *                the maximum graph distance to traverse
         * @param traversal
         *                the traversal type, one of
         *                {@link prefuse.Constants#NODE_TRAVERSAL},
         *                {@link prefuse.Constants#EDGE_TRAVERSAL}, or
         *                {@link prefuse.Constants#NODE_AND_EDGE_TRAVERSAL}
         */
    public VisualBreadthFirstIterator( NodeItem ni, int depth,
	     int traversal) {
	init(new NodeItem[] { ni }, depth, traversal);
    }

    /**
         * Create a new BreadthFirstIterator starting from the given source node
         * items.
         * 
         * @param it
         *                an Iterator over the source node items from which to
         *                begin the traversal
         * @param depth
         *                the maximum graph distance to traverse
         * @param traversal
         *                the traversal type, one of
         *                {@link prefuse.Constants#NODE_TRAVERSAL},
         *                {@link prefuse.Constants#EDGE_TRAVERSAL}, or
         *                {@link prefuse.Constants#NODE_AND_EDGE_TRAVERSAL}
         */
    public VisualBreadthFirstIterator( Iterator it, int depth,
	     int traversal) {
	init(it, depth, traversal);
    }

    /**
         * Initialize (or re-initialize) this iterator.
         * 
         * @param o
         *                Either a source node item or iterator over source node
         *                items
         * @param depth
         *                the maximum graph distance to traverse
         * @param traversal
         *                the traversal type, one of
         *                {@link prefuse.Constants#NODE_TRAVERSAL},
         *                {@link prefuse.Constants#EDGE_TRAVERSAL}, or
         *                {@link prefuse.Constants#NODE_AND_EDGE_TRAVERSAL}
         */
    public void init( Object o, int depth, int traversal) {
	// initialize the member variables
	m_queue.clear();
	m_depth = depth;
	if ((traversal < 0) || (traversal >= Constants.TRAVERSAL_COUNT)) {
	    throw new IllegalArgumentException("Unrecognized traversal type: "
		    + traversal);
	}
	m_traversal = traversal;
	m_includeNodeItems = ((traversal == Constants.NODE_TRAVERSAL) || (traversal == Constants.NODE_AND_EDGE_TRAVERSAL));
	m_includeEdgeItems = ((traversal == Constants.EDGE_TRAVERSAL) || (traversal == Constants.NODE_AND_EDGE_TRAVERSAL));

	// seed the queue
	// TODO: clean this up? (use generalized iterator?)
	if (m_includeNodeItems) {
	    if (o instanceof NodeItem) {
		if (checkVisible((NodeItem) o)) {
		    m_queue.add(o, 0);
		}
	    } else {
		 Iterator items = (Iterator) o;
		while (items.hasNext()) {
		     NodeItem ni = (NodeItem) items.next();
		    if (checkVisible(ni)) {
			m_queue.add(ni, 0);
		    }
		}
	    }
	} else {
	    if ((o instanceof NodeItem) && checkVisible((NodeItem) o)) {
		 NodeItem ni = (NodeItem) o;
		m_queue.visit(ni, 0);
		 Iterator edgeItems = getEdges(ni);
		while (edgeItems.hasNext()) {
		     EdgeItem ei = (EdgeItem) edgeItems.next();
		    if (!checkVisible(ei)) {
			continue;
		    }
		     NodeItem nni = ei.getAdjacentItem(ni);
		    if (!checkVisible(nni)) {
			continue;
		    }
		    m_queue.visit(nni, 1);
		    if (m_queue.getDepth(ei) < 0) {
			m_queue.add(ei, 1);
		    }
		}
	    } else {
		 Iterator items = (Iterator) o;
		while (items.hasNext()) {
		    // TODO: graceful error handling when non-node in set?
		     NodeItem ni = (NodeItem) items.next();
		    if (!checkVisible(ni)) {
			continue;
		    }
		    m_queue.visit(ni, 0);
		     Iterator edgeItems = getEdges(ni);
		    while (edgeItems.hasNext()) {
			 EdgeItem ei = (EdgeItem) edgeItems.next();
			if (!checkVisible(ei)) {
			    continue;
			}
			 NodeItem nni = ei.getAdjacentItem(ni);
			if (!checkVisible(nni)) {
			    continue;
			}
			m_queue.visit(nni, 1);
			if (m_queue.getDepth(ei) < 0) {
			    m_queue.add(ei, 1);
			}
		    }
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

    /**
         * @see java.util.Iterator#hasNext()
         */
    public boolean hasNext() {
	return !m_queue.isEmpty();
    }

    /**
         * Determines which edges are traversed for a given node.
         * 
         * @param n
         *                a node
         * @return an iterator over edges incident on the node
         */
    protected Iterator getEdges( NodeItem ni) {
	return ni.edges();
    }

    /**
         * Get the traversal depth at which a particular VisualItem was
         * encountered.
         * 
         * @param t
         *                the VisualItem to lookup
         * @return the traversal depth of the VisualItem, or -1 if the
         *         VisualItem has not been visited by the traversal.
         */
    public int getDepth( VisualItem t) {
	return m_queue.getDepth(t);
    }

    /**
         * @see java.util.Iterator#next()
         */
    public Object next() {
	VisualItem t = (VisualItem) m_queue.removeFirst();

	switch (m_traversal) {

	case Constants.NODE_TRAVERSAL:
	case Constants.NODE_AND_EDGE_TRAVERSAL:
	    for (; true; t = (VisualItem) m_queue.removeFirst()) {
		if (t instanceof EdgeItem) {
		    return t;
		} else {
		     NodeItem ni = (NodeItem) t;
		    if (!checkVisible(ni)) {
			continue;
		    }

		     int d = m_queue.getDepth(ni);

		    if (d < m_depth) {
			 int dd = d + 1;
			 Iterator edgeItems = getEdges(ni);
			while (edgeItems.hasNext()) {
			     EdgeItem ei = (EdgeItem) edgeItems.next();
			    if (!checkVisible(ei)) {
				continue;
			    }
			     NodeItem vi = ei.getAdjacentItem(ni);
			    if (!checkVisible(vi)) {
				continue;
			    }
			    if (m_includeEdgeItems
				    && (m_queue.getDepth(ei) < 0)) {
				m_queue.add(ei, dd);
			    }
			    if (m_queue.getDepth(vi) < 0) {
				m_queue.add(vi, dd);
			    }
			}
		    } else if (m_includeEdgeItems && (d == m_depth)) {
			 Iterator edgeItems = getEdges(ni);
			while (edgeItems.hasNext()) {
			     EdgeItem ei = (EdgeItem) edgeItems.next();
			    if (!checkVisible(ei)) {
				continue;
			    }
			     NodeItem vi = ei.getAdjacentItem(ni);
			    if (!checkVisible(vi)) {
				continue;
			    }
			     int dv = m_queue.getDepth(vi);
			    if ((dv > 0) && (m_queue.getDepth(ei) < 0)) {
				m_queue.add(ei, Math.min(d, dv));
			    }
			}
		    }
		    return ni;
		}
	    }

	case Constants.EDGE_TRAVERSAL:
	     EdgeItem ei = (EdgeItem) t;

	     NodeItem ui = ei.getSourceItem();
	     NodeItem vi = ei.getTargetItem();
	     int du = m_queue.getDepth(ui);
	     int dv = m_queue.getDepth(vi);

	    if (du != dv) {
		 NodeItem ni = (dv > du ? vi : ui);
		 int d = Math.max(du, dv);

		if (d < m_depth) {
		     int dd = d + 1;
		     Iterator edgeItems = getEdges(ni);
		    while (edgeItems.hasNext()) {
			 EdgeItem eei = (EdgeItem) edgeItems.next();
			if (!checkVisible(eei)) {
			    continue;
			}
			if (m_queue.getDepth(eei) >= 0) {
			    continue; // already visited
			}

			 NodeItem nni = eei.getAdjacentItem(ni);
			m_queue.visit(nni, dd);
			m_queue.add(eei, dd);
		    }
		}
	    }
	    return ei;

	default:
	    throw new IllegalStateException();
	}
    }

    /**
         * Indicates if invisible items are excluded
         * 
         * @return true if invisible items are excluded, false otherwise.
         */
    public boolean isHighlightWithInvisibleEdge() {
	return excludeInvisible;
    }

    /**
         * Determines if invisible items should be excluded
         * 
         * @param excludeInvisible
         *                assign true if invisible items should be excluded,
         *                false otherwise highlightWithInvisibleEdge assign true
         *                if neighbors with invisible edges should still get
         *                highlighted, false otherwise.
         */
    public void setExcludeInvisible( boolean excludeInvisible) {
	this.excludeInvisible = excludeInvisible;
    }

    /**
         * checks if the given visual item should be taking into account; this
         * is the case when the item is visible or if also invisible items
         * should be used
         * 
         * @param vi
         *                the visual item to be checked
         * @return
         */
    private boolean checkVisible( VisualItem vi) {
	return vi.isVisible() || (!excludeInvisible);
    }

} // end of class BreadthFirstIterator
