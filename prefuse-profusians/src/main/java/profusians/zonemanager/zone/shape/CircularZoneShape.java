package profusians.zonemanager.zone.shape;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;

import prefuse.Display;
import prefuse.util.ColorLib;
import prefuse.visual.NodeItem;
import profusians.zonemanager.zone.CircularZone;

public class CircularZoneShape extends DefaultZoneShape implements ZoneShape,
	Cloneable {

    float m_radius = 0;

    float m_startRadius = 0;

    float m_endRadius = 0;

    public CircularZoneShape( float centerX, float centerY,
	     float radius) {
	setCenterX(centerX);
	setCenterY(centerY);
	setRadius(radius);
    }

    public CircularZoneShape( String xmlString) {
	 String[] values = xmlString.split(",");
	setCenterX(Float.valueOf(values[0].trim()).floatValue());
	setCenterY(Float.valueOf(values[1].trim()).floatValue());
	setRadius(Float.valueOf(values[2].trim()).floatValue());

    }

    public String getZoneType() {
	return "CIRCULAR";
    }

    public Class getZoneClass() {
	return CircularZone.class;
    }

    public boolean contains( NodeItem ni) {
	 double xDif = ni.getX() - getCenterX();
	 double yDif = ni.getY() - getCenterY();

	return (Math.sqrt(xDif * xDif + yDif * yDif) <= getRadius());
    }

    public boolean containsAtStart( NodeItem ni) {
	 double xDif = ni.getStartX() - getStartCenterX();
	 double yDif = ni.getStartY() - getStartCenterY();

	return (Math.sqrt(xDif * xDif + yDif * yDif) <= getStartRadius());
    }

    public boolean containsAtEnd( NodeItem ni) {
	 double xDif = ni.getEndX() - getEndCenterX();
	 double yDif = ni.getEndY() - getEndCenterY();

	return (Math.sqrt(xDif * xDif + yDif * yDif) <= getEndRadius());
    }

    public float getRadius() {
	return m_radius;
    }

    public float getStartRadius() {
	return m_startRadius;
    }

    public float getEndRadius() {
	return m_endRadius;
    }

    public void updateStartValues() {
	super.updateStartValues();
	m_startRadius = m_radius;
    }

    public void updateEndValues() {
	super.updateEndValues();
	m_endRadius = m_radius;
    }

    public void setRadius( float radius) {
	m_radius = radius;
    }

    public void setStartRadius( float startRadius) {
	m_startRadius = startRadius;
    }

    public void setEndRadius( float endRadius) {
	m_endRadius = endRadius;
    }

    public void animateShape( double frac) {
	super.animateShape(frac);
	m_radius = (float) ((1 - frac) * m_startRadius + frac * m_endRadius);
    }

    public void drawBorder( Display d, Graphics2D g,
	     int borderColor) {

	 double s = d.getScale();

	g.setColor(ColorLib.getColor(borderColor));

	g
		.drawOval((int) (s * (getCenterX() - getRadius()) - (int) d
			.getDisplayX()),
			(int) (s * (getCenterY() - getRadius()) - (int) d
				.getDisplayY()), (int) (s * 2 * getRadius()),
			(int) (s * 2 * getRadius()));
    }

    public Shape getRawShape( double x, double y) {
	 Ellipse2D m_ellipse = new Ellipse2D.Double();
	m_ellipse.setFrame(x, y, 2 * getRadius(), 2 * getRadius());
	return m_ellipse;

    }

    public Object clone() {
	return new CircularZoneShape(getCenterX(), getCenterY(), getRadius());
    }

}
