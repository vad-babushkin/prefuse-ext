package profusians.util.force;

import java.awt.Polygon;
import java.awt.geom.Point2D;

import prefuse.util.force.AbstractForce;
import prefuse.util.force.ForceItem;
import prefuse.util.force.WallForce;

/**
 * Uses a gravitational force model to act as a polygon "box". This is achieved
 * by specifying n corner points, which are connected by n wall forces
 * constituting the polygon
 * 
 * Can be used to construct polygons which either attract or repel items.
 * 
 * @author <a href="http://goosebumps4all.net">martin dudek</a>
 */

public class PolygonWallForce extends AbstractForce {

    private static final String[] pnames = new String[] { "GravitationalConstant" };

    public static final float DEFAULT_GRAV_CONSTANT = -0.1f;

    public static final float DEFAULT_MIN_GRAV_CONSTANT = -1.0f;

    public static final float DEFAULT_MAX_GRAV_CONSTANT = 1.0f;

    public static final int GRAVITATIONAL_CONST = 0;

    private WallForce[] m_wallForces;

    private int m_numberOfPoints;

    public PolygonWallForce( Point2D.Float[] points) {
	this(DEFAULT_GRAV_CONSTANT, points);

    }

    public PolygonWallForce( Polygon polygon) {
	this(DEFAULT_GRAV_CONSTANT, polygonToCornerPoints(polygon));
    }

    public PolygonWallForce( float gravConst, Polygon polygon) {
	this(gravConst, polygonToCornerPoints(polygon));
    }

    public PolygonWallForce( float gravConst, Point2D.Float[] points) {
	params = new float[] { gravConst };
	minValues = new float[] { DEFAULT_MIN_GRAV_CONSTANT };
	maxValues = new float[] { DEFAULT_MAX_GRAV_CONSTANT };

	m_numberOfPoints = points.length;
	m_wallForces = new WallForce[m_numberOfPoints];

	for (int i = 0; i < m_numberOfPoints; i++) {
	    m_wallForces[i] = new WallForce(gravConst,
		    (float) points[i].getX(), (float) points[i].getY(),
		    (float) points[(i + 1) % m_numberOfPoints].getX(),
		    (float) points[(i + 1) % m_numberOfPoints].getY());
	}
    }

    public boolean isItemForce() {
	return true;
    }

    protected String[] getParameterNames() {
	return pnames;
    }

    public void getForce( ForceItem item) {
	for (int i = 0; i < m_numberOfPoints; i++) {
	    m_wallForces[i].getForce(item);
	}
    }

    private static Point2D.Float[] polygonToCornerPoints( Polygon polygon) {
	 Point2D.Float[] result = new Point2D.Float[polygon.npoints];
	for (int i = 0; i < polygon.npoints; i++) {
	    result[i] = new Point2D.Float(polygon.xpoints[i],
		    polygon.ypoints[i]);
	}
	return result;
    }

} // end of class PolygonWallForce
