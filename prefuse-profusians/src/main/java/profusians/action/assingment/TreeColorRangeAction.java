package profusians.action.assingment;

import java.util.HashMap;

import prefuse.action.GroupAction;
import prefuse.data.Graph;
import prefuse.data.Tree;
import prefuse.data.tuple.TupleSet;
import prefuse.util.ColorLib;
import prefuse.visual.NodeItem;
import profusians.util.TreeLib;

/**
 * Assignment Action that assigns color values within a given color range to the
 * node items of a tree.
 * 
 * Initially, the root node is assigned with the interpolated middle color of
 * the given color range. It children are then associated with a distinct color
 * range from within the given color range and assigned again to the
 * interpolated middle color of this range. This procedures continues up to the
 * leaves of the tree.
 * 
 * There are two possible policies available in which way color ranges are
 * associated to the children of a node.
 * 
 * COLORRANGEPOLICY_EQUALPERLEAVE: (default) The size of the range is equal for each 
 * leave of the tree. 
 * 
 * COLORRANGEPOLICY_EQUALPERCHILD: The size of the range is equal for each child
 * of the node.
 * 
 * 
 * @author <a href="http://goosebumps4all.net">martin dudek</a>
 */

public class TreeColorRangeAction extends GroupAction {

    public  final static int COLORRANGEPOLICY_EQUALPERLEAVE = 1;

    public   final static int COLORRANGEPOLICY_EQUALPERCHILD = 2;

    private int m_startColor;

    private int m_endColor;

    private int m_rangePolicy;

    /**
         * Creates a new TreeColorRAngeAction
         * 
         * @param group
         *                the name of the group to process
         * @param startColor
         *                the start color of the color range
         * @param endColor
         *                the end color of the color range
         */
    public TreeColorRangeAction( String group, int startColor,
	     int endColor) {
	this(group, startColor, endColor, COLORRANGEPOLICY_EQUALPERLEAVE);
    }

    /**
         * Creates a new TreeColorRAngeAction
         * 
         * @param group
         *                the name of the group to process
         * @param startColor
         *                the start color of the color range
         * @param endColor
         *                the end color of the color range
         * @param policy
         *                the color range policy to be used by this action
         */

    public TreeColorRangeAction( String group, int startColor,
	     int endColor, int policy) {
	super(group);
	m_startColor = startColor;
	m_endColor = endColor;
	m_rangePolicy = policy;
    }

    public void run( double frac) {
	 NodeItem ni = getLayoutRoot();
	switch (m_rangePolicy) {
	case COLORRANGEPOLICY_EQUALPERCHILD:
	    setColorEqualPerChild(ni, m_startColor, m_endColor);
	    break;
	case COLORRANGEPOLICY_EQUALPERLEAVE:
	     HashMap subtreeSizeMap = TreeLib.getSubtreeSizeMap(ni);
	    setColorSubtreeSize(ni, m_startColor, m_endColor, subtreeSizeMap);
	    break;
	default:
	    throw new IllegalArgumentException(
		    "Unrecognized color range policy value: " + m_rangePolicy);
	}

    }

    /**
         * Setting the color for this node item. Override this method if you
         * want to change something else than the text color ...
         * 
         * @param ni
         *                the node item
         * @param color
         *                the color according to the choosen color range policy
         */

    public void setColor( NodeItem ni, int color) {
	ni.setStartTextColor(ni.getEndTextColor());
	ni.setTextColor(color);
	ni.setEndTextColor(color);
    }

    /**
         * Setting the color range policy. Possible choices:
         * 
         * TreeColorRangeAction.COLORRANGEPOLICY_EQUALPERCHILD The color range
         * is divided equally per child
         * 
         * TreeColorRangeAction.COLORRANGEPOLICY_EQUALPERLEAVE The color range is
         * devided equally per leave
         * 
         * @param policy
         *                the choosen policy
         */

    public void setColorRangePolicy( int policy) {
	m_rangePolicy = policy;
    }

    /**
         * Gets the color range policy used by this action
         * 
         * @return the color range policy
         */
    public int getColorRangePolicy() {
	return m_rangePolicy;
    }

    private void setColorSubtreeSize( NodeItem ni, int startColor,
	     int endColor, HashMap sizeMap) {
	setColor(ni, ColorLib.interp(startColor, endColor, 0.5));
	 int numberOfChildren = ni.getChildCount();

	int startSize = 0;
	 float totalSize = ((Integer) sizeMap.get(ni)).floatValue(); // float
	// for
	// later
	// division

	NodeItem ci = (NodeItem) ni.getFirstChild();
	for (float f = 0.f; f < numberOfChildren; f++, ci = (NodeItem) ci
		.getNextSibling()) {
	     int step = ((Integer) sizeMap.get(ci)).intValue();
	    setColorSubtreeSize(ci, ColorLib.interp(startColor, endColor,
		    startSize / totalSize), ColorLib.interp(startColor,
		    endColor, (startSize + step) / totalSize), sizeMap);
	    startSize += step;
	}
    }

    private void setColorEqualPerChild( NodeItem ni, int startColor,
	     int endColor) {
	setColor(ni, ColorLib.interp(startColor, endColor, 0.5));
	 int numberOfChildren = ni.getChildCount();
	NodeItem ci = (NodeItem) ni.getFirstChild();
	for (float f = 0.f; f < numberOfChildren; f++, ci = (NodeItem) ci
		.getNextSibling()) {
	    setColorEqualPerChild(ci, ColorLib.interp(startColor, endColor, f
		    / numberOfChildren), ColorLib.interp(startColor, endColor,
		    (f + 1) / numberOfChildren));
	}
    }

    private NodeItem getLayoutRoot() {

	 TupleSet ts = m_vis.getGroup(m_group);
	if (ts instanceof Graph) {
	     Tree tree = ((Graph) ts).getSpanningTree();
	    return (NodeItem) tree.getRoot();
	} else {
	    throw new IllegalStateException("This action's data group does"
		    + "not resolve to a Graph instance.");
	}
    }
}
