package profusians.zonemanager.zone;

import prefuse.Visualization;
import prefuse.util.force.ForceSimulator;
import prefuse.visual.AggregateItem;
import prefuse.visual.NodeItem;
import profusians.util.force.RectangularWallForce;
import profusians.zonemanager.zone.attributes.ZoneAttributes;
import profusians.zonemanager.zone.colors.ZoneColors;
import profusians.zonemanager.zone.shape.RectangularZoneShape;
import profusians.zonemanager.zone.shape.ZoneShape;

/**
 * Zone class representing a rectangular zone
 * 
 * @author <a href="http://goosebumps4all.net">martin dudek</a>
 * 
 */

public class RectangularZone extends Zone {

    protected int width;

    protected int height;

    protected static float m_defaultGravConstant = -1.44f;

    public RectangularZone() {

    }

    public RectangularZone( Visualization vis, ForceSimulator fsim,
	     ZoneShape zoneShape) {
	this(vis, fsim, zoneShape, new ZoneColors(0, 0), new ZoneAttributes());
    }

    public RectangularZone( Visualization vis, ForceSimulator fsim,
	     ZoneShape zoneShape, ZoneColors zoneColors) {
	this(vis, fsim, zoneShape, zoneColors, new ZoneAttributes());
    }

    public RectangularZone( Visualization vis, ForceSimulator fsim,
	     ZoneShape zoneShape, ZoneAttributes zoneAttributes) {
	this(vis, fsim, zoneShape, new ZoneColors(0, 0), zoneAttributes);
    }

    public RectangularZone( Visualization vis, ForceSimulator fsim,
	     ZoneShape zoneShape, ZoneColors zColors,
	     ZoneAttributes zoneAttributes) {
	super(vis, fsim, zoneShape, zColors, zoneAttributes);
    }

    /**
         * Return the default gravitational constant used by the wall forces of
         * this zone
         * 
         * @return the default gravitational constant
         */

    public static float getDefaultGravConstant() {
	return m_defaultGravConstant;
    }

    public String getType() {
	return "RECTANGULAR";
    }

    public void calculateForces() {
	m_forces[0] = new RectangularWallForce(getGravConstant(), m_zoneShape
		.getCenterX(), m_zoneShape.getCenterY(),
		((RectangularZoneShape) m_zoneShape).getWidth(),
		((RectangularZoneShape) m_zoneShape).getHeight());
    }

    public void catchItem( NodeItem ni, boolean onlyIfOutside) {
	if (onlyIfOutside
		&& (isFlexible() ? m_zoneShape.containsAtEnd(ni) : contains(ni))) {
	    return;
	}

	if (isFlexible()) {
	    setX(ni, null, m_zoneShape.getEndCenterX() + (Math.random() - 0.5)
		    * ((RectangularZoneShape) m_zoneShape).getEndWidth() * 0.5);
	    setY(ni, null, m_zoneShape.getEndCenterY() + (Math.random() - 0.5)
		    * ((RectangularZoneShape) m_zoneShape).getEndHeight() * 0.5);
	} else {
	    setX(ni, null, m_zoneShape.getCenterX() + (Math.random() - 0.5)
		    * ((RectangularZoneShape) m_zoneShape).getWidth() * 0.5);
	    setY(ni, null, m_zoneShape.getCenterY() + (Math.random() - 0.5)
		    * ((RectangularZoneShape) m_zoneShape).getHeight() * 0.5);
	}

    }

    public int getWidth() {

	return (int) ((RectangularZoneShape) m_zoneShape).getWidth();
    }

    public int getHeight() {
	return (int) ((RectangularZoneShape) m_zoneShape).getHeight();

    }

    public int getInitialWidth() {

	return (int) ((RectangularZoneShape) m_initialZoneShape).getWidth();
    }

    public int getInitialHeight() {
	return (int) ((RectangularZoneShape) m_initialZoneShape).getHeight();

    }

    public void updateZone() {
	if (isFlexible()) {
	    m_zoneShape.updateStartValues();

	    ((RectangularZoneShape) m_zoneShape).setWidth(getUpdateWidth());
	    ((RectangularZoneShape) m_zoneShape).setHeight(getUpdateHeight());
	    m_zoneShape.setCenterX(getUpdateCenterX());
	    m_zoneShape.setCenterY(getUpdateCenterY());

	    m_zoneShape.updateEndValues();
	}
    }

    public float getUpdateCenterX() {
	return getInitialCenterX();
    }

    public float getUpdateCenterY() {
	return getInitialCenterY();
    }

    public int getUpdateWidth() {
	return Math.round((float) (getInitialWidth() * Math
		.sqrt(getNumberOfItems())));
    }

    public int getUpdateHeight() {
	return Math.round((float) (getInitialHeight() * Math
		.sqrt(getNumberOfItems())));
    }

    public void positionZoneAggregate( AggregateItem aAItem) {
	setX(aAItem, null, getCenterX() - getWidth() / 2);
	setY(aAItem, null, getCenterY() - getHeight() / 2);
    }

    public void animateZoneAggregate( AggregateItem aAItem,
	     double frac) {
	m_zoneShape.animateShape(frac);
	positionZoneAggregate(aAItem);
    }

}
