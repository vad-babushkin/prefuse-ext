package profusians.render;

import java.awt.geom.Point2D;

import prefuse.Constants;
import prefuse.render.EdgeRenderer;
import prefuse.visual.EdgeItem;

/**
 * This edge renderer class draws curved edges less "wild" than the extended
 * EdgeRenderer. In addition it is symmetric, should mean x and y distances
 * between beginning and end point of the curve are treated equal (in difference
 * to the extend class which only takes x distance into account, loosly spoken)
 * An effect of this is that the edge is straight if x and y distances between
 * beginning and end point are equal. Smooth like the palms of an elephant ...
 * 
 * @author <a href="http://goosebumps4all.net">martin dudek</a>
 */

public class SmoothEdgeRenderer extends EdgeRenderer {

    /**
         * Determines the control points to use for cubic (Bezier) curve edges.
         * To reduce object initialization, the entries of the Point2D array are
         * already initialized, so use the <tt>Point2D.setLocation()</tt>
         * method rather than <tt>new Point2D.Double()</tt> to more
         * efficiently set custom control points.
         * 
         * @param eitem
         *                the EdgeItem we are determining the control points for
         * @param cp
         *                array of Point2D's (length >= 2) in which to return
         *                the control points
         * @param x1
         *                the x co-ordinate of the first node this edge connects
         *                to
         * @param y1
         *                the y co-ordinate of the first node this edge connects
         *                to
         * @param x2
         *                the x co-ordinate of the second node this edge
         *                connects to
         * @param y2
         *                the y co-ordinate of the second node this edge
         *                connects to
         */

    protected void getCurveControlPoints( EdgeItem eitem,
	     Point2D[] cp, double x1, double y1,
	     double x2, double y2) {
	 double dx = x2 - x1, dy = y2 - y1;

	 double c = Math.sqrt((dx * dx) + (dy * dy));

	 double radAngle = Math.acos(((Math.abs(dx)) / c));
	 double degAngle = radAngle * (180 / Math.PI);

	 double wx = getWeight(90 - degAngle);
	 double wy = getWeight(degAngle);

	if (eitem.isDirected() && (m_edgeArrow != Constants.EDGE_ARROW_NONE)) {
	    if (m_edgeArrow == Constants.EDGE_ARROW_FORWARD) {
		cp[0].setLocation(x1 + wx * 2 * dx / 3, y1 + wy * 2 * dy / 3);
		cp[1].setLocation(x2 - dx / 8, y2 - dy / 8);
	    } else {
		cp[0].setLocation(x1 + dx / 8, y1 + dy / 8);
		cp[1].setLocation(x2 - wx * 2 * dx / 3, y2 - wy * 2 * dy / 3);
	    }
	} else {
	    cp[0].setLocation(x1 + wx * 1 * dx / 3, y1 + wy * 1 * dy / 3);
	    cp[1].setLocation(x1 + wx * 2 * dx / 3, y1 + wy * 2 * dy / 3);
	}
    }

    private double getWeight( double x) {
	return 1 - Math.min(Math.abs(3 - x / 22.5), 1);
    }
}
