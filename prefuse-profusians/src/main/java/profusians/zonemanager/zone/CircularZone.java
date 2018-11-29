package profusians.zonemanager.zone;

import prefuse.Visualization;
import prefuse.util.force.CircularWallForce;
import prefuse.util.force.ForceSimulator;
import prefuse.visual.AggregateItem;
import prefuse.visual.NodeItem;
import profusians.zonemanager.zone.attributes.ZoneAttributes;
import profusians.zonemanager.zone.colors.ZoneColors;
import profusians.zonemanager.zone.shape.CircularZoneShape;
import profusians.zonemanager.zone.shape.ZoneShape;

/**
 * Zone class representing a circular zone
 * 
 * @author <a href="http://goosebumps4all.net">martin dudek</a>
 * 
 */
public class CircularZone extends Zone {

    protected static final float m_defaultGravConstant = -0.11f;

    public CircularZone() {

    }

    public CircularZone( Visualization vis, ForceSimulator fsim,
	     ZoneShape zoneShape) {
	this(vis, fsim, zoneShape, new ZoneColors(0, 0), new ZoneAttributes());
    }

    public CircularZone( Visualization vis, ForceSimulator fsim,
	     ZoneShape zoneShape, ZoneColors zoneColors) {
	this(vis, fsim, zoneShape, zoneColors, new ZoneAttributes());
    }

    public CircularZone( Visualization vis, ForceSimulator fsim,
	     ZoneShape zoneShape, ZoneAttributes zoneAttributes) {
	this(vis, fsim, zoneShape, new ZoneColors(0, 0), zoneAttributes);
    }

    public CircularZone( Visualization vis, ForceSimulator fsim,
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
	return "CIRCULAR";
    }

    public void calculateForces() {
	m_forces[0] = new CircularWallForce(getGravConstant(), m_zoneShape
		.getCenterX(), m_zoneShape.getCenterY(),
		((CircularZoneShape) m_zoneShape).getRadius());
    }

    public void catchItem( NodeItem ni, boolean onlyIfOutside) {

	if (onlyIfOutside
		&& (isFlexible() ? m_zoneShape.containsAtEnd(ni) : contains(ni))) {
	    return;
	}

	 float radius = isFlexible() ? ((CircularZoneShape) m_zoneShape)
		.getEndRadius() : getRadius();

	 float centerX = isFlexible() ? m_zoneShape.getEndCenterX()
		: getCenterX();
	 float centerY = isFlexible() ? m_zoneShape.getEndCenterY()
		: getCenterY();

	setX(ni, null, centerX + 0.5 * (Math.random() * radius * 2 - radius));

	setY(ni, null, centerY + 0.5 * (Math.random() * radius * 2 - radius));
    }

    public float getRadius() {
	return (int) ((CircularZoneShape) m_zoneShape).getRadius();
    }

    public int getInitialRadius() {
	return (int) ((CircularZoneShape) m_initialZoneShape).getRadius();
    }

    public void updateZone() {
	if (isFlexible()) {
	    m_zoneShape.updateStartValues();

	    ((CircularZoneShape) m_zoneShape).setRadius(getUpdateRadius());
	    m_zoneShape.setCenterX(getUpdateCenterX());
	    m_zoneShape.setCenterY(getUpdateCenterY());

	    m_zoneShape.updateEndValues();
	}
    }

    public int getUpdateRadius() {
	return Math.round((float) (getInitialRadius() * Math
		.sqrt(getNumberOfItems())));
    }

    public float getUpdateCenterX() {
	return getInitialCenterX();
    }

    public float getUpdateCenterY() {
	return getInitialCenterY();
    }

    public void positionZoneAggregate( AggregateItem aAItem) {

	setX(aAItem, null, getCenterX() - getRadius());
	setY(aAItem, null, getCenterY() - getRadius());

    }

    public void animateZoneAggregate( AggregateItem aAItem,
	     double frac) {
	m_zoneShape.animateShape(frac);
	positionZoneAggregate(aAItem);
    }

}
