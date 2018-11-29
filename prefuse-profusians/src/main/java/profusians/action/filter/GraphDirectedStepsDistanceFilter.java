package profusians.action.filter;

import java.util.Iterator;

import prefuse.Constants;
import prefuse.Visualization;
import prefuse.action.GroupAction;
import prefuse.data.Graph;
import prefuse.data.expression.Predicate;
import prefuse.data.tuple.TupleSet;
import prefuse.data.util.FilterIterator;
import prefuse.util.PrefuseLib;
import prefuse.visual.VisualItem;
import prefuse.visual.expression.InGroupPredicate;
import profusians.data.util.BreadthFirstIteratorDirectedSteps;

/**
 * 
 * Filter Action for directed graphs. All items within a specified directed
 * graph distance from a set of focus items are set visible; all other items
 * will be set to invisible.
 * 
 * The distance is specified by the two parameters successorStepsDistance and
 * predecessorStepsDistance. Nodes which are set visible must be reachable from
 * a set of focus items by traversing at most successorStepsDistance times to a
 * successor of a node and at most predecessorStepsDistance times to predecessor
 * of a node.
 * 
 * By default, the successorStepsDistance and predecessorStepsDistance is set 1
 * and the {@link prefuse.Visualization#FOCUS_ITEMS} group will be used as the
 * source nodes from which to measure the distance.
 * 
 * @author <a href="http://goosebumps4all.net">martin dudek</a>
 * @author <a href="http://jheer.org">jeffrey heer</a>
 * 
 */

public class GraphDirectedStepsDistanceFilter extends GroupAction {

    protected int m_successorStepsDistance;

    protected int m_predecessorStepsDistance;

    protected String m_sources;

    protected Predicate m_groupP;

    protected BreadthFirstIteratorDirectedSteps m_bfs;

    /**
         * Create a new GraphDirectedStepsDistanceFilter that processes the
         * given data group and uses a directed graph successorStepsDistance and
         * predecessorStepsDistance of 1.
         * 
         * By default, the {@link prefuse.Visualization#FOCUS_ITEMS} group will
         * be used as the source nodes from which to measure the distance.
         * 
         * @param group
         *                the group to process. This group should resolve to a
         *                Graph instance, otherwise exceptions will be thrown
         *                when this Action is run.
         */

    public GraphDirectedStepsDistanceFilter( String group) {
	this(group, 1, 1);
    }

    /**
         * Create a new GraphDirectedStepsDistanceFilter that processes the
         * given data group and uses the given directed graph
         * successorStepsDistance and predecessorStepsDistance. By default, the
         * {@link prefuse.Visualization#FOCUS_ITEMS} group will be used as the
         * source nodes from which to measure the distance.
         * 
         * @param group
         *                the group to process. This group should resolve to a
         *                Graph instance, otherwise exceptions will be thrown
         *                when this Action is run.
         * @param successorStepsDistance
         *                the directed graph successorStepsDistance within which
         *                items will be visible.
         * @param predecessorStepsDistance
         *                the directed graph predecessorStepsDistance within
         *                which items will be visible.
         */

    public GraphDirectedStepsDistanceFilter( String group,
	     int successorStepsDistance, int predecessorStepsDistance) {
	this(group, Visualization.FOCUS_ITEMS, successorStepsDistance,
		predecessorStepsDistance);
    }

    /**
         * Create a new GraphDirectedStepsDistanceFilter that processes the
         * given data group and uses the given directed graph
         * successorStepsDistance and predecessorStepsDistance.
         * 
         * @param group
         *                the group to process. This group should resolve to a
         *                Graph instance, otherwise exceptions will be thrown
         *                when this Action is run.
         * @param sources
         *                the group to use as source nodes for measuring graph
         *                distance.
         * @param successorStepsDistance
         *                the directed graph successorStepsDistance within which
         *                items will be visible.
         * @param predecessorStepsDistance
         *                the directed graph predecessorStepsDistance within
         *                which items will be visible.
         */

    public GraphDirectedStepsDistanceFilter( String group,
	     String sources, int successorStepsDistance,
	     int predecessorStepsDistance) {
	super(group);
	m_sources = sources;
	m_predecessorStepsDistance = predecessorStepsDistance;
	m_successorStepsDistance = successorStepsDistance;
	m_groupP = new InGroupPredicate(PrefuseLib.getGroupName(group,
		Graph.NODES));
	m_bfs = new BreadthFirstIteratorDirectedSteps();
    }

    /**
         * Return the directed graph predecessorStepsDistance threshold used by
         * this filter.
         * 
         * @return directed graph predecessorStepsDistance threshold
         */

    public int getPredecessorStepsDistance() {
	return m_predecessorStepsDistance;
    }

    /**
         * Set the directed graph predecessorStepsDistance threshold used by
         * this filter.
         * 
         * @param predecessorStepsDistance
         *                the directed graph predecessorStepsDistance threshold
         *                to use
         */

    public void setPredecessorStepsDistance( int predecessorStepsDistance) {
	m_predecessorStepsDistance = predecessorStepsDistance;
    }

    /**
         * Return the directed graph successorStepsDistance threshold used by
         * this filter.
         * 
         * @return directed graph successorStepsDistance threshold
         */

    public int getSuccessorStepsDistance() {
	return m_successorStepsDistance;
    }

    /**
         * Set the directed graph successorStepsDistance threshold used by this
         * filter.
         * 
         * @param successorStepsDistance
         *                the directed graph predecessorStepsDistance threshold
         *                to use
         */

    public void setSuccessorStepsDistance( int successorStepsDistance) {
	m_successorStepsDistance = successorStepsDistance;
    }

    /**
         * Get the name of the group to use as source nodes for measuring graph
         * distance. These form the roots from which the graph distance is
         * measured.
         * 
         * @return the source data group
         */

    public String getSources() {
	return m_sources;
    }

    /**
         * Set the name of the group to use as source nodes for measuring graph
         * distance. These form the roots from which the graph distance is
         * measured.
         * 
         * @param sources
         *                the source data group
         */

    public void setSources( String sources) {
	m_sources = sources;
    }

    /**
         * @see prefuse.action.GroupAction#run(double)
         */

    public void run( double frac) {
	// mark the items
	Iterator items = m_vis.visibleItems(m_group);
	while (items.hasNext()) {
	     VisualItem item = (VisualItem) items.next();
	    item.setDOI(Constants.MINIMUM_DOI);
	}

	// set up the graph traversal
	 TupleSet src = m_vis.getGroup(m_sources);
	 Iterator srcs = new FilterIterator(src.tuples(), m_groupP);
	m_bfs.init(srcs, m_successorStepsDistance, m_predecessorStepsDistance,
		Constants.NODE_AND_EDGE_TRAVERSAL);

	// traverse the graph
	while (m_bfs.hasNext()) {
	     VisualItem item = (VisualItem) m_bfs.next();
	    int ds = m_bfs.getSuccessorDepth(item);
	     int dp = m_bfs.getPredecessorDepth(item);
	    PrefuseLib.updateVisible(item, true);
	    item.setDOI(-ds - dp);

	    item.setExpanded(ds < m_successorStepsDistance);

	}

	// mark unreached items
	items = m_vis.visibleItems(m_group);
	while (items.hasNext()) {
	     VisualItem item = (VisualItem) items.next();
	    if (item.getDOI() == Constants.MINIMUM_DOI) {
		PrefuseLib.updateVisible(item, false);
		item.setExpanded(false);
	    }
	}
    }
}
