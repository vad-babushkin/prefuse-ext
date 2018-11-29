package profusians.action.layout;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Iterator;

import prefuse.Constants;
import prefuse.action.layout.Layout;
import prefuse.visual.NodeItem;
import prefuse.visual.VisualItem;
import prefuse.visual.expression.StartVisiblePredicate;

/**
 * Modification of the CollapsedSubtreeLayout class from the prefuse library,
 * which can handle changes of the tree root.
 * 
 * @author <a href="http://jheer.org">jeffrey heer</a>
 * @author <a href="http://goosebumps4all.net">martin dudek</a>
 */

public class FlexibleTreeRootCollapsedSubtreeLayout extends Layout {

    private int m_orientation;

    private  Point2D m_point = new Point2D.Double();

    /**
         * Create a new CollapsedSubtreeLayoutFlexibleTreeRoot
         * 
         * @param group
         *                the data group to be layout
         */

    public FlexibleTreeRootCollapsedSubtreeLayout( String group) {
	this(group, Constants.ORIENT_CENTER);
    }

    /**
         * Create a new CollapsedSubtreeLayoutFlexibleTreeRoot
         * 
         * @param group
         *                the data group to be layout
         * @param orientation
         *                the orientatin of the layout
         */
    public FlexibleTreeRootCollapsedSubtreeLayout( String group,
	     int orientation) {
	super(group);
	m_orientation = orientation;
    }

    // ------------------------------------------------------------------------

    public int getOrientation() {
	return m_orientation;
    }

    public void setOrientation( int orientation) {
	if ((orientation < 0) || (orientation >= Constants.ORIENTATION_COUNT)) {
	    throw new IllegalArgumentException(
		    "Unrecognized orientation value: " + orientation);
	}
	m_orientation = orientation;
    }

    // ------------------------------------------------------------------------

    /**
         * @see prefuse.action.Action#run(double)
         */
    public void run( double frac) {
	// handle newly expanded subtrees - ensure they emerge from
	// a visible ancestor node or successor node

	NodeItem root = (NodeItem) m_vis.items(m_group,
		"ISNODE() AND VISIBLE()").next();

	while (root.getParent() != null) {
	     NodeItem parent = (NodeItem) root.getParent();
	    if (parent.isVisible()) {
		root = parent;
	    } else {
		break;
	    }
	}

	Iterator items = m_vis.visibleItems(m_group);
	while (items.hasNext()) {
	     VisualItem item = (VisualItem) items.next();
	    if ((item instanceof NodeItem) && !item.isStartVisible()) {
		 NodeItem n = (NodeItem) item;
		 Point2D p = getPoint(n, true, root);
		n.setStartX(p.getX());
		n.setStartY(p.getY());
	    }
	}

	// handle newly collapsed nodes - ensure they collapse to
	// the greatest visible ancestor node
	items = m_vis.items(m_group, StartVisiblePredicate.TRUE);
	while (items.hasNext()) {
	     VisualItem item = (VisualItem) items.next();
	    if ((item instanceof NodeItem) && !item.isEndVisible()) {
		 NodeItem n = (NodeItem) item;
		 Point2D p = getPoint(n, false, root);
		n.setStartX(n.getEndX());
		n.setStartY(n.getEndY());
		n.setEndX(p.getX());
		n.setEndY(p.getY());
	    }
	}
    }

    private Point2D getPoint( NodeItem n, boolean start,
	     NodeItem root) {
	// find the visible ancestor
	NodeItem p = (NodeItem) n.getParent();
	if (start) {
	    for (; (p != null) && !p.isStartVisible(); p = (NodeItem) p
		    .getParent()) {
		;
	    }
	} else {
	    for (; (p != null) && !p.isEndVisible(); p = (NodeItem) p
		    .getParent()) {
		;
	    }
	}
	if (p == null) {
	    m_point.setLocation(root.getX(), root.getY());
	    return m_point;
	}

	// get the vanishing/appearing point
	 double x = start ? p.getStartX() : p.getEndX();
	 double y = start ? p.getStartY() : p.getEndY();
	 Rectangle2D b = p.getBounds();
	switch (m_orientation) {
	case Constants.ORIENT_LEFT_RIGHT:
	    m_point.setLocation(x + b.getWidth(), y);
	    break;
	case Constants.ORIENT_RIGHT_LEFT:
	    m_point.setLocation(x - b.getWidth(), y);
	    break;
	case Constants.ORIENT_TOP_BOTTOM:
	    m_point.setLocation(x, y + b.getHeight());
	    break;
	case Constants.ORIENT_BOTTOM_TOP:
	    m_point.setLocation(x, y - b.getHeight());
	    break;
	case Constants.ORIENT_CENTER:
	    m_point.setLocation(x, y);
	    break;
	}
	return m_point;
    }

}
