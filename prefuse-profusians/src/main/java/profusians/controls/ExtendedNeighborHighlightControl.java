package profusians.controls;

import java.awt.event.MouseEvent;

import prefuse.Constants;
import prefuse.action.assignment.ColorAction;
import prefuse.controls.ControlAdapter;
import prefuse.data.Graph;
import prefuse.util.ColorLib;
import prefuse.visual.NodeItem;
import prefuse.visual.VisualItem;
import profusians.visual.util.VisualBreadthFirstIterator;

/**
 * Modification of the NeighborHighlightControl of the prefuse library. This
 * control enables the user to specify the distance in which neighbour nodes
 * should be highlighted.
 * 
 * The color action returned by the method getHighlightColorAction() of this
 * class must be used on the nodes for which the highlight flag is set
 * (VisualItem.HIGHLIGHT predicate) to change the node appearance as desired.
 * (Check the related demo coming along with the profusians library to get an
 * idea what that means)
 * 
 * The nodes can be either all highlighted in the same color or in dependence to
 * the distance from the node the mouse points to according to a given color
 * palette.
 * 
 * This color palette can be either specified by an explicit array of colors or
 * through a start and end color, which is interpolated according to the given
 * distance.
 * 
 * By default, invisible items are not taking into account, which includes that
 * nodes connect through invisible edges are not counted as neighbors. This
 * behavior can be changed through the setHighlightWithInvisibleEdge() method.
 * 
 * 
 * </p>
 * 
 * @author <a href="http://goosebumps4all.net">martin dudek</a>
 * @author <a href="http://jheer.org">jeffrey heer</a>
 */
public class ExtendedNeighborHighlightControl extends ControlAdapter {

    private String m_activity = null;

    protected VisualBreadthFirstIterator m_vbfs;

    protected int m_distance;

    private HighlightColorAction m_colorAction;

    private boolean m_dynamicPalette = false;

    private int m_startColor, m_endColor;

    private boolean m_highlightWithInvisibleEdge = false;

    /**
         * Creates a new highlight control with distance 1
         */
    public ExtendedNeighborHighlightControl( int color) {
	this(1, color, color, null);
    }

    /**
         * Creates a new highlight control. All nodes within the given distance
         * are highlighted in the specified color.
         * 
         * @param distance
         *                the graph distance from the node the mouse points to
         *                in which nodes should be highlighted
         * @param color
         *                the highlight color
         */

    public ExtendedNeighborHighlightControl( int distance, int color) {
	this(distance, new int[] { color }, null);
    }

    /**
         * Creates a new highlight control. A color palette if the size of the
         * parameter distance is created by interpolating the given colors
         * startColor and endColor,
         * 
         * All nodes within the given distance are highlighted according to this
         * color palette, the directed neighbours in the startColor, the nodes
         * at maximal distance in the endColor, all nodes inbetween according to
         * other colors of the color palette.
         * 
         * If the distance of the control is changed later on, a new color
         * palette is created using the same start and end color.
         * 
         * @param distance
         *                the graph distance from the node the mouse points to
         *                in which nodes should be highlighted
         * @param startColor
         *                the first color of the color palette
         * @param endColor
         *                the last color of the color palette
         */

    public ExtendedNeighborHighlightControl( int distance,
	     int startColor, int endColor) {
	this(distance, startColor, endColor, null);
    }

    /**
         * Creates a new highlight control that runs the given activity whenever
         * the neighbor highlight changes.
         * 
         * A color palette if the size of the parameter distance is created by
         * interpolating the given colors startColor and endColor,
         * 
         * All nodes within the given distance are highlighted according to this
         * color palette, the directed neighbours in the startColor, the nodes
         * at maximal distance in the endColor, all nodes inbetween according to
         * other colors of the color palette.
         * 
         * If the distance of the control is changed later on, a new color
         * palette is created using the same start and end color.
         * 
         * @param distance
         *                the graph distance from the node the mouse points to
         *                in which nodes should be highlighted
         * @param startColor
         *                the first color of the color palette
         * @param endColor
         *                the last color of the color palette
         * @param activity
         *                the update Activity to run
         */

    public ExtendedNeighborHighlightControl( int distance,
	     int startColor, int endColor, String activity) {

	this(distance, ColorLib.getInterpolatedPalette(distance, startColor,
		endColor), activity);
	m_startColor = startColor;
	m_endColor = endColor;
	m_dynamicPalette = true;
    }

    /**
         * Creates a new highlight control.
         * 
         * All nodes within the given distance are highlighted according to the
         * given color palette, the directed neighbours in the first color of
         * the palette, the nodes with graph distance two in the second color
         * and so on.
         * 
         * The last color of the given color palette is used for all neighbor
         * nodes with a distance larger than the size of the color palette.
         * 
         * @param distance
         *                the graph distance from the node the mouse points to
         *                in which nodes should be highlighted
         * @param colorPalette
         *                the color palette to be used
         */

