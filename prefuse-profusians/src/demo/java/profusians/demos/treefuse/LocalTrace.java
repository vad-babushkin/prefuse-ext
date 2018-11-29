package profusians.demos.treefuse;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import prefuse.visual.NodeItem;

/**
 * Utility class to keep track of which nodeitem among its siblings was visited
 * last time. For each nodeitem a trace nodeitem from among its children can be
 * stored. The class doesn't check if the nodes are the same tree nor if they
 * are in a parent child relationship, it just stored whatever is given.
 * 
 * @author <a href="http://goosebumps4all.net">martin dudek</a>
 * 
 */
public class LocalTrace {

    HashMap lastVisitedChild;

    public LocalTrace() {

	lastVisitedChild = new HashMap();
	// TODO Auto-generated constructor stub
    }

    /**
         * store a trace for the given node
         * 
         * @param parent
         *                the parent node for which a trace should be stored
         * @param visitedChild
         *                the trace to be stored
         */

    public void storeTrace(NodeItem parent, NodeItem visitedChild) {
	lastVisitedChild.put(parent, visitedChild);
    }

    /**
         * get the trace stored for the given nodeitem if any
         * 
         * @param ni
         * @return
         */

    public NodeItem getTrace(NodeItem ni) {
	return (NodeItem) lastVisitedChild.get(ni);
    }

    /**
         * remove the trace stored for the given nodeitem
         * 
         * @param ni
         */
    public void removeTraceOf(NodeItem ni) {

	lastVisitedChild.remove(ni);

    }

    /**
         * remove the trace stored for all nodeitems specified through the
         * iterator
         * 
         * @param iter
         */

    public void removeTraceOf(Iterator iter) {
	while (iter.hasNext()) {
	    removeTraceOf((NodeItem) iter.next());
	}
    }

    public void removeTracedAs(NodeItem ni) {
	NodeItem removeKey = null;
	Iterator keyIterator = lastVisitedChild.keySet().iterator();
	while (keyIterator.hasNext()) {
	    NodeItem aKey = (NodeItem) keyIterator.next();
	    if (lastVisitedChild.get(aKey) == ni) {
		removeKey = aKey;
	    }
	}

	if (removeKey != null) {
	    lastVisitedChild.remove(removeKey);
	}

    }

    public void removeTracedAs(Iterator iter) {
	HashSet removeKeys = new HashSet();
	while (iter.hasNext()) {
	    NodeItem aTracedItem = (NodeItem) iter.next();
	    Iterator keyIterator = lastVisitedChild.keySet().iterator();
	    while (keyIterator.hasNext()) {
		NodeItem aKey = (NodeItem) keyIterator.next();
		if (lastVisitedChild.get(aKey) == aTracedItem) {
		    removeKeys.add(aKey);
		}
	    }
	}
	Iterator invalidKeys = removeKeys.iterator();
	while (invalidKeys.hasNext()) {
	    lastVisitedChild.remove(invalidKeys.next());
	}

    }

}
