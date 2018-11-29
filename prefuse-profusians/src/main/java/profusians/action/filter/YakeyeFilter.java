package profusians.action.filter;

/**
 * This class is an extension of the
 * {@link profusians.action.filter.GraphDirectedStepsDistanceFilter} class,
 * covering the specific case of setting the successorStepsDistance one point
 * higher than the predecessorStepsDistance. Please check the documentation of
 * the {@link profusians.action.filter.GraphDirectedStepsDistanceFilter} class
 * for further informations about these two distance parameter.
 * 
 * 
 * @author <a href="http://goosebumps4all.net">martin dudek</a>
 * 
 */

public class YakeyeFilter extends GraphDirectedStepsDistanceFilter {

    /**
         * Create a new YakeyeFilter that processes the given data group and
         * uses the given graph distance as successorStepsDistance. The
         * predecessorStepsDistance will be set one point lower.
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
         *                successorStepsDistance.
         * 
         */

    public YakeyeFilter( String group, int distance) {
	super(group, distance, distance - 1);
    }

    /**
         * Create a new YakeyeFilter that processes the given data group and
         * uses the given graph distance as successorStepsDistance. The
         * predecessorStepsDistance will be set one point lower.
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
         *                successorStepsDistance.
         * 
         */

    public YakeyeFilter( String group, String source,
	     int distance) {
	super(group, source, distance, distance - 1);
    }
}
