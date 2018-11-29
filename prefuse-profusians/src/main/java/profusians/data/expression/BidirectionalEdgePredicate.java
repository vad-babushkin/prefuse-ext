package profusians.data.expression;

import java.util.HashMap;
import java.util.Iterator;

import prefuse.data.Edge;
import prefuse.data.Node;
import prefuse.data.Tuple;
import prefuse.data.expression.AbstractPredicate;

/**
 * Predicate for edges of directed graphs which determines if an edge is
 * bidirectional. This predicate uses internally a cache wich has to be cleared
 * with the method clearCache() if edges are added or removed from the graph.
 * Alternativley this cache can be disabled alltogether.
 * 
 * @author <a href="http://goosebumps4all.net">martin dudek</a>
 */
public class BidirectionalEdgePredicate extends AbstractPredicate {

    private HashMap m_cache;

    private boolean m_useCache = true;

    /**
         * Creates a new BidrectionalEdgePredicate.
         * 
         */
    public BidirectionalEdgePredicate() {
	this(true);
    }

    /**
         * Creates a new BidrectionalEdgePredicate.
         * 
         * @param useCache
         *                If true, then this predicate uses an internal cache
         *                which has to be cleared whenever an edge is added or
         *                removed from the graph. If false, no cache is used.
         */
    public BidirectionalEdgePredicate( boolean useCache) {
	m_useCache = useCache;
	if (useCache) {
	    m_cache = new HashMap();
	}
    }

    public boolean getBoolean( Tuple tpl) {

	if (!(tpl instanceof Edge)) {
	    return false;
	}
	if (m_useCache && m_cache.containsKey(tpl)) {
	    return (Boolean) m_cache.get(tpl) == Boolean.TRUE;
	}

	 Edge tplAsEdge = (Edge) tpl;

	 Node sourceNode = tplAsEdge.getSourceNode();
	 Node targetNode = tplAsEdge.getTargetNode();

	boolean bothDir = false;

	 Iterator iter = sourceNode.inEdges();

	while (iter.hasNext() && !bothDir) {
	     Edge aEdge = (Edge) iter.next();
	    if (aEdge.getSourceNode() == targetNode) {
		bothDir = true;
	    }
	}
	if (m_useCache) {
	    m_cache.put(tpl, Boolean.valueOf(bothDir));
	}
	return bothDir;
    }

    /**
         * Clears the internal cache used by the predicate to store edge type
         * information. This cache has to be cleared by this method whenever an
         * edge is added or removed from the graph.
         * 
         */
    public void clearCache() {
	m_cache.clear();
    }

    /**
         * Sets the internal cache active/inactive.
         * 
         * @param active
         *                If true, the internal cache is activated, otherwise
         *                deactivated.
         */

    public void setCacheActive( boolean active) {
	m_useCache = active;
	if (active) {
	    m_cache = new HashMap();
	}
    }
}
