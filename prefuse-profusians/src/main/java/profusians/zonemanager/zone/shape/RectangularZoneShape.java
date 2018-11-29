package profusians.zonemanager.zone.shape;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;

import prefuse.Display;
import prefuse.util.ColorLib;
import prefuse.visual.NodeItem;
import profusians.zonemanager.zone.RectangularZone;

public class RectangularZoneShape extends DefaultZoneShape implements
	ZoneShape, Cloneable {

    float m_width;

    float m_height;

    float m_startWidth = 0;

    float m_startHeight = 0;

    float m_endWidth = 0;

    float m_endHeight = 0;

    public RectangularZoneShape( float x, float y,
	     float width, float height) {
	setCenterX(x);
	setCenterY(y);
	setWidth(width);
	setHeight(height);

    }

    public RectangularZoneShape( String xmlString) {
	 String[] values = xmlString.split(",");
	setCenterX(Float.valueOf(values[0].trim()).floatValue());
	setCenterY(Float.valueOf(values[1].trim()).floatValue());
	setWidth(Float.valueOf(values[2].trim()).floatValue());
	setHeight(Float.valueOf(values[3].trim()).floatValue());
    }

    public String getZoneType() {
	return "RECTANGULAR";
    }

    public Class getZoneClass() {
	return RectangularZone.class;
    }

    public boolean contains( NodeItem ni) {
	 double xDif = ni.getX() - getCenterX();
	 double yDif = ni.getY() - getCenterY();

	return ((Math.abs(xDif) <= getWidth() / 2) && (Math.abs(yDif) <= getHeight() / 2));
    }

    public boolean containsAtStart( NodeItem ni) {
	 double xDif = ni.getStartX() - getStartCenterX();
	 double yDif = ni.getStartY() - getStartCenterY();

	return ((Math.abs(xDif) <= getStartWidth() / 2) && (Math.abs(yDif) <= getStartHeight() / 2));

    }

    public boolean containsAtEnd( NodeItem ni) {
	 double xDif = ni.getEndX() - getEndCenterX();
	 double yDif = ni.getEndY() - getEndCenterY();

	return ((Math.abs(xDif) <= getEndWidth() / 2) && (Math.abs(yDif) <= getEndHeight() / 2));

    }

    public float getWidth() {
	return m_width;
    }

    public void setWidth( float w) {
	m_width = w;
    }

    public void setStartWidth( float w) {
	m_startWidth = w;
    }

    public void setEndWidth( float w) {
	m_endWidth = w;
    }

    public float getHeight() {
	return m_height;
    }

    public void setHeight( float h) {
	m_height = h;
    }

    public void setStartHeight( float h) {
	System.out.println(h);
	m_startHeight = h;
    }

    public void setEndHeight( float h) {
	m_endHeight = h;
    }

    public float getStartHeight() {
	return m_startHeight;
    }

    public float getEndHeight() {
	return m_endHeight;
    }

    public float getStartWidth() {
	return m_startWidth;
    }

    public float getEndWidth() {
	return m_endWidth;
    }

    public void updateStartValues() {
	super.updateStartValues();
	m_startWidth = m_width;
	m_startHeight = m_height;
    }

    public void updateEndValues() {
	super.updateEndValues();
	m_endWidth = m_width;
	m_endHeight = m_height;
    }

    public void animateShape( double frac) {
	super.animateShape(frac);
	m_width = (float) ((1 - frac) * m_startWidth + frac * m_endWidth);
	m_height = (float) ((1 - frac) * m_startHeight + frac * m_endHeight);
    }

    public void drawBorder( Display d, Graphics2D g,
	     int borderColor) {

	g.setColor(ColorLib.getColor(borderColor));
	 double s = d.getScale();
	g.drawRect((int) (s * (getCenterX() - getWidth() / 2) - (int) d
		.getDisplayX()),
		(int) (s * (getCenterY() - getHeight() / 2) - (int) d
			.getDisplayY()), (int) (s * getWidth()),
		(int) (s * getHeight()));
    }

    public Shape getRawShape( double x, double y) {
	 Rectangle2D rectangle = new Rectangle2D.Double();
	rectangle.setFrame(x, y, getWidth(), getHeight());
	return rectangle;

    }

    public Object clone() {
	return new RectangularZoneShape(getCenterX(), getCenterY(), getWidth(),
		getHeight());

    }

}
