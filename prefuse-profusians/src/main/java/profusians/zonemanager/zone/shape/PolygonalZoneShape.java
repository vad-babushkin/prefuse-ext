package profusians.zonemanager.zone.shape;

import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Shape;
import java.awt.geom.Point2D;

import prefuse.Display;
import prefuse.util.ColorLib;
import prefuse.visual.NodeItem;
import profusians.zonemanager.zone.PolygonalZone;

public class PolygonalZoneShape extends DefaultZoneShape implements ZoneShape,
	Cloneable {

    Polygon m_polygon;

    Polygon m_startPolygon;

    Polygon m_endPolygon;

    int m_numberOfPoints;

    public PolygonalZoneShape( int[] xpoints, int[] ypoints) {
	m_polygon = new Polygon(xpoints, ypoints, xpoints.length);
	m_numberOfPoints = xpoints.length;

	 Point2D.Float center = PolygonalZoneShape.getCentroid(m_polygon);
	super.setCenterX(center.x);
	super.setCenterY(center.y);

    }

    public PolygonalZoneShape( String xmlString) {
	 String[] values = xmlString.split(",");
	 int[] xpoints = new int[values.length / 2];
	 int[] ypoints = new int[values.length / 2];
	for (int i = 0; i < xpoints.length; i++) {
	    xpoints[i] = Integer.valueOf(values[i].trim()).intValue();
	    ypoints[i] = Integer.valueOf(values[i + xpoints.length].trim()).intValue();
	}

	m_polygon = new Polygon(xpoints, ypoints, xpoints.length);
	m_numberOfPoints = xpoints.length;

	 Point2D.Float center = PolygonalZoneShape.getCentroid(m_polygon);
	super.setCenterX(center.x);
	super.setCenterY(center.y);

    }

    public String getZoneType() {
	return "POLYGONAL";
    }

    public Class getZoneClass() {
	return PolygonalZone.class;
    }

    public boolean contains( NodeItem ni) {
	return m_polygon.contains(ni.getX(), ni.getY());
    }

    public boolean containsAtStart( NodeItem ni) {
	return m_startPolygon.contains(ni.getStartX(), ni.getStartY());
    }

    public boolean containsAtEnd( NodeItem ni) {
	return m_endPolygon.contains(ni.getEndX(), ni.getEndY());
    }

    public Polygon getPolygon() {
	return m_polygon;
    }

    public Polygon getStartPolygon() {
	return m_startPolygon;
    }

    public Polygon getEndPolygon() {
	return m_endPolygon;
    }

    public int getNumberOfPoints() {
	return m_numberOfPoints;
    }

    public void setPolygon( Polygon p) {
	m_polygon = p;
	m_numberOfPoints = m_polygon.npoints;
	 Point2D.Float center = PolygonalZoneShape.getCentroid(m_polygon);
	if (center != null) {
	    super.setCenterX(center.x);
	    super.setCenterY(center.y);
	}
    }

    public void setStartPolygon( Polygon p) {
	m_startPolygon = p;
	 Point2D.Float center = PolygonalZoneShape.getCentroid(p);
	if (center != null) {
	    setStartCenterX(center.x);
	    setStartCenterY(center.y);
	}
    }

    public void setEndPolygon( Polygon p) {
	m_endPolygon = p;
	 Point2D.Float center = PolygonalZoneShape.getCentroid(p);
	if (center != null) {
	    setEndCenterX(center.x);
	    setEndCenterY(center.y);
	}
    }

    /**
         * Sets the x position of the center of the polygon. The polygon is
         * recalculated according to the given new center x-position.
         * 
         * @param centerX
         *                the x position
         */
    public void setCenterX( float centerX) {
	PolygonalZoneShape.updatePolygon(m_polygon, centerX - getCenterX(),
		getCenterY());
	m_centerX = centerX;
    }

    /**
         * Sets the x position of the center of the start polygon. The polygon
         * is recalculated according to the given new center x-position.
         * 
         * @param startCenterX
         *                the x position
         */
    public void setStartCenterX( float startCenterX) {
	PolygonalZoneShape.updatePolygon(m_startPolygon, startCenterX
		- getStartCenterX(), getStartCenterY());
	m_startCenterX = startCenterX;
    }

    /**
         * Sets the x position of the center of the polygon. The polygon is
         * recalculated according to the given new center x-position.
         * 
         * @param endCenterX
         *                the x position
         */
    public void setEndCenterX( float endCenterX) {
	PolygonalZoneShape.updatePolygon(m_endPolygon, endCenterX
		- getEndCenterX(), getEndCenterY());
	m_endCenterX = endCenterX;

    }

    /**
         * Sets the y position of the center of the polygon. The polygon is
         * recalculated according to the given new center y-position.
         * 
         * @param centerY
         *                the Y position
         */

    public void setCenterY( float centerY) {
	PolygonalZoneShape.updatePolygon(m_polygon, getCenterX(), centerY
		- getCenterY());
	m_centerY = centerY;
    }

    /**
         * Sets the y position of the center of the start polygon. The start
         * polygon is recalculated according to the given new center y-position.
         * 
         * @param startCenterY
         *                the y position
         */

    public void setStartCenterY( float startCenterY) {
	PolygonalZoneShape.updatePolygon(m_startPolygon, getStartCenterX(),
		startCenterY - getStartCenterY());
	m_startCenterY = startCenterY;
    }

    /**
         * Sets the y position of the center of the end polygon. The end polygon
         * is recalculated according to the given new center y-position.
         * 
         * @param endCenterY
         *                the y position
         */
    public void setEndCenterY( float endCenterY) {
	PolygonalZoneShape.updatePolygon(m_endPolygon, getEndCenterX(),
		endCenterY - getEndCenterY());
	m_endCenterY = endCenterY;
    }

    public void updateStartValues() {
	super.updateStartValues();
	m_startPolygon = new Polygon(m_polygon.xpoints, m_polygon.ypoints,
		m_polygon.npoints);
    }

    public void updateEndValues() {
	super.updateEndValues();
	m_endPolygon = new Polygon(m_polygon.xpoints, m_polygon.ypoints,
		m_polygon.npoints);
    }

    public Shape getRawShape( double x, double y) {

	return m_polygon;
    }

    public void drawBorder( Display d, Graphics2D g,
	     int borderColor) {

	g.setColor(ColorLib.getColor(borderColor));
	g.drawPolygon(getScaledPolygon(m_polygon, d));

    }

    public void animateShape( double frac) {
	 int[] xpoints = new int[m_numberOfPoints];
	 int[] ypoints = new int[m_numberOfPoints];
	for (int i = 0; i < m_numberOfPoints; i++) {
	    xpoints[i] = (int) ((1 - frac) * m_startPolygon.xpoints[i] + frac
		    * m_endPolygon.xpoints[i]);
	    ypoints[i] = (int) ((1 - frac) * m_startPolygon.ypoints[i] + frac
		    * m_endPolygon.ypoints[i]);
	}
	setPolygon(new Polygon(xpoints, ypoints, m_numberOfPoints));

    }

    public Object clone() {
	return new PolygonalZoneShape(m_polygon.xpoints, m_polygon.ypoints);
    }

    static private Polygon getScaledPolygon( Polygon p, Display d) {

	 double scale = d.getScale();
	 int[] xpoints = new int[p.npoints];
	 int[] ypoints = new int[p.npoints];

	for (int i = 0; i < p.npoints; i++) {
	    xpoints[i] = (int) Math.round(p.xpoints[i] * scale
		    - d.getDisplayX());
	    ypoints[i] = (int) Math.round(p.ypoints[i] * scale
		    - d.getDisplayY());
	}
	return new Polygon(xpoints, ypoints, p.npoints);
    }

    static private Point2D.Float getCentroid( Polygon p) {
	double cx = 0.0f, cy = 0.0f;
	double factor;
	if (p.npoints <= 0) {
	    return null;
	}
	for (int i = 0, j = 0; i < p.npoints; i++) {
	    j = (i + 1) % p.npoints;
	    factor = (p.xpoints[i] * p.ypoints[j] - p.xpoints[j] * p.ypoints[i]);
	    cx += (p.xpoints[i] + p.xpoints[j]) * factor;
	    cy += (p.ypoints[i] + p.ypoints[j]) * factor;
	}
	 double area = getArea(p);
	if (area != 0) {
	    cx /= (6 * area);
	    cy /= (6 * area);
	}
	return new Point2D.Float((float) cx, (float) cy);
    }

    static private double getArea( Polygon p) {
	double sum = 0.0;
	for (int i = 0, j = 0; i < p.npoints; i++) {
	    j = (i + 1) % p.npoints;
	    sum = sum + (p.xpoints[i] * p.ypoints[j])
		    - (p.ypoints[i] * p.xpoints[j]);
	}
	return 0.5 * sum;
    }

    static private void updatePolygon(Polygon p, float diffCenterX,
	     float diffCenterY) {
	if ((diffCenterX == 0) && (diffCenterY == 0)) {
	    return;
	}
	 int[] xpoints = new int[p.npoints];
	 int[] ypoints = new int[p.npoints];

	for (int i = 0; i < p.npoints; i++) {
	    xpoints[i] = (int) (p.xpoints[i] + diffCenterX);
	    ypoints[i] = (int) (p.ypoints[i] + diffCenterY);
	}
	p = new Polygon(xpoints, ypoints, p.npoints);

    }

}