    public ExtendedNeighborHighlightControl( int distance,
	     int[] colorPalette) {
	this(distance, colorPalette, null);
    }

    /**
         * Creates a new highlight control that runs the given activity whenever
         * the neighbor highlight changes.
         * 
         * All nodes within the given distance are highlighted according to the
         * given color palette, the directed neighbours in the first color of
         * the palette, the nodes with graph distance two in the second colr and
         * so on.
         * 
         * The last color of the given color palette is used for all neighbor
         * nodes with a distance bigger than the size of the color palette.
         * 
         * @param distance
         *                the graph distance from the node the mouse points to
         *                in which nodes should be highlighted
         * @param colorPalette
         *                the color palette to be used
         * @param activity
         *                the update Activity to run
         */

    public ExtendedNeighborHighlightControl( int distance,
	     int[] colorPalette, String activity) {

	m_distance = distance;
	m_vbfs = new VisualBreadthFirstIterator();

	m_activity = activity;

	m_colorAction = new HighlightColorAction(distance, colorPalette);

    }

    /**
         * @see prefuse.controls.Control#itemEntered(prefuse.visual.VisualItem,
         *      java.awt.event.MouseEvent)
         */
    public void itemEntered( VisualItem item, MouseEvent e) {
	if (item instanceof NodeItem) {
	    setNeighborHighlight((NodeItem) item, true);
	}
    }

    /**
         * @see prefuse.controls.Control#itemExited(prefuse.visual.VisualItem,
         *      java.awt.event.MouseEvent)
         */
    public void itemExited( VisualItem item, MouseEvent e) {
	if (item instanceof NodeItem) {
	    setNeighborHighlight((NodeItem) item, false);
	}
    }

    /**
         * Set the highlighted state of the neighbors of a node.
         * 
         * @param ni
         *                the node under consideration
         * @param state
         *                the highlighting state to apply to neighbors
         */
    protected void setNeighborHighlight( NodeItem ni, boolean state) {

	m_vbfs.init(ni, m_distance, Constants.NODE_AND_EDGE_TRAVERSAL);

	m_vbfs.setExcludeInvisible(!m_highlightWithInvisibleEdge);

	while (m_vbfs.hasNext()) {
	     VisualItem item = (VisualItem) m_vbfs.next();
	    int d = m_vbfs.getDepth(item);
	    item.setHighlighted(state && (d > 0));
	    item.setDOI(-d);
	}

	if (m_activity != null) {
	    ni.getVisualization().run(m_activity);
	}
    }

    /**
         * returns the color action which must be used to highlight the
         * neighbors
         * 
         * @return
         */

    public ColorAction getHighlightColorAction() {
	return m_colorAction;
    }

    /**
         * sets the graph distance in which neighbors should be highlighted
         * 
         * @param distance
         *                the distance to be used by the control
         */
    public void setDistance( int distance) {

	m_distance = distance;

	if (m_dynamicPalette && (distance > 1)) { // recalculating the
	    // color
	    // palette if dynamic
	    m_colorAction.setColors(ColorLib.getInterpolatedPalette(distance,
		    m_startColor, m_endColor));
	}

    }

    /**
         * returns the distance used by the control
         * 
         * @return the ditance used yby this control
         */
    public int getDistance() {
	return m_distance;
    }

    /**
         * Indicates if neighbor nodes with edges currently not visible still
         * get highlighted.
         * 
         * @return true if neighbors with invisible edges still get highlighted,
         *         false otherwise.
         */
    public boolean isHighlightWithInvisibleEdge() {
	return m_highlightWithInvisibleEdge;
    }

    /**
         * Determines if neighbor nodes with edges currently not visible still
         * get highlighted.
         * 
         * @param highlightWithInvisibleEdge
         *                assign true if neighbors with invisible edges should
         *                still get highlighted, false otherwise.
         */
    public void setHighlightWithInvisibleEdge(
	     boolean highlightWithInvisibleEdge) {
	m_highlightWithInvisibleEdge = highlightWithInvisibleEdge;
    }

    private class HighlightColorAction extends ColorAction {

	int[] m_colors;

	public HighlightColorAction( int size, int[] colors) {
	    super(Graph.NODES, VisualItem.FILLCOLOR);

	    m_colors = colors;

	}

	public void setColors( int[] colors) {
	    m_colors = colors;
	}

	public int[] getColors() {
	    return m_colors;
	}

	public int getColor( VisualItem item) {
	    if (item == null) {
		return 0;
	    }
	    int moi = -1 * (int) item.getDOI() - 1;
	    if (item.isHighlighted() && (moi >= 0)) {
		moi = moi >= m_colors.length ? m_colors.length - 1 : moi;
		return m_colors[moi];
	    }

	    return 0;
	}

    }

} // end of class ExtendedNeighborHighlightControl
