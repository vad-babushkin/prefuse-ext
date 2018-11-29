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
import profusians.data.util.BreadthFirstIteratorDirectedSpheres;

/**
 * 
 * Filter Action for directed graphs. All items within a specified directed
 * graph distance from a set of focus items are set visible; all other items
 * will be set to invisible.
 * 
 * The distance is specified by the two parameters successorSphereDistance and
 * predecessorSphereDistance. Nodes which are set visible must be reachable from
 * a set of focus items by the follwing traversing rules:
 * 
 * Within the graph distance specified by the successorSphereDistance parameter,
 * edges from a node to a successor can be traversed. (successor sphere) Within
 * the graph distance specified by the predecessorSphereDistance parameter,
 * edges from a node to a predecessor can be traversed. (predecessor sphere)
 * 
 * By default, the successorSphereDistance and predecessorSphereDistance is set
 * 1 and the {@link prefuse.Visualization#FOCUS_ITEMS} group will be used as the
 * source nodes from which to measure the distance.
 * 
 * @author <a href="http://goosebumps4all.net">martin dudek</a>
 * @author <a href="http://jheer.org">jeffrey heer</a>
 * 
 */

public class GraphDirectedSpheresDistanceFilter extends GroupAction {

    protected int m_successorSphereDistance;

    protected int m_predecessorSphereDistance;

    protected int m_distance;

    protected String m_sources;

    protected Predicate m_groupP;

    protected BreadthFirstIteratorDirectedSpheres m_bfs;

    /**
         * Create a new GraphDirectedSpheresDistanceFilter that processes the
         * given data group and uses a directed graph successorSphereDistance
         * and predecessorSphereDistance of 1.
         * 
         * By default, the {@link prefuse.Visualization#FOCUS_ITEMS} group will
         * be used as the source nodes from which to measure the distance.
         * 
         * @param group
         *                the group to process. This group should resolve to a
         *                Graph instance, otherwise exceptions will be thrown
         *                when this Action is run.
         */

    public GraphDirectedSpheresDistanceFilter( String group) {
	this(group, 1, 1);
    }

    /**
         * Create a new GraphDirectedSpheresDistanceFilter that processes the
         * given data group and uses the given directed graph
         * successorSphereDistance and predecessorSphereDistance. By default,
         * the {@link prefuse.Visualization#FOCUS_ITEMS} group will be used as
         * the source nodes from which to measure the distance.
         * 
         * @param group
         *                the group to process. This group should resolve to a
         *                Graph instance, otherwise exceptions will be thrown
         *                when this Action is run.
         * @param successorSphereDistance
         *                the directed graph successorSphereDistance within
         *                which items will be visible.
         * @param predecessorSphereDistance
         *                the directed graph predecessorSphereDistance within
         *                which items will be visible.
         */

    public GraphDirectedSpheresDistanceFilter( String group,
	     int successorSphereDistance,
	     int predecessorSphereDistance) {
	this(group, Visualization.FOCUS_ITEMS, successorSphereDistance,
		predecessorSphereDistance);
    }

    /**
         * Create a new GraphDirectedSpheresDistanceFilter that processes the
         * given data group and uses the given directed graph
         * successorSphereDistance and predecessorSphereDistance.
         * 
         * @param group
         *                the group to process. This group should resolve to a
         *                Graph instance, otherwise exceptions will be thrown
         *                when this Action is run.
         * @param sources
         *                the group to use as source nodes for measuring graph
         *                distance.
         * @param successorSphereDistance
         *                the directed graph successorSphereDistance within
         *                which items will be visible.
         * @param predecessorSphereDistance
         *                the directed graph predecessorSphereDistance within
         *                which items will be visible.
         */

    public GraphDirectedSpheresDistanceFilter( String group,
	     String sources, int successorSphereDistance,
	     int predecessorSphereDistance) {
	super(group);
	m_sources = sources;
	m_predecessorSphereDistance = predecessorSphereDistance;
	m_successorSphereDistance = successorSphereDistance;

	m_distance = Math.max(successorSphereDistance,
		predecessorSphereDistance);

	m_groupP = new InGroupPredicate(PrefuseLib.getGroupName(group,
		Graph.NODES));
	m_bfs = new BreadthFirstIteratorDirectedSpheres();
    }

    /**
         * Return the directed graph predecessorSphereDistance threshold used by
         * this filter.
         * 
         * @return directed graph predecessorSphereDistance threshold
         */

    public int getPredecessorSphereDistance() {
	return m_predecessorSphereDistance;
    }

    /**
         * Set the directed graph predecessorSphereDistance threshold used by
         * this filter.
         * 
         * @param predecessorSphereDistance
         *                the directed graph predecessorSphereDistance threshold
         *                to use
         */

    public void setPredecessorSphereDistance( int predecessorSphereDistance) {
	m_predecessorSphereDistance = predecessorSphereDistance;
    }

    /**
         * Return the directed graph successorSphereDistance threshold used by
         * this filter.
         * 
         * @return directed graph successorSphereDistance threshold
         */

    public int getSuccessorDistance() {
	return m_successorSphereDistance;
    }

    /**
         * Set the directed graph successorSphereDistance threshold used by this
         * filter.
         * 
         * @param successorSphereDistance
         *                the directed graph predecessorSphereDistance threshold
         *                to use
         */

    public void setSuccessorDistance( int successorSphereDistance) {
	m_successorSphereDistance = successorSphereDistance;
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
	m_bfs.init(srcs, m_successorSphereDistance,
		m_predecessorSphereDistance, Constants.NODE_AND_EDGE_TRAVERSAL);

	// traverse the graph
	while (m_bfs.hasNext()) {
	     VisualItem item = (VisualItem) m_bfs.next();
	    int d = m_bfs.getDepth(item);

	    PrefuseLib.updateVisible(item, true);
	    item.setDOI(-d);
	    item.setExpanded(d < m_distance);
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

} // end of class GraphDistanceFilter

