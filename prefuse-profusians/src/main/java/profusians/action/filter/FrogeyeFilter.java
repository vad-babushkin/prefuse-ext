package profusians.action.filter;

/**
 * This class is an extension of the
 * {@link profusians.action.filter.GraphDirectedSpheresDistanceFilter} class,
 * covering the specific case of setting the successorSphereDistance one point
 * higher than the predecessorSphereDistance. Please check the documentation of
 * the {@link profusians.action.filter.GraphDirectedSpheresDistanceFilter} class
 * for further informations about these two distance parameter.
 * 
 * @author <a href="http://goosebumps4all.net">martin dudek</a>
 * 
 */

public class FrogeyeFilter extends GraphDirectedSpheresDistanceFilter {

    /**
         * Creates a new FrogeyeFilter that processes the given data group and
         * uses the given graph distance as successorSphereDistance. The
         * predecessorSphereDistance will be set one point lower.
         * 
         * By default, the {@link prefuse.Visualization#FOCUS_ITEMS} group will
         * be used as the source nodes from which to measure the distance.
         * 
         * @param group
         *                the group to process. This group should resolve to a
         *                Graph instance, otherwise exceptions will be thrown
         *                when this Action is run.
         * @param distance
         *                the graph distance within which items will be visible.
         *                The given value will be uses as the
         *                successorSphereDistance.
         * 
         */

    public FrogeyeFilter( String group, int distance) {
	super(group, distance, distance - 1);
    }

    /**
         * Create a new FrogeyeFilter that processes the given data group and
         * uses the given graph distance as successorSphereDistance. The
         * predecessorSphereDistance will be set one point lower.
         * 
         * By default, the {@link prefuse.Visualization#FOCUS_ITEMS} group will
         * be used as the source nodes from which to measure the distance.
         * 
         * @param group
         *                the group to process. This group should resolve to a
         *                Graph instance, otherwise exceptions will be thrown
         *                when this Action is run.
         * @param source
         *                the group to use as source nodes for measuring graph
         *                distance.
         * @param distance
         *                the graph distance within which items will be visible.
         *                The given value will be used as the
         *                successorSphereDistance.
         * 
         */

    public FrogeyeFilter( String group, String source,
	     int distance) {
	super(group, source, distance, distance - 1);
    }
}
