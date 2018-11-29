package profusians.zonemanager.zone;

import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;

import prefuse.Visualization;
import prefuse.util.force.ForceSimulator;
import prefuse.visual.AggregateItem;
import prefuse.visual.NodeItem;
import profusians.util.force.PolygonWallForce;
import profusians.zonemanager.zone.attributes.ZoneAttributes;
import profusians.zonemanager.zone.colors.ZoneColors;
import profusians.zonemanager.zone.shape.PolygonalZoneShape;
import profusians.zonemanager.zone.shape.ZoneShape;

/**
 * Zone class representing a polygon zone.
 * 
 * @author <a href="http://goosebumps4all.net">martin dudek</a>
 * 
 */

public class PolygonalZone extends Zone {

    protected  static float DefaultGravConstant = -4.44f;

    public PolygonalZone() {

    }

    public PolygonalZone( Visualization vis, ForceSimulator fsim,
	     ZoneShape zoneShape) {
	this(vis, fsim, zoneShape, new ZoneColors(0, 0), new ZoneAttributes());
    }

    public PolygonalZone( Visualization vis, ForceSimulator fsim,
	     ZoneShape zoneShape, ZoneColors zoneColors) {
	this(vis, fsim, zoneShape, zoneColors, new ZoneAttributes());
    }

    public PolygonalZone( Visualization vis, ForceSimulator fsim,
	     ZoneShape zoneShape, ZoneAttributes zoneAttributes) {
	this(vis, fsim, zoneShape, new ZoneColors(0, 0), zoneAttributes);
    }

    public PolygonalZone( Visualization vis, ForceSimulator fsim,
	     ZoneShape zoneShape, ZoneColors zColors,
	     ZoneAttributes zoneAttributes) {
	super(vis, fsim, zoneShape, zColors, zoneAttributes);

    }

    public String getType() {
	return "POLYGONAL";
    }

    public void calculateForces() {
	m_forces[0] = new PolygonWallForce(getGravConstant(),
		((PolygonalZoneShape) m_zoneShape).getPolygon());
    }

    public void catchItem( NodeItem ni, boolean onlyIfOutside) {

	if (onlyIfOutside
		&& (isFlexible() ? m_zoneShape.containsAtEnd(ni) : contains(ni))) {
	    return;
	}

	/*
         * int posX = 0; int posY = 0;
         * 
         * for (int i=0;i<m_numberOfPoints;i++) { posX += getCenterX() + 0.5 *
         * Math.random() * (m_polygon.xpoints[i]-getCenterX()); posY +=
         * getCenterY() + 0.5 * Math.random() *
         * (m_cornerPoints[i].getY()-getCenterY()); } posX /= m_numberOfPoints;
         * posY /= m_numberOfPoints;
         * 
         * 
         * setX(ni,null,posX); setY(ni,null,posY);
         */

	 Polygon p = isFlexible() ? ((PolygonalZoneShape) m_zoneShape)
		.getEndPolygon() : ((PolygonalZoneShape) m_zoneShape)
		.getPolygon();

	 Rectangle2D bounds = p.getBounds2D();
	if (bounds != null) {

	    double x, y, sx, sy;
	     double upperCornerX = bounds.getCenterX() - bounds.getWidth()
		    / 2;
	     double upperCornerY = bounds.getCenterY()
		    - bounds.getHeight() / 2;
	    do {
		sx = Math.random();
		sy = Math.random();
		x = upperCornerX + sx * bounds.getWidth();
		y = upperCornerY + sy * bounds.getHeight();
	    } while (!p.contains(x, y));

	    setX(ni, null, x);
	    setY(ni, null, y);
	}

    }

    public Polygon getPolygon() {
	return ((PolygonalZoneShape) m_zoneShape).getPolygon();
    }

    public Polygon getInitalPolygon() {
	return ((PolygonalZoneShape) m_initialZoneShape).getPolygon();
    }

    public Polygon getEndPolygon() {
	return ((PolygonalZoneShape) m_zoneShape).getEndPolygon();
    }

    public Polygon getStartPolygon() {
	return ((PolygonalZoneShape) m_zoneShape).getStartPolygon();
    }

    public void updateZone() {
	if (isFlexible()) {

	    m_zoneShape.updateStartValues();
	    ((PolygonalZoneShape) m_zoneShape).setPolygon(getUpdatePolygon());
	    m_zoneShape.updateEndValues();
	}
    }

    public void setPolygon( Polygon p) {
	((PolygonalZoneShape) m_zoneShape).setPolygon(p);
    }

    public void setStartPolygon( Polygon p) {
	((PolygonalZoneShape) m_zoneShape).setStartPolygon(p);
    }

    public void setEndPolygon( Polygon p) {
	((PolygonalZoneShape) m_zoneShape).setEndPolygon(p);
    }

    public float getUpdateCenterX() {
	return getInitialCenterX();
    }

    public float getUpdateCenterY() {
	return getInitialCenterY();
    }

    public Polygon getUpdatePolygon() {
	 int numberOfPoints = ((PolygonalZoneShape) m_zoneShape)
		.getNumberOfPoints();
	 int[] updatedX = new int[numberOfPoints];
	 int[] updatedY = new int[numberOfPoints];

	for (int i = 0; i < numberOfPoints; i++) {
	    updatedX[i] = (int) (getUpdateCenterX() + Math
		    .sqrt(getNumberOfItems())
		    * (((PolygonalZoneShape) m_initialZoneShape).getPolygon().xpoints[i] - getInitialCenterX()));
	    updatedY[i] = (int) (getUpdateCenterY() + Math
		    .sqrt(getNumberOfItems())
		    * (((PolygonalZoneShape) m_initialZoneShape).getPolygon().ypoints[i] - getInitialCenterY()));
	}
	return new Polygon(updatedX, updatedY, numberOfPoints);
    }

    public void positionZoneAggregate( AggregateItem aAItem) {

	 Polygon p = getPolygon();
	if (p != null) {
	     Rectangle bounds = p.getBounds();

	    if (bounds != null) {
		setX(aAItem, null, bounds.getX());
		setY(aAItem, null, bounds.getY());
	    } else {
		setX(aAItem, null, 0);
		setY(aAItem, null, 0);
	    }
	}
    }

    public void animateZoneAggregate( AggregateItem aAItem,
	     double frac) {
	m_zoneShape.animateShape(frac);
	positionZoneAggregate(aAItem);
    }
}
